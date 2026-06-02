// src/pages/admin/brands/AdminBrandsPage.jsx
import React, { useEffect, useState } from 'react';
import { brandService } from '../../../services/brandService';
import BrandForm from '../../../components/brand/form/BrandForm';
import '../shared/AdminPage.css';

export default function AdminBrandsPage() {
  useEffect(() => { document.title = 'Admin - Brands'; }, []);

  const [brands, setBrands] = useState([]);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState(null);
  const [modal, setModal] = useState(null);
  const [deleteTarget, setDeleteTarget] = useState(null);

  const fetchBrands = async () => {
    try {
      setLoading(true);
      const res = await brandService.getAll();
      setBrands(res.data?.data || []);
    } catch {
      setError('Failed to load brands.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchBrands(); }, []);

  const handleCreate = async (formData) => {
    setSubmitting(true);
    try {
      const fd = new FormData();
      fd.append('name', formData.name);
      fd.append('description', formData.description || '');
      if (formData.imageFile) fd.append('image', formData.imageFile);

      const res = await fetch(`${import.meta.env.VITE_API_URL}/api/brands`, {
        method: 'POST',
        credentials: 'include',
        body: fd,
      });
      const data = await res.json();
      if (res.ok && data.success) {
        window.showSuccess('Brand created successfully');
        setModal(null);
        fetchBrands();
      } else {
        return data.errors || { _: data.message || 'Failed to create brand' };
      }
    } catch {
      window.showError('Server connection error');
    } finally {
      setSubmitting(false);
    }
  };

  const handleUpdate = async (formData) => {
    setSubmitting(true);
    try {
      const fd = new FormData();
      fd.append('name', formData.name);
      fd.append('description', formData.description || '');
      if (formData.imageFile) fd.append('image', formData.imageFile);

      const res = await fetch(
        `${import.meta.env.VITE_API_URL}/api/brands/${modal.brand.id}`,
        { method: 'PUT', credentials: 'include', body: fd }
      );
      const data = await res.json();
      if (res.ok && data.success) {
        window.showSuccess('Brand updated successfully');
        setModal(null);
        fetchBrands();
      } else {
        return data.errors || { _: data.message || 'Failed to update brand' };
      }
    } catch {
      window.showError('Server connection error');
    } finally {
      setSubmitting(false);
    }
  };

  const handleDelete = async () => {
    if (!deleteTarget) return;
    try {
      const res = await brandService.delete(deleteTarget.id);
      if (res.ok) {
        window.showSuccess('Brand deleted successfully');
        fetchBrands();
      } else {
        window.showError(res.data?.message || 'Failed to delete brand');
      }
    } catch {
      window.showError('Server connection error');
    } finally {
      setDeleteTarget(null);
    }
  };

  return (
    <div className="admin-page">
      <div className="admin-page-header">
        <h2 className="admin-page-title">🏷️ Brands</h2>
        <button className="btn-admin-primary" onClick={() => setModal('create')}>
          + Add New
        </button>
      </div>

      {error && <div className="admin-alert admin-alert-error">{error}</div>}

      {loading ? (
        <div className="admin-loading">
          <div className="admin-spinner" />
          <span>Loading...</span>
        </div>
      ) : (
        <div className="admin-table-wrapper">
          <table className="admin-table">
            <thead>
              <tr>
                <th>#</th>
                <th>Image</th>
                <th>Brand Name</th>
                <th>Description</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {brands.length === 0 ? (
                <tr>
                  <td colSpan={5} className="admin-table-empty">
                    No brands found
                  </td>
                </tr>
              ) : (
                brands.map((brand, idx) => (
                  <tr key={brand.id}>
                    <td>{idx + 1}</td>
                    <td>
                      {brand.image ? (
                        <img src={brand.image} alt={brand.name} className="admin-table-img" />
                      ) : (
                        <span className="admin-no-image">—</span>
                      )}
                    </td>
                    <td className="admin-table-name">{brand.name}</td>
                    <td className="admin-table-desc">
                      {brand.description
                        ? brand.description.length > 60
                          ? brand.description.slice(0, 60) + '...'
                          : brand.description
                        : '—'}
                    </td>
                    <td>
                      <div className="admin-action-btns">
                        <button
                          className="btn-admin-edit"
                          onClick={() => setModal({ mode: 'edit', brand })}
                        >
                          ✏️ Edit
                        </button>
                        <button
                          className="btn-admin-delete"
                          onClick={() => setDeleteTarget(brand)}
                        >
                          🗑️ Delete
                        </button>
                      </div>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      )}

      {/* Create modal */}
      {modal === 'create' && (
        <div className="admin-modal-overlay" onClick={() => setModal(null)}>
          <div className="admin-modal" onClick={(e) => e.stopPropagation()}>
            <BrandForm
              onSubmit={handleCreate}
              onCancel={() => setModal(null)}
              loading={submitting}
            />
          </div>
        </div>
      )}

      {/* Edit modal */}
      {modal?.mode === 'edit' && (
        <div className="admin-modal-overlay" onClick={() => setModal(null)}>
          <div className="admin-modal" onClick={(e) => e.stopPropagation()}>
            <BrandForm
              brand={modal.brand}
              onSubmit={handleUpdate}
              onCancel={() => setModal(null)}
              loading={submitting}
            />
          </div>
        </div>
      )}

      {/* Delete confirm */}
      {deleteTarget && (
        <div className="admin-modal-overlay" onClick={() => setDeleteTarget(null)}>
          <div className="admin-modal admin-confirm-modal" onClick={(e) => e.stopPropagation()}>
            <h3>Confirm Delete</h3>
            <p>
              Are you sure you want to delete brand{' '}
              <strong>{deleteTarget.name}</strong>?
            </p>
            <div className="admin-confirm-actions">
              <button className="btn-admin-secondary" onClick={() => setDeleteTarget(null)}>
                Cancel
              </button>
              <button className="btn-admin-danger" onClick={handleDelete}>
                Delete
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
