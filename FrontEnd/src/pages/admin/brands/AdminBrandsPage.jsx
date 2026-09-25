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
      <div className="brands-page">
        <div className="admin-loading">
          <div className="admin-spinner"></div>
          <span>Loading...</span>
        </div>
      </div>
    );
  }

  return (
    <div className="brands-page">
      {/* Header */}
      <div className="brands-header light-card">
        <div className="header-left">
          <div className="header-icon">
            <i className="fa-solid fa-tags"></i>
          </div>
          <div>
            <h1 className="header-title">Brand Management</h1>
            <p className="header-subtitle">Manage brand portfolio, descriptions & visual identity</p>
          </div>
        </div>

        {/* Stats Badges */}
        <div className="header-stats">
          <div className="stat-badge">
            <span className="stat-dot total"></span>
            <span className="stat-label">Total:</span>
            <span className="stat-value">{brands.length}</span>
          </div>
        </div>
      </div>

      {error && <div className="admin-alert admin-alert-error">{error}</div>}

      {/* Toolbar */}
      <div className="brands-toolbar light-card">
        <div className="toolbar-filters">
          {/* Search */}
          <div className="search-input-wrapper">
            <i className="fa-solid fa-magnifying-glass"></i>
            <input
              type="text"
              placeholder="Search by name, description..."
              value={searchQuery}
              onChange={e => setSearchQuery(e.target.value)}
              className="search-input"
            />
          </div>

          {/* Reset Button */}
          {searchQuery && (
            <button
              className="reset-button"
              onClick={() => setSearchQuery('')}
            >
              <i className="fa-solid fa-rotate-left"></i> Reset search
            </button>
          )}
        </div>

        {/* Add New Button */}
        <button
          className="btn-add-new"
          onClick={() => setModal('create')}
        >
          <i className="fa-solid fa-plus"></i>
          <span>Add New Brand</span>
        </button>
      </div>

      {/* Table Container */}
      <div className="brands-table-container light-card">
        <div className="table-wrapper">
          <table className="brands-table">
            <thead>
              <tr>
                <th className="col-checkbox">#</th>
                <th className="col-image">Brand Image</th>
                <th className="col-name">Brand Name</th>
                <th className="col-description">Description</th>
                <th className="col-actions">Actions</th>
              </tr>
            </thead>
            <tbody>
              {filteredBrands.length === 0 ? (
                <tr>
                  <td colSpan="5" className="empty-state">
                    <i className="fa-solid fa-box-open"></i>
                    <p>No matching brands found</p>
                    <span>Please try searching again</span>
                  </td>
                </tr>
              ) : filteredBrands.map((brand, idx) => (
                <tr key={brand.id}>
                  <td className="col-checkbox">{idx + 1}</td>
                  <td className="col-image">
                    <div className="brand-image-cell">
                      {brand.image ? (
                        <img src={brand.image} alt={brand.name} />
                      ) : (
                        <div className="no-image">No Image</div>
                      )}
                    </div>
                  </td>
                  <td className="col-name">
                    <h4 className="brand-name">{brand.name}</h4>
                  </td>
                  <td className="col-description">
                    <p className="brand-description">
                      {brand.description ? brand.description.length > 100 ? brand.description.slice(0, 100) + '...' : brand.description : '—'}
                    </p>
                  </td>
                  <td className="col-actions">
                    <div className="action-buttons">
                      <button
                        className="btn-action btn-edit"
                        title="Edit"
                        onClick={() => setModal({ mode: 'edit', brand })}
                      >
                        <i className="fa-solid fa-pen-to-square"></i>
                      </button>
                      <button
                        className="btn-action btn-delete"
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
              <button className="btn-admin-secondary" onClick={() => setDeleteTarget(null)}>Cancel</button>
              <button className="btn-admin-danger"    onClick={handleDelete}>Delete</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
