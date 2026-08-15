// src/services/addressService.js
import { fetch } from './apiService';

export const addressService = {
  /**
   * GET /api/addresses/default
   * Lấy địa chỉ mặc định của user hiện tại
   */
  getDefaultAddress: () => fetch('/api/addresses/default'),

  /**
   * GET /api/addresses/mine
   * Lấy danh sách địa chỉ của user hiện tại
   * @param {Object} params - { search, province, ward, isDefault }
   */
  getMyAddresses: (params = {}) => {
    const query = new URLSearchParams();
    if (params.search)    query.append('search', params.search);
    if (params.province)  query.append('province', params.province);
    if (params.ward)      query.append('ward', params.ward);
    if (params.isDefault !== undefined) query.append('isDefault', params.isDefault);
    const qs = query.toString();
    return fetch(`/api/addresses/mine${qs ? `?${qs}` : ''}`);
  },

  /**
   * GET /api/addresses/:addressId
   * Lấy chi tiết một địa chỉ
   * @param {number} addressId
   */
  getAddressById: (addressId) => fetch(`/api/addresses/${addressId}`),

  /**
   * POST /api/addresses
   * Tạo địa chỉ mới
   * @param {Object} data - { title, streetName, houseNumber, detail?, wardId, isDefault? }
   */
  createAddress: (data) => fetch('/api/addresses', { method: 'POST', body: data }),

  /**
   * PUT /api/addresses/:addressId
   * Cập nhật địa chỉ
   * @param {number} addressId
   * @param {Object} data - { streetName, houseNumber, title?, detail?, wardId, isDefault }
   */
  updateAddress: (addressId, data) =>
    fetch(`/api/addresses/${addressId}`, { method: 'PUT', body: data }),

  /**
   * DELETE /api/addresses/:addressId
   * Xóa địa chỉ
   * @param {number} addressId
   */
  deleteAddress: (addressId) =>
    fetch(`/api/addresses/${addressId}`, { method: 'DELETE' }),

  /**
   * GET /api/addresses/master-regions
   * Lấy danh sách tỉnh/thành phố (không cần auth)
   */
  getMasterRegions: () => fetch('/api/addresses/master-regions'),
};
