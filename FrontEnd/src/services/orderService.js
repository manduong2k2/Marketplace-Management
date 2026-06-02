// services/orderService.js
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
