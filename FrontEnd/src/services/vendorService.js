// services/vendorService.js
import { API_URL } from '../configs/constants';

// multipart/form-data request (for file uploads)
async function requestMultipart(path, options = {}) {
  const { method = 'POST', body } = options;

  const res = await fetch(
    `${API_URL}${path}`,
    {
      method,
      // Do NOT set Content-Type — browser sets it automatically with boundary
      credentials: 'include',
      body,
    }
  );

  const contentType = res.headers.get('Content-Type') || '';
  let data;
  if (contentType.includes('application/json')) {
    data = await res.json();
  } else {
    data = await res.text();
  }

  return { ok: res.ok, status: res.status, data };
}

// JSON request
async function request(path, options = {}) {
  const { method = 'GET', body } = options;

  const res = await fetch(
    `${API_URL}${path}`,
    {
      method,
      headers: { 'Content-Type': 'application/json' },
      credentials: 'include',
      body: body ? JSON.stringify(body) : undefined,
    }
  );

  const contentType = res.headers.get('Content-Type') || '';
  let data;
  if (contentType.includes('application/json')) {
    data = await res.json();
  } else {
    data = await res.text();
  }

  return { ok: res.ok, status: res.status, data };
}

export const vendorService = {
  // ── Vendor registration (multipart/form-data) ────────────────────────────
  register: (formData) =>
    requestMultipart('/api/vendors/me', { method: 'POST', body: formData }),

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
    requestMultipart('/api/vendors/me', { method: 'PUT', body: formData }),

  // ── PATCH (partial update) ────────────────────────────────────────────────
  patch: (id, data) => request(`/api/vendors/${id}`, { method: 'PATCH', body: data }),

  // Approve / reject / suspend a vendor (common admin actions)
  approve: (id) => request(`/api/vendors/${id}/approve`, { method: 'PATCH' }),

  reject: (id, reason) =>
    request(`/api/vendors/${id}/reject`, { method: 'PATCH', body: { reason } }),

  suspend: (id, reason) =>
    request(`/api/vendors/${id}/suspend`, { method: 'PATCH', body: { reason } }),

  // ── DELETE ───────────────────────────────────────────────────────────────
  delete: (id) => request(`/api/vendors/${id}`, { method: 'DELETE' }),
};
