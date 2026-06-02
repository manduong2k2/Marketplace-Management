// services/productService.js
import { API_URL } from '../configs/constants';

async function request(path, options = {}) {
  const { method = 'GET', body } = options;

  const res = await fetch(`${API_URL}${path}`, {
    method,
    headers: { 'Content-Type': 'application/json' },
    credentials: 'include',
    body: body ? JSON.stringify(body) : undefined,
  });

  const contentType = res.headers.get('Content-Type') || '';
  let data;
  if (contentType.includes('application/json')) {
    data = await res.json();
  } else {
    data = await res.text();
  }

  return { ok: res.ok, status: res.status, headers: res.headers, data };
}

// ===== Product APIs =====
export const productService = {
  getAll: (params = {}) => {
    const query = new URLSearchParams(params).toString();
    return request(`/api/products${query ? `?${query}` : ''}`);
  },

  getById: (id) => request(`/api/products/${id}`),

  getStatuses: () => request('/api/products/statuses'),

  create: (data) => request('/api/products', { method: 'POST', body: data }),

  update: (id, data) => request(`/api/products/${id}`, { method: 'PUT', body: data }),

  delete: (id) => request(`/api/products/${id}`, { method: 'DELETE' }),
};
