// services/vendorService.js
import { request } from './apiService';

export const vendorService = {
  // ── Vendor registration (multipart/form-data) ────────────────────────────
  register: (formData) =>
    request('/api/vendors/me', { method: 'POST', body: formData }),

  // ── GET ──────────────────────────────────────────────────────────────────
  getAll: (params) => {
    const query = params ? '?' + new URLSearchParams(params).toString() : '';
    return request(`/api/vendors${query}`);
  },

  getById: (id) => request(`/api/vendors/${id}`),

  getMyVendor: () => request('/api/vendors/me'),

  search: (keyword) => request(`/api/vendors/search?q=${encodeURIComponent(keyword)}`),

  // ── POST ─────────────────────────────────────────────────────────────────
  create: (data) => request('/api/vendors', { method: 'POST', body: data }),

  // ── PUT (full update) ─────────────────────────────────────────────────────
  update: (id, data) => request(`/api/vendors/${id}`, { method: 'PUT', body: data }),

  updateMyVendor: (data) => request('/api/vendors/me', { method: 'PUT', body: data }),

  updateMyVendorMultipart: (formData) =>
    request('/api/vendors/me', { method: 'PUT', body: formData }),

  // ── PATCH (partial update) ────────────────────────────────────────────────
  patch: (id, data) => request(`/api/vendors/${id}`, { method: 'PATCH', body: data }),

  // Approve / reject / suspend a vendor (common admin actions)
  // Admin: activates a pending vendor
  approve: (id) => request(`/api/vendors/${id}/activate`, { method: 'POST' }),

  reject: (id, reason) =>
    request(`/api/vendors/${id}/reject`, { method: 'PATCH', body: { reason } }),

  suspend: (id, reason) =>
    request(`/api/vendors/${id}/suspend`, { method: 'PATCH', body: { reason } }),

  // ── DELETE ───────────────────────────────────────────────────────────────
  delete: (id) => request(`/api/vendors/${id}`, { method: 'DELETE' }),
};
