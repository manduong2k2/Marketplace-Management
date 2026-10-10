// services/brandService.js
import { request } from './apiService';

// ===== Brand APIs =====
export const brandService = {
  // Lấy danh sách thương hiệu, có thể thêm params như page, filter
  getAll: () => {
    return request(`/api/brands`);
  },

  // Lấy chi tiết 1 thương hiệu
  getById: (id) => request(`/api/brands/${id}`),

  // Tạo thương hiệu mới
  create: (data) => request('/api/brands', { method: 'POST', body: data }),

  // Cập nhật thương hiệu
  update: (id, data) => request(`/api/brands/${id}`, { method: 'PUT', body: data }),

  // Xóa thương hiệu
  delete: (id) => request(`/api/brands/${id}`, { method: 'DELETE' }),

  // Lấy thương hiệu theo trạng thái
  getByStatus: (status) => request(`/api/brands/status/${status}`),

  // Tìm kiếm thương hiệu
  search: (keyword) => request(`/api/brands/search?q=${encodeURIComponent(keyword)}`),

  // Lấy số lượng sản phẩm trong thương hiệu
  getProductCount: (brandId) => request(`/api/brands/${brandId}/products/count`),
};
