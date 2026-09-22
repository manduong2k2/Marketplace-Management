// services/productService.js
import { apiService } from './apiService';

// ===== Product APIs =====
export const productService = {
  getAll: (params = {}) => {
    const query = new URLSearchParams(params).toString();
    return apiService.fetch(`/api/products${query ? `?${query}` : ''}`);
  },

  getById: (id) => apiService.fetch(`/api/products/${id}`),

  getStatuses: () => apiService.fetch('/api/products/statuses'),

  create: (data) => apiService.fetch('/api/products', { method: 'POST', body: data }),

  update: (id, data) => apiService.fetch(`/api/products/${id}`, { method: 'PUT', body: data }),

  delete: (id) => apiService.fetch(`/api/products/${id}`, { method: 'DELETE' }),
};
