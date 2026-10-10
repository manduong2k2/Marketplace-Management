// services/adminAuthService.js
import { request } from './apiService';

export const adminAuthService = {
  login: (data) => request('/api/auth/admin/login', { method: 'POST', body: data }),
  logout: () => request('/api/auth/logout', { method: 'POST' }),
  profile: () => request('/api/auth/profile'),
};
