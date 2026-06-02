// src/pages/admin/categories/AdminCategoriesPage.jsx
import React, { useEffect, useState } from 'react';
import { categoryService } from '../../../services/categoryService';
import CategoryForm from '../../../components/category/form/CategoryForm';
import '../shared/AdminPage.css';

export default function AdminCategoriesPage() {
  useEffect(() => { document.title = 'Admin - Categories'; }, []);

  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState(null);
  const [modal, setModal] = useState(null);
  const [deleteTarget, setDeleteTarget] = useState(null);

  const fetchCategories = async () => {
    try {
      setLoading(true);
      const res = await categoryService.getAll();
      setCategories(res.data?.data || []);
    } catch {
      setError('Failed to load categories.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchCategories(); }, []);

  const getParentName = (parentId) => {
    if (!parentId) return '—';
    const parent = categories.find((c) => c.id === parentId);
    return parent ? parent.name : parentId;
  };

  const handleCreate = async (formData) => {
    setSubmitting(true);
    try {
      const fd = new FormData();
      fd.append('name', formData.name);
      fd.append('description', formData.description || '');
      if (formData.parentId) fd.append('parentId', formData.parentId);
      if (formData.imageFile) fd.append('image', formData.imageFile);

      const res = await fetch(`${import.meta.env.VITE_API_URL}/api/categories`, {
        method: 'POST',
        credentials: 'include',
        body: fd,
      });
      const data = await res.json();
      if (res.ok && data.success) {
        window.showSuccess('Category created successfully');
        setModal(null);
        fetchCategories();
      } else {
        return data.errors || { _: data.message || 'Failed to create category' };
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
      if (formData.parentId) fd.append('parentId', formData.parentId);
      if (formData.imageFile) fd.append('image', formData.imageFile);

      const res = await fetch(
        `${import.meta.env.VITE_API_URL}/api/categories/${modal.category.id}`,
        { method: 'PUT', credentials: 'include', body: fd }
      );
      const data = await res.json();
      if (res.ok && data.success) {
        window.showSuccess('Category updated successfully');
        setModal(null);
        fetchCategories();
      } else {
        return data.errors || { _: data.message || 'Failed to update category' };
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
      const res = await categoryService.delete(deleteTarget.id);
      if (res.ok) {
        window.showSuccess('Category deleted successfully');
        fetchCategories();
      } else {
        window.showError(res.data?.message || 'Failed to delete category');
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
        <h2 className="admin-page-title">📂 Categories</h2>
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
                <th>Category Name</th>
                <th>Parent Category</th>
                <th>Description</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {categories.length === 0 ? (
                <tr>
                  <td colSpan={6} className="admin-table-empty">
                    No categories found
                  </td>
                </tr>
              ) : (
                categories.map((cat, idx) => (
                  <tr key={cat.id}>
                    <td>{idx + 1}</td>
                    <td>
                      {cat.image ? (
                        <img src={cat.image} alt={cat.name} className="admin-table-img" />
                      ) : (
                        <span className="admin-no-image">—</span>
                      )}
                    </td>
                    <td className="admin-table-name">{cat.name}</td>
                    <td>{getParentName(cat.parentId)}</td>
                    <td className="admin-table-desc">
                      {cat.description
                        ? cat.description.length > 60
                          ? cat.description.slice(0, 60) + '...'
                          : cat.description
                        : '—'}
                    </td>
                    <td>
                      <div className="admin-action-btns">
                        <button
                          className="btn-admin-edit"
                          onClick={() => setModal({ mode: 'edit', category: cat })}
                        >
                          ✏️ Edit
                        </button>
                        <button
                          className="btn-admin-delete"
                          onClick={() => setDeleteTarget(cat)}
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
            <CategoryForm
              categories={categories}
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
            <CategoryForm
              category={modal.category}
              categories={categories.filter((c) => c.id !== modal.category.id)}
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
              Are you sure you want to delete category{' '}
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
