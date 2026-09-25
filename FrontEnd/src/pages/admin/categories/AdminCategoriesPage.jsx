// src/pages/admin/categories/AdminCategoriesPage.jsx
import React, { useEffect, useState } from 'react';
import { categoryService } from '../../../services/categoryService';
import CategoryForm from '../../../components/category/form/CategoryForm';
import '../shared/AdminPage.css';
import './AdminCategoriesPage.css';

export default function AdminCategoriesPage() {
  useEffect(() => { document.title = 'Admin - Categories'; }, []);

  const [categories, setCategories]     = useState([]);
  const [loading, setLoading]           = useState(true);
  const [submitting, setSubmitting]     = useState(false);
  const [error, setError]               = useState(null);
  const [modal, setModal]               = useState(null);
  const [deleteTarget, setDeleteTarget] = useState(null);
  const [searchQuery, setSearchQuery]   = useState('');

  const fetchCategories = async () => {
    try {
      setLoading(true);
      const res = await categoryService.getAll();
      setCategories(res.data?.data || []);
    } catch { setError('Failed to load categories.'); }
    finally  { setLoading(false); }
  };

  useEffect(() => { fetchCategories(); }, []);

  const getParentName = (parentId) => {
    if (!parentId) return '—';
    const parent = categories.find(c => c.id === parentId);
    return parent ? parent.name : parentId;
  };

  const filteredCategories = categories.filter(cat =>
    cat.name?.toLowerCase().includes(searchQuery.toLowerCase()) ||
    cat.description?.toLowerCase().includes(searchQuery.toLowerCase())
  );

  const handleCreate = async (formData) => {
    setSubmitting(true);
    try {
      const fd = new FormData();
      fd.append('name', formData.name);
      fd.append('description', formData.description || '');
      if (formData.parentId) fd.append('parentId', formData.parentId);
      if (formData.imageFile) fd.append('image', formData.imageFile);
      const res  = await fetch(`${import.meta.env.VITE_API_URL}/api/categories`, { method: 'POST', credentials: 'include', body: fd });
      const data = await res.json();
      if (res.ok && data.success) { window.showSuccess('Category created successfully'); setModal(null); fetchCategories(); }
      else return data.errors || { _: data.message || 'Failed to create category' };
    } catch { window.showError('Server connection error'); }
    finally   { setSubmitting(false); }
  };

  const handleUpdate = async (formData) => {
    setSubmitting(true);
    try {
      const fd = new FormData();
      fd.append('name', formData.name);
      fd.append('description', formData.description || '');
      if (formData.parentId) fd.append('parentId', formData.parentId);
      if (formData.imageFile) fd.append('image', formData.imageFile);
      const res  = await fetch(`${import.meta.env.VITE_API_URL}/api/categories/${modal.category.id}`, { method: 'PUT', credentials: 'include', body: fd });
      const data = await res.json();
      if (res.ok && data.success) { window.showSuccess('Category updated successfully'); setModal(null); fetchCategories(); }
      else return data.errors || { _: data.message || 'Failed to update category' };
    } catch { window.showError('Server connection error'); }
    finally   { setSubmitting(false); }
  };

  const handleDelete = async () => {
    if (!deleteTarget) return;
    try {
      const res = await categoryService.delete(deleteTarget.id);
      if (res.ok) { window.showSuccess('Category deleted successfully'); fetchCategories(); }
      else window.showError(res.data?.message || 'Failed to delete category');
    } catch { window.showError('Server connection error'); }
    finally  { setDeleteTarget(null); }
  };

  if (loading) {
    return (
      <div className="categories-page">
        <div className="admin-loading">
          <div className="admin-spinner"></div>
          <span>Loading...</span>
        </div>
      </div>
    );
  }

  return (
    <div className="categories-page">
      {/* Header */}
      <div className="categories-header light-card">
        <div className="header-left">
          <div className="header-icon">
            <i className="fa-solid fa-folder-tree"></i>
          </div>
          <div>
            <h1 className="header-title">Category Management</h1>
            <p className="header-subtitle">Manage product categorization, hierarchy & structure</p>
          </div>
        </div>

        {/* Stats Badges */}
        <div className="header-stats">
          <div className="stat-badge">
            <span className="stat-dot total"></span>
            <span className="stat-label">Total:</span>
            <span className="stat-value">{categories.length}</span>
          </div>
        </div>
      </div>

      {error && <div className="admin-alert admin-alert-error">{error}</div>}

      {/* Toolbar */}
      <div className="categories-toolbar light-card">
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
          <span>Add New Category</span>
        </button>
      </div>

      {/* Table Container */}
      <div className="categories-table-container light-card">
        <div className="table-wrapper">
          <table className="categories-table">
            <thead>
              <tr>
                <th className="col-checkbox">#</th>
                <th className="col-image">Category Image</th>
                <th className="col-name">Category Name</th>
                <th className="col-parent">Parent Category</th>
                <th className="col-description">Description</th>
                <th className="col-actions">Actions</th>
              </tr>
            </thead>
            <tbody>
              {filteredCategories.length === 0 ? (
                <tr>
                  <td colSpan="6" className="empty-state">
                    <i className="fa-solid fa-box-open"></i>
                    <p>No matching categories found</p>
                    <span>Please try searching again</span>
                  </td>
                </tr>
              ) : filteredCategories.map((cat, idx) => (
                <tr key={cat.id}>
                  <td className="col-checkbox">{idx + 1}</td>
                  <td className="col-image">
                    <div className="category-image-cell">
                      {cat.image ? (
                        <img src={cat.image} alt={cat.name} />
                      ) : (
                        <div className="no-image">No Image</div>
                      )}
                    </div>
                  </td>
                  <td className="col-name">
                    <h4 className="category-name">{cat.name}</h4>
                  </td>
                  <td className="col-parent">
                    <span className="parent-badge">{getParentName(cat.parentId)}</span>
                  </td>
                  <td className="col-description">
                    <p className="category-description">
                      {cat.description ? cat.description.length > 100 ? cat.description.slice(0, 100) + '...' : cat.description : '—'}
                    </p>
                  </td>
                  <td className="col-actions">
                    <div className="action-buttons">
                      <button
                        className="btn-action btn-edit"
                        title="Edit"
                        onClick={() => setModal({ mode: 'edit', category: cat })}
                      >
                        <i className="fa-solid fa-pen-to-square"></i>
                      </button>
                      <button
                        className="btn-action btn-delete"
                        title="Delete"
                        onClick={() => setDeleteTarget(cat)}
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

      {modal === 'create' && (
        <div className="admin-modal-overlay" onClick={() => setModal(null)}>
          <div className="admin-modal" onClick={e => e.stopPropagation()}>
            <CategoryForm categories={categories} onSubmit={handleCreate} onCancel={() => setModal(null)} loading={submitting} />
          </div>
        </div>
      )}

      {modal?.mode === 'edit' && (
        <div className="admin-modal-overlay" onClick={() => setModal(null)}>
          <div className="admin-modal" onClick={e => e.stopPropagation()}>
            <CategoryForm category={modal.category} categories={categories.filter(c => c.id !== modal.category.id)} onSubmit={handleUpdate} onCancel={() => setModal(null)} loading={submitting} />
          </div>
        </div>
      )}

      {deleteTarget && (
        <div className="admin-modal-overlay" onClick={() => setDeleteTarget(null)}>
          <div className="admin-modal admin-confirm-modal" onClick={e => e.stopPropagation()}>
            <h3>Confirm Delete</h3>
            <p>Are you sure you want to delete category <strong>{deleteTarget.name}</strong>?</p>
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
