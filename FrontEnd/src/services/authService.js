// services/authService.js
import { request } from './apiService';

// ===== Auth APIs =====
export const authService = {
  login: (data) => request('/api/auth/login', { method: 'POST', body: data }),
  // provider: 'google' (credential = ID token) | 'facebook' (credential = access token)
  oauthLogin: (provider, credential) =>
    request(`/api/auth/oauth/${provider}`, { method: 'POST', body: { credential } }),
  register: (data) => request('/api/auth/register', { method: 'POST', body: data }),
  refreshToken: (data) => request('/api/auth/refresh-token', { method: 'POST', body: data }),
  verifyEmail: (email, token) =>
    request(`/api/auth/verify-email?email=${encodeURIComponent(email)}&token=${encodeURIComponent(token)}`),
  forgotPassword: (email) => request('/api/auth/forgot-password', { method: 'POST', body: { email } }),
  resetPassword: (email, token, newPassword) =>
    request('/api/auth/reset-password', { method: 'POST', body: { email, token, newPassword } }),
  // Signs out every other device; the response sets new session cookies for this browser
  changePassword: (currentPassword, newPassword) =>
    request('/api/auth/change-password', { method: 'PUT', body: { currentPassword, newPassword } }),
  profile: () => request('/api/auth/profile'),
  updateProfile: (formData) => request('/api/auth/profile', { method: 'PUT', body: formData }),
  logout: () => request('/api/auth/logout', { method: 'POST' }),
};