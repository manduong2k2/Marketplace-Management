// src/pages/admin/categories/AdminCategoriesPage.jsx
import React, { useEffect, useState } from 'react';
import { categoryService } from '../../../services/categoryService';
import CategoryForm from '../../../components/category/form/CategoryForm';
import { PageHeader, SearchBox, EmptyState, Modal, ConfirmDialog } from '../shared/AdminUi';
import './AdminCategoriesPage.css';

const truncate = (text, max = 100) => (text.length > max ? text.slice(0, max) + '...' : text);

/**
 * Orders the flat category list as a tree (parent followed by its children) and computes each
 * category's depth. Categories whose parent is missing from the list are treated as top-level.
 */
const buildTreeRows = (categories) => {
  const ids = new Set(categories.map(c => c.id));
  const childrenOf = new Map();
  categories.forEach(cat => {
    const key = cat.parentId && ids.has(cat.parentId) ? cat.parentId : null;
    if (!childrenOf.has(key)) childrenOf.set(key, []);
    childrenOf.get(key).push(cat);
  });

  const rows = [];
  const visited = new Set();
  const walk = (parentId, depth) => {
    (childrenOf.get(parentId) || []).forEach(cat => {
      if (visited.has(cat.id)) return;
      visited.add(cat.id);
      rows.push({ category: cat, depth, childCount: (childrenOf.get(cat.id) || []).length });
      walk(cat.id, depth + 1);
    });
  };
  walk(null, 0);
  // Safety net for cycles: anything not reached is listed at the top level
  categories.forEach(cat => {
    if (!visited.has(cat.id)) rows.push({ category: cat, depth: 0, childCount: (childrenOf.get(cat.id) || []).length });
  });
  return rows;
};

export default function AdminCategoriesPage() {
  useEffect(() => { document.title = 'Admin - Categories'; }, []);

  const [categories, setCategories]     = useState([]);
  const [loading, setLoading]           = useState(true);
  const [submitting, setSubmitting]     = useState(false);
  const [deleting, setDeleting]         = useState(false);
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
    if (!parentId) return null;
    const parent = categories.find(c => c.id === parentId);
    return parent ? parent.name : parentId;
  };

  const matchesSearch = (cat) =>
    cat.name?.toLowerCase().includes(searchQuery.toLowerCase()) ||
    cat.description?.toLowerCase().includes(searchQuery.toLowerCase());

  const rows = buildTreeRows(categories).filter(row => matchesSearch(row.category));

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
    setDeleting(true);
    try {
      const res = await categoryService.delete(deleteTarget.id);
      if (res.ok) { window.showSuccess('Category deleted successfully'); fetchCategories(); }
      else window.showError(res.data?.message || 'Failed to delete category');
    } catch { window.showError('Server connection error'); }
    finally  { setDeleting(false); setDeleteTarget(null); }
  };

  const closeModal = () => setModal(null);
  const editing = modal?.mode === 'edit' ? modal.category : null;

  return (
    <div className="admin-ui-page admin-categories-page">
      <PageHeader
        eyebrow="Catalog"
        eyebrowIcon="bi-box-seam"
        title="Categories"
        description="Organize products into a category hierarchy. Sub-categories are shown under their parent."
        actions={
          <button type="button" className="admin-ui-btn admin-ui-btn--primary" onClick={() => setModal('create')}>
            <i className="bi bi-plus-lg"></i> New category
          </button>
        }
      />

      {error && (
        <div className="admin-categories-alert" role="alert">
          <i className="bi bi-exclamation-circle"></i> {error}
        </div>
      )}

      <div className="admin-ui-toolbar">
        <SearchBox value={searchQuery} onChange={setSearchQuery} placeholder="Search by name or description…" />
        <span className="admin-ui-muted admin-categories-count">
          {searchQuery ? `${rows.length} of ${categories.length}` : categories.length} categor{categories.length === 1 ? 'y' : 'ies'}
        </span>
      </div>

      <div className="admin-ui-card">
        <div className="admin-ui-table-wrap">
          <table className="admin-ui-table admin-categories-table">
            <thead>
              <tr>
                <th>Category</th>
                <th>Parent</th>
                <th>Description</th>
                <th>Sub-categories</th>
                <th className="admin-ui-col-actions"><span className="visually-hidden">Actions</span></th>
              </tr>
            </thead>
            <tbody>
              {loading && categories.length === 0 && [...Array(5)].map((_, i) => (
                <tr key={i} className="admin-ui-skeleton-row"><td colSpan={5}><span></span></td></tr>
              ))}
              {rows.map(({ category: cat, depth, childCount }) => {
                const parentName = getParentName(cat.parentId);
                return (
                  <tr key={cat.id} onClick={() => setModal({ mode: 'edit', category: cat })}>
                    <td>
                      <div
                        className="admin-categories-identity"
                        style={{ paddingLeft: searchQuery ? 0 : `${Math.min(depth, 6) * 1.6}rem` }}
                      >
                        {depth > 0 && !searchQuery && <i className="bi bi-arrow-return-right admin-categories-branch"></i>}
                        <span className="admin-categories-thumb">
                          {cat.image
                            ? <img src={cat.image} alt={cat.name} />
                            : <i className="bi bi-image"></i>}
                        </span>
                        <strong>{cat.name}</strong>
                      </div>
                    </td>
                    <td>
                      {parentName
                        ? <span className="admin-categories-parent"><i className="bi bi-diagram-2"></i> {parentName}</span>
                        : <span className="admin-ui-tag">Top level</span>}
                    </td>
                    <td className="admin-ui-muted admin-categories-description">
                      {cat.description ? truncate(cat.description) : '—'}
                    </td>
                    <td>
                      <span className="admin-ui-tag admin-categories-children">
                        <i className="bi bi-folder2"></i> {childCount}
                      </span>
                    </td>
                    <td className="admin-ui-col-actions" onClick={(e) => e.stopPropagation()}>
                      <button type="button" className="admin-ui-icon-btn" title="Edit" onClick={() => setModal({ mode: 'edit', category: cat })}>
                        <i className="bi bi-pencil"></i>
                      </button>
                      <button type="button" className="admin-ui-icon-btn admin-ui-icon-btn--danger" title="Delete" onClick={() => setDeleteTarget(cat)}>
                        <i className="bi bi-trash3"></i>
                      </button>
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
          {!loading && rows.length === 0 && (
            <EmptyState
              icon="bi-diagram-3"
              title={searchQuery ? 'No matching categories found' : 'No categories yet'}
              text={searchQuery ? 'Try another search.' : 'Use "New category" to add the first one.'}
            />
          )}
        </div>
      </div>

      {(modal === 'create' || editing) && (
        <Modal
          title={editing ? 'Edit category' : 'New category'}
          subtitle={editing ? editing.name : 'Add a category and optionally place it under a parent.'}
          onClose={closeModal}
          width={720}
        >
          <div className="admin-categories-form-scope">
            {editing
              ? <CategoryForm category={editing} categories={categories.filter(c => c.id !== editing.id)} onSubmit={handleUpdate} onCancel={closeModal} loading={submitting} />
              : <CategoryForm categories={categories} onSubmit={handleCreate} onCancel={closeModal} loading={submitting} />}
          </div>
        </Modal>
      )}

      {deleteTarget && (
        <ConfirmDialog
          title="Delete category"
          message={<>Are you sure you want to delete category <strong>{deleteTarget.name}</strong>?</>}
          busy={deleting}
          onConfirm={handleDelete}
          onClose={() => setDeleteTarget(null)}
        />
      )}
    </div>
  );
}
