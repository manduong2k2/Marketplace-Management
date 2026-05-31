// services/authService.js
import { API_URL } from '../configs/constants';

async function request(path, options = {}) {

  const { method = 'GET', body , headers = {} } = options;

  const isFormData = body instanceof FormData;

  const res = await fetch(
    `${API_URL}${path}`,
    {
      method,
      headers: {
        ...headers,
        ...(isFormData ? {} : { 'Content-Type': 'application/json' })
      },
      credentials: 'include',
      body: body ? (isFormData ? body : JSON.stringify(body)) : undefined,
    }
  );

  const contentType =
    res.headers.get('Content-Type') || '';

  let data;

  if (contentType.includes('application/json')) {
    data = await res.json();
  } else {
    data = await res.text();
  }

  return {
    ok: res.ok,
    status: res.status,
    headers: res.headers,
    data
  };
}

// ===== Auth APIs =====
export const authService = {
  login: (data) => request('/api/auth/login', { method: 'POST', body: data }),
  register: (data) => request('/api/auth/register', { method: 'POST', body: data }),
  refreshToken: (data) => request('/api/auth/refresh-token', { method: 'POST', body: data }),
  verifyEmail: (email, token) =>
    request(`/api/auth/verify-email?email=${encodeURIComponent(email)}&token=${encodeURIComponent(token)}`),
  forgotPassword: (email) => request('/api/auth/forgot-password', { method: 'POST', body: { email } }),
  resetPassword: (email, token, newPassword) =>
    request('/api/auth/reset-password', { method: 'POST', body: { email, token, newPassword } }),
  profile: () => request('/api/auth/profile'),
  updateProfile: (formData) => request('/api/auth/profile', { method: 'PUT', body: formData }),
  logout: () => request('/api/auth/logout', { method: 'POST' }),
};