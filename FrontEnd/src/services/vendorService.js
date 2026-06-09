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
  // RegisterVendorCommand — multipart/form-data
  register: (formData) =>
    requestMultipart('/api/vendors/me', { method: 'POST', body: formData }),

  getById: (id) => request(`/api/vendors/${id}`),
  getMyVendor: () => request('/api/vendors/me'),
  getAll: () => request('/api/vendors'),
};
