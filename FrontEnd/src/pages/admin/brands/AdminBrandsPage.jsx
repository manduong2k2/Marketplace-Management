// src/pages/admin/brands/AdminBrandsPage.jsx
import React, { useEffect, useState } from 'react';
import { brandService } from '../../../services/brandService';
import BrandForm from '../../../components/brand/form/BrandForm';
import { PageHeader, SearchBox, EmptyState, Modal, ConfirmDialog } from '../shared/AdminUi';
import './AdminBrandsPage.css';

const truncate = (text, max = 100) => (text.length > max ? text.slice(0, max) + '...' : text);

export default function AdminBrandsPage() {
  useEffect(() => { document.title = 'Admin - Brands'; }, []);

  const [brands, setBrands]             = useState([]);
  const [loading, setLoading]           = useState(true);
  const [submitting, setSubmitting]     = useState(false);
  const [deleting, setDeleting]         = useState(false);
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
    setDeleting(true);
    try {
      const res = await brandService.delete(deleteTarget.id);
      if (res.ok) { window.showSuccess('Brand deleted successfully'); fetchBrands(); }
      else window.showError(res.data?.message || 'Failed to delete brand');
    } catch { window.showError('Server connection error'); }
    finally  { setDeleting(false); setDeleteTarget(null); }
  };

  const closeModal = () => setModal(null);
  const editing = modal?.mode === 'edit' ? modal.brand : null;

  return (
    <div className="admin-ui-page admin-brands-page">
      <PageHeader
        eyebrow="Catalog"
        eyebrowIcon="bi-box-seam"
        title="Brands"
        description="Manage the brand portfolio: names, descriptions and logos shown across the store."
        actions={
          <button type="button" className="admin-ui-btn admin-ui-btn--primary" onClick={() => setModal('create')}>
            <i className="bi bi-plus-lg"></i> New brand
          </button>
        }
      />

      {error && (
        <div className="admin-brands-alert" role="alert">
          <i className="bi bi-exclamation-circle"></i> {error}
        </div>
      )}

      <div className="admin-ui-toolbar">
        <SearchBox value={searchQuery} onChange={setSearchQuery} placeholder="Search by name or description…" />
        <span className="admin-ui-muted admin-brands-count">
          {searchQuery ? `${filteredBrands.length} of ${brands.length}` : brands.length} brand{brands.length === 1 ? '' : 's'}
        </span>
      </div>

      <div className="admin-ui-card">
        <div className="admin-ui-table-wrap">
          <table className="admin-ui-table admin-brands-table">
            <thead>
              <tr>
                <th>Brand</th>
                <th>Description</th>
                <th>Products</th>
                <th className="admin-ui-col-actions"><span className="visually-hidden">Actions</span></th>
              </tr>
            </thead>
            <tbody>
              {loading && brands.length === 0 && [...Array(5)].map((_, i) => (
                <tr key={i} className="admin-ui-skeleton-row"><td colSpan={4}><span></span></td></tr>
              ))}
              {filteredBrands.map(brand => (
                <tr key={brand.id} onClick={() => setModal({ mode: 'edit', brand })}>
                  <td>
                    <div className="admin-brands-identity">
                      <span className="admin-brands-thumb">
                        {brand.image
                          ? <img src={brand.image} alt={brand.name} />
                          : <i className="bi bi-image"></i>}
                      </span>
                      <strong>{brand.name}</strong>
                    </div>
                  </td>
                  <td className="admin-ui-muted admin-brands-description">
                    {brand.description ? truncate(brand.description) : '—'}
                  </td>
                  <td>
                    <span className="admin-ui-tag admin-brands-products">
                      <i className="bi bi-box"></i> {brand.productsCount ?? 0}
                    </span>
                  </td>
                  <td className="admin-ui-col-actions" onClick={(e) => e.stopPropagation()}>
                    <button type="button" className="admin-ui-icon-btn" title="Edit" onClick={() => setModal({ mode: 'edit', brand })}>
                      <i className="bi bi-pencil"></i>
                    </button>
                    <button type="button" className="admin-ui-icon-btn admin-ui-icon-btn--danger" title="Delete" onClick={() => setDeleteTarget(brand)}>
                      <i className="bi bi-trash3"></i>
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
          {!loading && filteredBrands.length === 0 && (
            <EmptyState
              icon="bi-tags"
              title={searchQuery ? 'No matching brands found' : 'No brands yet'}
              text={searchQuery ? 'Try another search.' : 'Use "New brand" to add the first one.'}
            />
          )}
        </div>
      </div>

      {(modal === 'create' || editing) && (
        <Modal
          title={editing ? 'Edit brand' : 'New brand'}
          subtitle={editing ? editing.name : 'Create a brand profile with a description and logo.'}
          onClose={closeModal}
          width={640}
        >
          <div className="admin-brands-form-scope">
            {editing
              ? <BrandForm brand={editing} onSubmit={handleUpdate} onCancel={closeModal} loading={submitting} />
              : <BrandForm onSubmit={handleCreate} onCancel={closeModal} loading={submitting} />}
          </div>
        </Modal>
      )}

      {deleteTarget && (
        <ConfirmDialog
          title="Delete brand"
          message={<>Are you sure you want to delete brand <strong>{deleteTarget.name}</strong>?</>}
          busy={deleting}
          onConfirm={handleDelete}
          onClose={() => setDeleteTarget(null)}
        />
      )}
    </div>
  );
}
