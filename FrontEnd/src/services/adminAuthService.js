// services/adminAuthService.js
import { API_URL } from '../configs/constants';

async function request(path, options = {}) {
  const { method = 'GET', body, headers = {} } = options;
  const isFormData = body instanceof FormData;

  const res = await fetch(`${API_URL}${path}`, {
    method,
    headers: {
      ...headers,
      ...(isFormData ? {} : { 'Content-Type': 'application/json' }),
    },
    credentials: 'include',
    body: body ? (isFormData ? body : JSON.stringify(body)) : undefined,
  });

  const contentType = res.headers.get('Content-Type') || '';
  let data;
  if (contentType.includes('application/json')) {
    data = await res.json();
  } else {
    data = await res.text();
  }

  return { ok: res.ok, status: res.status, data };
}

export const adminAuthService = {
  login: (data) => request('/api/auth/admin/login', { method: 'POST', body: data }),
  logout: () => request('/api/auth/logout', { method: 'POST' }),
  profile: () => request('/api/auth/profile'),
};
