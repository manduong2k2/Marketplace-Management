// services/cartService.js
import { API_URL } from '../configs/constants';

async function request(path, options = {}) {
  const { method = 'GET', body } = options;

  const res = await fetch(
    `${API_URL}${path}`,
    {
      method,
      headers: {
        'Content-Type': 'application/json',
      },
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

  return {
    ok: res.ok,
    status: res.status,
    headers: res.headers,
    data,
  };
}

// ===== Cart APIs =====
export const cartService = {
  // Lấy giỏ hàng của user hiện tại
  getCart: () => request('/api/cart'),

  // Thêm sản phẩm vào giỏ hàng
  addItem: (productId, quantity = 1) =>
    request('/api/cart/items', {
      method: 'POST',
      body: { productId, quantity },
    }),

  // Cập nhật số lượng sản phẩm trong giỏ
  updateItem: (productId, quantity) =>
    request(`/api/cart/items/${productId}`, {
      method: 'PUT',
      body: { quantity },
    }),

  // Xóa 1 sản phẩm khỏi giỏ
  removeItem: (productId) =>
    request(`/api/cart/items/${productId}`, { method: 'DELETE' }),

  // Xóa toàn bộ giỏ hàng
  clearCart: () => request('/api/cart', { method: 'DELETE' }),
};
