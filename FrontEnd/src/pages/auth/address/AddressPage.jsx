// src/pages/auth/address/AddressPage.jsx
import { useState, useEffect } from 'react';
import { addressService } from '../../../services/addressService';
import { showSuccess, showError } from '../../../components/master/popup';
import './AddressPage.css';

const EMPTY_FORM = {
  title: '',
  streetName: '',
  houseNumber: '',
  detail: '',
  wardId: '',
  isDefault: false,
};

export default function AddressPage() {
  useEffect(() => {
    document.title = 'My Store - My Addresses';
  }, []);

  const [addresses, setAddresses] = useState([]);
  const [provinces, setProvinces] = useState([]);
  const [loading, setLoading] = useState(true);

  // Modal state
  const [showModal, setShowModal] = useState(false);
  const [editingId, setEditingId] = useState(null); // null = create mode
  const [formData, setFormData] = useState(EMPTY_FORM);
  const [formLoading, setFormLoading] = useState(false);

  // Province/ward cascade select
  const [selectedProvinceId, setSelectedProvinceId] = useState('');

  // Delete confirm
  const [deleteId, setDeleteId] = useState(null);
  const [deleteLoading, setDeleteLoading] = useState(false);

  // ── Load addresses & provinces ──
  useEffect(() => {
    fetchAddresses();
    fetchProvinces();
  }, []);

  const fetchAddresses = async () => {
    setLoading(true);
    try {
      const res = await addressService.getMyAddresses();
      if (res.ok) setAddresses(res.data.data || []);
      else showError('Failed to load addresses', 'Error');
    } catch {
      showError('An error occurred while loading addresses', 'Error');
    } finally {
      setLoading(false);
    }
  };

  const fetchProvinces = async () => {
    try {
      const res = await addressService.getMasterRegions();
      if (res.ok) setProvinces(res.data.data || []);
    } catch {
      // silently fail
    }
  };

  // ── Helpers ──
  const formatAddress = (addr) => {
    const parts = [addr.houseNumber, addr.streetName];
    if (addr.ward) {
      const ward = typeof addr.ward === 'object' ? (addr.ward.fullName || addr.ward.name) : addr.ward;
      parts.push(ward);
    }
    if (addr.province) {
      const prov = typeof addr.province === 'object' ? (addr.province.name || addr.province.fullName) : addr.province;
      parts.push(prov);
    }
    if (addr.detail) parts.push(addr.detail);
    return parts.filter(Boolean).join(', ');
  };

  // ── Modal open/close ──
  const openCreateModal = () => {
    setEditingId(null);
    setFormData(EMPTY_FORM);
    setSelectedProvinceId('');
    setShowModal(true);
  };

  const openEditModal = async (addr) => {
    setEditingId(addr.id);
    try {
      const res = await addressService.getAddressById(addr.id);
      if (res.ok) {
        const d = res.data.data;
        const wardId = d.ward?.id || '';
        const province = provinces.find((p) =>
          (p.wards || []).some((w) => w.id === wardId)
        );
        setSelectedProvinceId(province?.id || '');
        setFormData({
          title: d.title || '',
          streetName: d.streetName || '',
          houseNumber: d.houseNumber || '',
          detail: d.detail || '',
          wardId,
          isDefault: d.isDefault || false,
        });
      } else {
        setSelectedProvinceId('');
        setFormData({
          title: addr.title || '',
          streetName: addr.streetName || '',
          houseNumber: addr.houseNumber || '',
          detail: addr.detail || '',
          wardId: '',
          isDefault: addr.isDefault || false,
        });
      }
    } catch {
      setSelectedProvinceId('');
      setFormData({
        title: addr.title || '',
        streetName: addr.streetName || '',
        houseNumber: addr.houseNumber || '',
        detail: addr.detail || '',
        wardId: '',
        isDefault: addr.isDefault || false,
      });
    }
    setShowModal(true);
  };

  const closeModal = () => {
    setShowModal(false);
    setEditingId(null);
    setFormData(EMPTY_FORM);
    setSelectedProvinceId('');
  };

  // ── Form handlers ──
  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value,
    }));
  };

  const handleProvinceChange = (e) => {
    setSelectedProvinceId(e.target.value);
    setFormData((prev) => ({ ...prev, wardId: '' }));
  };

  const availableWards =
    provinces.find((p) => p.id === selectedProvinceId)?.wards || [];

  const handleSubmit = async (e) => {
    e.preventDefault();
    setFormLoading(true);
    try {
      let res;
      if (editingId) {
        res = await addressService.updateAddress(editingId, formData);
      } else {
        res = await addressService.createAddress(formData);
      }

      if (res.ok) {
        showSuccess(editingId ? 'Address updated successfully' : 'Address added successfully', 'Success');
        closeModal();
        fetchAddresses();
      } else {
        const msg = res.data?.message || 'Something went wrong';
        showError(msg, 'Error');
      }
    } catch {
      showError('Something went wrong, please try again', 'Error');
    } finally {
      setFormLoading(false);
    }
  };

  // ── Delete ──
  const handleDeleteConfirm = async () => {
    if (!deleteId) return;
    setDeleteLoading(true);
    try {
      const res = await addressService.deleteAddress(deleteId);
      if (res.ok) {
        showSuccess('Address deleted successfully', 'Success');
        setDeleteId(null);
        fetchAddresses();
      } else {
        showError('Failed to delete address', 'Error');
      }
    } catch {
      showError('An error occurred while deleting the address', 'Error');
    } finally {
      setDeleteLoading(false);
    }
  };

  return (
    <div className="address-page">
      <div className="address-header">
        <h2>My Addresses</h2>
        <button className="btn-add-address" onClick={openCreateModal}>
          + Add New Address
        </button>
      </div>

      {loading ? (
        <div className="address-loading">Loading...</div>
      ) : addresses.length === 0 ? (
        <div className="address-empty">
          <p>You have no saved addresses.</p>
          <button className="btn-add-address" onClick={openCreateModal}>
            Add Your First Address
          </button>
        </div>
      ) : (
        <div className="address-list">
          {addresses.map((addr) => (
            <div key={addr.id} className={`address-card${addr.isDefault ? ' address-card--default' : ''}`}>
              <div className="address-card-body">
                <div className="address-card-title">
                  {addr.title}
                  {addr.isDefault && <span className="badge-default">Default</span>}
                </div>
                <div className="address-card-text">{formatAddress(addr)}</div>
              </div>
              <div className="address-card-actions">
                <button className="btn-edit" onClick={() => openEditModal(addr)}>
                  Edit
                </button>
                <button
                  className="btn-delete"
                  onClick={() => setDeleteId(addr.id)}
                  disabled={addr.isDefault}
                  title={addr.isDefault ? 'Cannot delete the default address' : ''}
                >
                  Delete
                </button>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* ── Create / Edit Modal ── */}
      {showModal && (
        <div className="modal-overlay" onClick={closeModal}>
          <div className="modal-box" onClick={(e) => e.stopPropagation()}>
            <h3>{editingId ? 'Edit Address' : 'Add New Address'}</h3>
            <form onSubmit={handleSubmit} className="address-form">
              <div className="form-group">
                <label>Title <span className="required">*</span></label>
                <input
                  type="text"
                  name="title"
                  value={formData.title}
                  onChange={handleChange}
                  placeholder="e.g. Home, Office..."
                  required
                />
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label>House Number <span className="required">*</span></label>
                  <input
                    type="text"
                    name="houseNumber"
                    value={formData.houseNumber}
                    onChange={handleChange}
                    placeholder="e.g. 462/31"
                    required
                  />
                </div>
                <div className="form-group">
                  <label>Street Name <span className="required">*</span></label>
                  <input
                    type="text"
                    name="streetName"
                    value={formData.streetName}
                    onChange={handleChange}
                    placeholder="e.g. Cach Mang Thang 8"
                    required
                  />
                </div>
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label>Province / City <span className="required">*</span></label>
                  <select
                    value={selectedProvinceId}
                    onChange={handleProvinceChange}
                    disabled={provinces.length === 0}
                  >
                    <option value="">-- Select province --</option>
                    {provinces.map((p) => (
                      <option key={p.id} value={p.id}>
                        {p.name || p.fullName}
                      </option>
                    ))}
                  </select>
                </div>

                <div className="form-group">
                  <label>Ward / District <span className="required">*</span></label>
                  <select
                    name="wardId"
                    value={formData.wardId}
                    onChange={handleChange}
                    required
                    disabled={!selectedProvinceId || availableWards.length === 0}
                  >
                    <option value="">-- Select ward --</option>
                    {availableWards.map((w) => (
                      <option key={w.id} value={w.id}>
                        {w.fullName || w.name}
                      </option>
                    ))}
                  </select>
                </div>
              </div>

              <div className="form-group">
                <label>Additional Detail</label>
                <input
                  type="text"
                  name="detail"
                  value={formData.detail}
                  onChange={handleChange}
                  placeholder="Floor, building, apartment..."
                />
              </div>

              <div className="form-group form-group--checkbox">
                <label>
                  <input
                    type="checkbox"
                    name="isDefault"
                    checked={formData.isDefault}
                    onChange={handleChange}
                  />
                  Set as default address
                </label>
              </div>

              <div className="form-actions">
                <button type="submit" className="btn-submit" disabled={formLoading}>
                  {formLoading ? 'Saving...' : editingId ? 'Update' : 'Add Address'}
                </button>
                <button type="button" className="btn-cancel" onClick={closeModal}>
                  Cancel
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* ── Delete Confirm Modal ── */}
      {deleteId && (
        <div className="modal-overlay" onClick={() => setDeleteId(null)}>
          <div className="modal-box modal-box--confirm" onClick={(e) => e.stopPropagation()}>
            <h3>Confirm Delete</h3>
            <p>Are you sure you want to delete this address? This action cannot be undone.</p>
            <div className="form-actions">
              <button
                className="btn-submit btn-submit--danger"
                onClick={handleDeleteConfirm}
                disabled={deleteLoading}
              >
                {deleteLoading ? 'Deleting...' : 'Delete'}
              </button>
              <button className="btn-cancel" onClick={() => setDeleteId(null)}>
                Cancel
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
