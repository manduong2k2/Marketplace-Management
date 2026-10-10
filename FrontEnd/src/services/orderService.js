// services/orderService.js
import { request } from './apiService';

// ===== Order APIs =====
export const orderService = {
  // Tạo đơn hàng mới
  create: (orderData) =>
    request('/api/orders', {
      method: 'POST',
      body: orderData,
    }),

  // Lấy danh sách đơn hàng của user hiện tại
  getMyOrders: (params = {}) => {
    const queryString = new URLSearchParams(params).toString();
    return request(`/api/orders/me${queryString ? `?${queryString}` : ''}`);
  },

  // Lấy danh sách tất cả đơn hàng (Admin)
  getAllOrders: (params = {}) => {
    const queryString = new URLSearchParams(params).toString();
    return request(`/api/orders${queryString ? `?${queryString}` : ''}`);
  },

  // Lấy chi tiết đơn hàng
  getOrderById: (id) => request(`/api/orders/${id}`),
};
