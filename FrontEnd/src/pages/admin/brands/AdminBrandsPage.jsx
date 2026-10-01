// src/pages/admin/brands/AdminBrandsPage.jsx
import React, { useEffect, useState } from 'react';
import { brandService } from '../../../services/brandService';
import BrandForm from '../../../components/brand/form/BrandForm';
import '../shared/AdminPage.css';
import './AdminBrandsPage.css';

export default function AdminBrandsPage() {
  useEffect(() => { document.title = 'Admin - Brands'; }, []);

  const [brands, setBrands]             = useState([]);
  const [loading, setLoading]           = useState(true);
  const [submitting, setSubmitting]     = useState(false);
  const [error, setError]               = useState(null);
  const [modal, setModal]               = useState(null);
  const [deleteTarget, setDeleteTarget] = useState(null);
  const [searchQuery, setSearchQuery]   = useState('');

  const fetchBrands = async () => {
    try {
      setLoading(true);
      const res = await brandService.getAll();
      setBrands(res.data?.data || []);
    } catch { setError('Failed to load brands.'); }
    finally  { setLoading(false); }
  };

  useEffect(() => { fetchBrands(); }, []);

  const filteredBrands = brands.filter(brand =>
    brand.name?.toLowerCase().includes(searchQuery.toLowerCase()) ||
    brand.description?.toLowerCase().includes(searchQuery.toLowerCase())
  );

  const handleCreate = async (formData) => {
    setSubmitting(true);
    try {
      const fd = new FormData();
      fd.append('name', formData.name);
      fd.append('description', formData.description || '');
      if (formData.imageFile) fd.append('image', formData.imageFile);
      const res  = await fetch(`${import.meta.env.VITE_API_URL}/api/brands`, { method: 'POST', credentials: 'include', body: fd });
      const data = await res.json();
      if (res.ok && data.success) { window.showSuccess('Brand created successfully'); setModal(null); fetchBrands(); }
      else return { message: data.message || 'Failed to create brand', errors: data.errors || {} };
    } catch { window.showError('Server connection error'); }
    finally   { setSubmitting(false); }
  };

  const handleUpdate = async (formData) => {
    setSubmitting(true);
    try {
      const fd = new FormData();
      fd.append('name', formData.name);
      fd.append('description', formData.description || '');
      if (formData.imageFile) fd.append('image', formData.imageFile);
      const res  = await fetch(`${import.meta.env.VITE_API_URL}/api/brands/${modal.brand.id}`, { method: 'PUT', credentials: 'include', body: fd });
      const data = await res.json();
      if (res.ok && data.success) { window.showSuccess('Brand updated successfully'); setModal(null); fetchBrands(); }
      else return { message: data.message || 'Failed to update brand', errors: data.errors || {} };
    } catch { window.showError('Server connection error'); }
    finally   { setSubmitting(false); }
  };

  const handleDelete = async () => {
    if (!deleteTarget) return;
    try {
      const res = await brandService.delete(deleteTarget.id);
      if (res.ok) { window.showSuccess('Brand deleted successfully'); fetchBrands(); }
      else window.showError(res.data?.message || 'Failed to delete brand');
    } catch { window.showError('Server connection error'); }
    finally  { setDeleteTarget(null); }
  };

  if (loading) {
    return (
      <div className="admin-brands-page">
        <div className="admin-loading">
          <div className="admin-spinner"></div>
          <span>Loading...</span>
        </div>
      </div>
    );
  }

  return (
    <div className="admin-brands-page">
      {/* Header */}
      <div className="admin-brands-header admin-light-card">
        <div className="admin-header-left">
          <div className="admin-header-icon">
            <i className="fa-solid fa-tags"></i>
          </div>
          <div>
            <h1 className="admin-header-title">Brand Management</h1>
            <p className="admin-header-subtitle">Manage brand portfolio, descriptions & visual identity</p>
          </div>
        </div>

        {/* Stats Badges */}
        <div className="admin-header-stats">
          <div className="admin-stat-badge">
            <span className="admin-stat-dot admin-total"></span>
            <span className="admin-stat-label">Total:</span>
            <span className="admin-stat-value">{brands.length}</span>
          </div>
        </div>
      </div>

      {error && <div className="admin-alert admin-alert-error">{error}</div>}

      {/* Toolbar */}
      <div className="admin-brands-toolbar admin-light-card">
        <div className="admin-toolbar-filters">
          {/* Search */}
          <div className="admin-search-input-wrapper">
            <i className="fa-solid fa-magnifying-glass"></i>
            <input
              type="text"
              placeholder="Search by name, description..."
              value={searchQuery}
              onChange={e => setSearchQuery(e.target.value)}
              className="admin-search-input"
            />
          </div>

          {/* Reset Button */}
          {searchQuery && (
            <button
              className="admin-reset-button"
              onClick={() => setSearchQuery('')}
            >
              <i className="fa-solid fa-rotate-left"></i> Reset search
            </button>
          )}
        </div>

        {/* Add New Button */}
        <button
          className="admin-btn-add-new"
          onClick={() => setModal('create')}
        >
          <i className="fa-solid fa-plus"></i>
          <span>Add New Brand</span>
        </button>
      </div>

      {/* Table Container */}
      <div className="admin-brands-table-container admin-light-card">
        <div className="admin-table-wrapper">
          <table className="admin-brands-table">
            <thead>
              <tr>
                <th className="admin-col-checkbox">#</th>
                <th className="admin-col-image">Brand Image</th>
                <th className="admin-col-name">Brand Name</th>
                <th className="admin-col-description">Description</th>
                <th className="admin-col-actions">Actions</th>
              </tr>
            </thead>
            <tbody>
              {filteredBrands.length === 0 ? (
                <tr>
                  <td colSpan="5" className="admin-empty-state">
                    <i className="fa-solid fa-box-open"></i>
                    <p>No matching brands found</p>
                    <span>Please try searching again</span>
                  </td>
                </tr>
              ) : filteredBrands.map((brand, idx) => (
                <tr key={brand.id}>
                  <td className="admin-col-checkbox">{idx + 1}</td>
                  <td className="admin-col-image">
                    <div className="admin-brand-image-cell">
                      {brand.image ? (
                        <img src={brand.image} alt={brand.name} />
                      ) : (
                        <div className="admin-no-image">No Image</div>
                      )}
                    </div>
                  </td>
                  <td className="admin-col-name">
                    <h4 className="admin-brand-name">{brand.name}</h4>
                  </td>
                  <td className="admin-col-description">
                    <p className="admin-brand-description">
                      {brand.description ? brand.description.length > 100 ? brand.description.slice(0, 100) + '...' : brand.description : '—'}
                    </p>
                  </td>
                  <td className="admin-col-actions">
                    <div className="admin-action-buttons">
                      <button
                        className="admin-btn-action btn-edit"
                        title="Edit"
                        onClick={() => setModal({ mode: 'edit', brand })}
                      >
                        <i className="fa-solid fa-pen-to-square"></i>
                      </button>
                      <button
                        className="admin-btn-action admin-btn-delete"
                        title="Delete"
                        onClick={() => setDeleteTarget(brand)}
                      >
                        <i className="fa-solid fa-trash-can"></i>
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Create modal */}
      {modal === 'create' && (
        <div className="admin-modal-overlay" onClick={() => setModal(null)}>
          <div className="admin-modal" onClick={e => e.stopPropagation()}>
            <BrandForm onSubmit={handleCreate} onCancel={() => setModal(null)} loading={submitting} />
          </div>
        </div>
      )}

      {/* Edit modal */}
      {modal?.mode === 'edit' && (
        <div className="admin-modal-overlay" onClick={() => setModal(null)}>
          <div className="admin-modal" onClick={e => e.stopPropagation()}>
            <BrandForm brand={modal.brand} onSubmit={handleUpdate} onCancel={() => setModal(null)} loading={submitting} />
          </div>
        </div>
      )}

      {/* Delete confirm */}
      {deleteTarget && (
        <div className="admin-modal-overlay" onClick={() => setDeleteTarget(null)}>
          <div className="admin-modal admin-confirm-modal" onClick={e => e.stopPropagation()}>
            <h3>Confirm Delete</h3>
            <p>Are you sure you want to delete brand <strong>{deleteTarget.name}</strong>?</p>
            <div className="admin-confirm-actions">
              <button className="admin-btn-secondary" onClick={() => setDeleteTarget(null)}>Cancel</button>
              <button className="admin-btn-danger"    onClick={handleDelete}>Delete</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
