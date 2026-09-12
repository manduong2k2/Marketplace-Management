// src/pages/admin/vendors/AdminVendorsPage.jsx
import React, { useEffect, useState } from 'react';
import { vendorService } from '../../../services/vendorService';
import VendorForm from '../../../components/vendor/form/VendorForm';
import '../shared/AdminPage.css';

const STATUS_CONFIG = {
  ACTIVE:   { label: 'Active',   color: '#22c55e', bg: '#f0fdf4' },
  PENDING:  { label: 'Pending',  color: '#f59e0b', bg: '#fffbeb' },
  INACTIVE: { label: 'Inactive', color: '#6b7280', bg: '#f3f4f6' },
  BANNED:   { label: 'Banned',   color: '#ef4444', bg: '#fef2f2' },
};

function getStatusBadge(status) {
  if (!status) return null;
  const cfg = STATUS_CONFIG[status.toUpperCase()] ?? {
    label: status, color: '#6b7280', bg: '#f3f4f6',
  };
  return (
    <span
      className="admin-badge"
      style={{ 
        backgroundColor: cfg.bg, 
        color: cfg.color,
        border: `1px solid ${cfg.color}33`
      }}
    >
      {cfg.label}
    </span>
  );
}

export default function AdminVendorsPage() {
  useEffect(() => { document.title = 'Admin - Vendors'; }, []);

  const [vendors, setVendors]           = useState([]);
  const [loading, setLoading]           = useState(true);
  const [submitting, setSubmitting]     = useState(false);
  const [error, setError]               = useState(null);
  const [modal, setModal]               = useState(null);
  const [deleteTarget, setDeleteTarget] = useState(null);

  const fetchVendors = async () => {
    try {
      setLoading(true);
      const res = await vendorService.getAll();
      setVendors(res.data?.data || []);
    } catch { setError('Failed to load vendors.'); }
    finally  { setLoading(false); }
  };

  useEffect(() => { fetchVendors(); }, []);

  const handleCreate = async (formData) => {
    setSubmitting(true);
    try {
      const fd = new FormData();
      fd.append('name', formData.name);
      fd.append('email', formData.email || '');
      fd.append('description', formData.description || '');
      fd.append('phone', formData.phone || '');
      fd.append('taxCode', formData.taxCode || '');
      if (formData.logoFile) fd.append('logo', formData.logoFile);
      if (formData.bannerFile) fd.append('banner', formData.bannerFile);
      const res  = await fetch(`${import.meta.env.VITE_API_URL}/api/vendors`, { method: 'POST', credentials: 'include', body: fd });
      const data = await res.json();
      if (res.ok && data.success) { window.showSuccess('Vendor created successfully'); setModal(null); fetchVendors(); }
      else return data.errors || { _: data.message || 'Failed to create vendor' };
    } catch { window.showError('Server connection error'); }
    finally   { setSubmitting(false); }
  };

  const handleUpdate = async (formData) => {
    setSubmitting(true);
    try {
      const fd = new FormData();
      fd.append('name', formData.name);
      fd.append('email', formData.email || '');
      fd.append('description', formData.description || '');
      fd.append('phone', formData.phone || '');
      fd.append('taxCode', formData.taxCode || '');
      if (formData.logoFile) fd.append('logo', formData.logoFile);
      if (formData.bannerFile) fd.append('banner', formData.bannerFile);
      const res  = await fetch(`${import.meta.env.VITE_API_URL}/api/vendors/${modal.vendor.id}`, { method: 'PUT', credentials: 'include', body: fd });
      const data = await res.json();
      if (res.ok && data.success) { window.showSuccess('Vendor updated successfully'); setModal(null); fetchVendors(); }
      else return data.errors || { _: data.message || 'Failed to update vendor' };
    } catch { window.showError('Server connection error'); }
    finally   { setSubmitting(false); }
  };

  const handleDelete = async () => {
    if (!deleteTarget) return;
    try {
      const res = await vendorService.delete(deleteTarget.id);
      if (res.ok) { window.showSuccess('Vendor deleted successfully'); fetchVendors(); }
      else window.showError(res.data?.message || 'Failed to delete vendor');
    } catch { window.showError('Server connection error'); }
    finally  { setDeleteTarget(null); }
  };

  const handleApprove = async (vendor) => {
    try {
      const res = await vendorService.approve(vendor.id);
      if (res.ok) { window.showSuccess('Vendor approved successfully'); fetchVendors(); }
      else window.showError(res.data?.message || 'Failed to approve vendor');
    } catch { window.showError('Server connection error'); }
  };

  const handleReject = async (vendor) => {
    const reason = prompt('Enter rejection reason:');
    if (!reason) return;
    try {
      const res = await vendorService.reject(vendor.id, reason);
      if (res.ok) { window.showSuccess('Vendor rejected successfully'); fetchVendors(); }
      else window.showError(res.data?.message || 'Failed to reject vendor');
    } catch { window.showError('Server connection error'); }
  };

  const handleSuspend = async (vendor) => {
    const reason = prompt('Enter suspension reason:');
    if (!reason) return;
    try {
      const res = await vendorService.suspend(vendor.id, reason);
      if (res.ok) { window.showSuccess('Vendor suspended successfully'); fetchVendors(); }
      else window.showError(res.data?.message || 'Failed to suspend vendor');
    } catch { window.showError('Server connection error'); }
  };

  return (
    <div className="admin-page">
      <div className="admin-page-header">
        <h2 className="admin-page-title">🏪 Vendors</h2>
        <button className="btn-admin-primary" onClick={() => setModal('create')}>+ Add New</button>
      </div>

      {error && <div className="admin-alert admin-alert-error">{error}</div>}

      {loading ? (
        <div className="admin-loading"><div className="admin-spinner" /><span>Loading...</span></div>
      ) : (
        <div className="admin-table-wrapper">
          <table className="admin-table">
            <thead><tr><th>#</th><th>Logo</th><th>Vendor Name</th><th>Email</th><th>Phone</th><th>Tax Code</th><th>Status</th><th>Actions</th></tr></thead>
            <tbody>
              {vendors.length === 0 ? (
                <tr><td colSpan={8} className="admin-table-empty">No vendors found</td></tr>
              ) : vendors.map((vendor, idx) => (
                <tr key={vendor.id}>
                  <td>{idx + 1}</td>
                  <td>{vendor.logo ? <img src={vendor.logo} alt={vendor.name} className="admin-table-img" /> : <span className="admin-no-image">—</span>}</td>
                  <td className="admin-table-name">{vendor.name}</td>
                  <td>{vendor.email || '—'}</td>
                  <td>{vendor.phone || '—'}</td>
                  <td>{vendor.taxCode || '—'}</td>
                  <td>{getStatusBadge(vendor.status)}</td>
                  <td>
                    <div className="admin-action-btns">
                      <button className="btn-admin-edit"   onClick={() => setModal({ mode: 'edit', vendor })}>✏️ Edit</button>
                      {vendor.status === 'PENDING' && (
                        <>
                          <button className="btn-admin-success" onClick={() => handleApprove(vendor)}>✅ Approve</button>
                          <button className="btn-admin-warning" onClick={() => handleReject(vendor)}>❌ Reject</button>
                        </>
                      )}
                      {vendor.status === 'ACTIVE' && (
                        <button className="btn-admin-warning" onClick={() => handleSuspend(vendor)}>⏸️ Suspend</button>
                      )}
                      <button className="btn-admin-delete" onClick={() => setDeleteTarget(vendor)}>🗑️ Delete</button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {/* Create modal */}
      {modal === 'create' && (
        <div className="admin-modal-overlay" onClick={() => setModal(null)}>
          <div className="admin-modal" onClick={e => e.stopPropagation()}>
            <VendorForm onSubmit={handleCreate} onCancel={() => setModal(null)} loading={submitting} />
          </div>
        </div>
      )}

      {/* Edit modal */}
      {modal?.mode === 'edit' && (
        <div className="admin-modal-overlay" onClick={() => setModal(null)}>
          <div className="admin-modal" onClick={e => e.stopPropagation()}>
            <VendorForm vendor={modal.vendor} onSubmit={handleUpdate} onCancel={() => setModal(null)} loading={submitting} />
          </div>
        </div>
      )}

      {/* Delete confirm */}
      {deleteTarget && (
        <div className="admin-modal-overlay" onClick={() => setDeleteTarget(null)}>
          <div className="admin-modal admin-confirm-modal" onClick={e => e.stopPropagation()}>
            <h3>Confirm Delete</h3>
            <p>Are you sure you want to delete vendor <strong>{deleteTarget.name}</strong>?</p>
            <div className="admin-confirm-actions">
              <button className="btn-admin-secondary" onClick={() => setDeleteTarget(null)}>Cancel</button>
              <button className="btn-admin-danger"    onClick={handleDelete}>Delete</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
