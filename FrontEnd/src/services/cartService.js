// services/cartService.js
import { request } from './apiService';

// ===== Cart APIs =====
export const cartService = {
  // Lấy giỏ hàng của user hiện tại
  getCart: () => request('/api/cart'),

  // Thêm sản phẩm vào giỏ hàng
  addItem: (productVariantId, quantity = 1) =>
    request('/api/cart/items', {
      method: 'POST',
      body: { productVariantId, quantity },
    }),

  // Cập nhật số lượng sản phẩm trong giỏ
  updateItem: (productVariantId, quantity) =>
    request(`/api/cart/items/${productVariantId}`, {
      method: 'PUT',
      body: { quantity },
    }),

  // Xóa 1 sản phẩm khỏi giỏ
  removeItem: (productVariantId) =>
    request(`/api/cart/items/${productVariantId}`, { method: 'DELETE' }),

  // Xóa toàn bộ giỏ hàng
  clearCart: () => request('/api/cart', { method: 'DELETE' }),
};
