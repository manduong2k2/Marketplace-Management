// services/authInterceptor.js
//
// Wraps window.fetch once, so EVERY call to the backend (services, apiService, raw fetch in pages)
// gets the same 401 handling:
//   1. request -> 401
//   2. call /api/auth/refresh-token once (concurrent 401s share the same refresh)
//      - refresh fails  -> redirect to login
//      - refresh works  -> retry the original request once
//   3. retry -> still 401 -> redirect to login
//
// Guests also get 401 (e.g. /api/auth/profile on page load). They have no session to refresh,
// so the flow only runs when this browser has signed in (session flag below); otherwise the
// 401 is returned as-is, like before.
import { API_URL } from '../configs/constants';

const SESSION_KEY = 'auth.hasSession';

// Fired after a successful refresh: the new access token may carry different roles,
// so contexts holding the profile (AuthContext, AdminContext) reload it.
export const AUTH_REFRESHED_EVENT = 'auth:refreshed';

// Endpoints that must never trigger a refresh (they *are* the auth flow)
const SKIP_REFRESH = [
  '/api/auth/refresh-token',
  '/api/auth/login',
  '/api/auth/admin/login',
  '/api/auth/google',
  '/api/auth/register',
  '/api/auth/logout',
];

// A successful response from these means the browser now has a session
const SESSION_START = ['/api/auth/login', '/api/auth/admin/login', '/api/auth/google', '/api/auth/profile'];

const storage = {
  has: () => { try { return localStorage.getItem(SESSION_KEY) === '1'; } catch { return false; } },
  set: () => { try { localStorage.setItem(SESSION_KEY, '1'); } catch { /* storage unavailable */ } },
  clear: () => { try { localStorage.removeItem(SESSION_KEY); } catch { /* storage unavailable */ } },
};

const pathOf = (input) => {
  const url = typeof input === 'string' ? input : input instanceof Request ? input.url : String(input);
  if (!API_URL || !url.startsWith(API_URL)) return null;   // not a backend call
  return new URL(url, window.location.origin).pathname;
};

const matches = (path, list) => list.some(prefix => path === prefix || path.startsWith(`${prefix}/`));

function redirectToLogin() {
  storage.clear();
  const isAdmin = window.location.pathname.startsWith('/admin');
  const target = isAdmin ? '/admin/login' : '/auth?action=login';
  // Already on a login page: nothing to do
  if (window.location.pathname === '/admin/login' || window.location.pathname === '/auth') return;
  window.location.assign(target);
}

export function installAuthInterceptor() {
  if (window.__authInterceptorInstalled) return;
  window.__authInterceptorInstalled = true;

  const originalFetch = window.fetch.bind(window);
  let refreshing = null;   // shared promise: one refresh for many concurrent 401s

  const refreshOnce = () => {
    if (!refreshing) {
      refreshing = (async () => {
        try {
          const res = await originalFetch(`${API_URL}/api/auth/refresh-token`, {
            method: 'POST',
            credentials: 'include',   // the refresh token is an httpOnly cookie
          });
          if (res.ok) window.dispatchEvent(new Event(AUTH_REFRESHED_EVENT));
          return res.ok;
        } catch {
          return false;               // network error = refresh failed
        } finally {
          // Allow a new refresh for 401s that happen later
          setTimeout(() => { refreshing = null; }, 0);
        }
      })();
    }
    return refreshing;
  };

  window.fetch = async (input, init) => {
    const path = pathOf(input);
    if (!path) return originalFetch(input, init);

    // A Request body can only be read once: keep a copy for the retry
    const retryInput = input instanceof Request ? input.clone() : input;
    const response = await originalFetch(input, init);

    if (response.ok && matches(path, SESSION_START)) storage.set();
    if (matches(path, ['/api/auth/logout'])) storage.clear();

    if (response.status !== 401 || matches(path, SKIP_REFRESH) || !storage.has()) {
      return response;
    }

    // 401 with a session: refresh once, then retry once
    const refreshed = await refreshOnce();
    if (!refreshed) {
      redirectToLogin();
      return response;
    }

    const retry = await originalFetch(retryInput, init);
    if (retry.status === 401) redirectToLogin();
    return retry;
  };
}
