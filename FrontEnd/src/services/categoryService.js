// services/categoryService.js
import { request } from './apiService';

// ===== Category APIs =====
export const categoryService = {
  // Lấy danh sách danh mục, có thể thêm params như page, filter
  getAll: () => {
    return request(`/api/categories`);
  },

  // Lấy chi tiết 1 danh mục
  getById: (id) => request(`/api/categories/${id}`),

  // Tạo danh mục mới
  create: (data) => request('/api/categories', { method: 'POST', body: data }),

  // Cập nhật danh mục
  update: (id, data) => request(`/api/categories/${id}`, { method: 'PUT', body: data }),

  // Xóa danh mục
  delete: (id) => request(`/api/categories/${id}`, { method: 'DELETE' }),

  // Lấy danh mục theo trạng thái
  getByStatus: (status) => request(`/api/categories/status/${status}`),

  // Tìm kiếm danh mục
  search: (keyword) => request(`/api/categories/search?q=${encodeURIComponent(keyword)}`),

  // Lấy số lượng sản phẩm trong danh mục
  getProductCount: (categoryId) => request(`/api/categories/${categoryId}/products/count`),
};