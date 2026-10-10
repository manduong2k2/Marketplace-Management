// src/services/addressService.js
import { fetch } from './apiService';

// An address = ward (belongs to a province) + detail (house number, street...) + isDefault.
// Responses: { id, detail, isDefault, ward: {id, name, fullName}, province: {...}, fullAddress }
export const addressService = {
  /** GET /api/addresses/provinces — [{ id, name, fullName }] (public) */
  getProvinces: () => fetch('/api/addresses/provinces'),

  /** GET /api/addresses/provinces/:provinceId/wards — [{ id, name, fullName }] (public) */
  getWards: (provinceId) => fetch(`/api/addresses/provinces/${encodeURIComponent(provinceId)}/wards`),

  /** GET /api/addresses/mine — default first, then newest */
  getMyAddresses: () => fetch('/api/addresses/mine'),

  /** GET /api/addresses/default — 404 when the user has no address yet */
  getDefaultAddress: () => fetch('/api/addresses/default', { silent: true }),

  getAddressById: (addressId) => fetch(`/api/addresses/${addressId}`),

  /** data: { wardId, detail, isDefault? } — the first address always becomes the default */
  createAddress: (data) => fetch('/api/addresses', { method: 'POST', body: data }),

  /** data: { wardId, detail, isDefault? } */
  updateAddress: (addressId, data) => fetch(`/api/addresses/${addressId}`, { method: 'PUT', body: data }),

  setDefaultAddress: (addressId) => fetch(`/api/addresses/${addressId}/default`, { method: 'PATCH' }),

  /** Deleting the default promotes the most recent remaining address */
  deleteAddress: (addressId) => fetch(`/api/addresses/${addressId}`, { method: 'DELETE' }),
};
