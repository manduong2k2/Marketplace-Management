// src/services/apiService.js
import { API_URL } from '../configs/constants';

/**
 * Extract error message from backend response body.
 *
 * Backend returns two shapes:
 *  - 4xx: { message: "..." }
 *  - 5xx: { status, message, type, cause, ... }  (ApiError)
 */
function extractMessage(data) {
  if (!data || typeof data !== 'object') return null;
  return data.message || null;
}

/**
 * Central fetch wrapper.
 *
 * - Attaches Content-Type: application/json automatically (skipped for FormData).
 * - Sends cookies (credentials: 'include').
 * - On non-2xx responses, if the body contains a `message` field, calls window.showError().
 * - Always returns { ok, status, data } so callers can still handle errors themselves.
 *
 * @param {string} path  - e.g. '/api/addresses/mine'
 * @param {object} options
 * @param {string}  [options.method='GET']
 * @param {object|FormData} [options.body]
 * @param {object}  [options.headers={}]
 * @param {boolean} [options.silent=false] - pass true to suppress automatic error popups
 */
export async function fetch(path, options = {}) {
  const { method = 'GET', body, headers = {}, silent = false } = options;

  const isFormData = body instanceof FormData;

  const res = await globalThis.fetch(`${API_URL}${path}`, {
    method,
    headers: {
      ...(!isFormData && { 'Content-Type': 'application/json' }),
      ...headers,
    },
    credentials: 'include',
    body: body
      ? isFormData
        ? body
        : JSON.stringify(body)
      : undefined,
  });

  const contentType = res.headers.get('Content-Type') || '';
  let data;
  if (contentType.includes('application/json')) {
    data = await res.json();
  } else {
    data = await res.text();
  }

  // Auto-show error popup when response is not ok and body has a message
  if (!res.ok && !silent) {
    const message = extractMessage(data);
    if (message && window.showError) {
      window.showError(message, 'Error');
    }
  }

  return { ok: res.ok, status: res.status, data };
}

export const apiService = { fetch };
