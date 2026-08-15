// src/pages/admin/products/AdminProductsPage.jsx
import React, { useEffect, useState } from 'react';
import { productService } from '../../../services/productService';
import { brandService } from '../../../services/brandService';
import { categoryService } from '../../../services/categoryService';
import ProductForm from '../../../components/product/form/ProductForm';
import '../shared/AdminPage.css';

const API_URL = import.meta.env.VITE_API_URL;

export default function AdminProductsPage() {
  useEffect(() => { document.title = 'Admin - Products'; }, []);

  const [products, setProducts]         = useState([]);
  const [brands, setBrands]             = useState([]);
  const [categories, setCategories]     = useState([]);
  const [statuses, setStatuses]         = useState([]);
  const [loading, setLoading]           = useState(true);
  const [submitting, setSubmitting]     = useState(false);
  const [error, setError]               = useState(null);
  const [modal, setModal]               = useState(null);
  const [deleteTarget, setDeleteTarget] = useState(null);
  const [searchQuery, setSearchQuery]   = useState('');
  const [sortBy, setSortBy]             = useState('name');
  const [sortOrder, setSortOrder]       = useState('asc');
  const [pagination, setPagination]     = useState({
    currentPage: 0, pageSize: 10, totalElements: 0,
    totalPages: 0, hasNext: false, hasPrevious: false,
  });

  const fetchAll = async () => {
    try {
      setLoading(true);
      const params = { page: pagination.currentPage, size: pagination.pageSize, sortBy, sortOrder };
      if (searchQuery.trim()) params.search = searchQuery.trim();
      const [prodRes, brandRes, catRes, statusRes] = await Promise.all([
        productService.getAll(params), brandService.getAll(),
        categoryService.getAll(), productService.getStatuses(),
      ]);
      setProducts(prodRes.data?.data || []);
      setPagination(prodRes.data?.pagination || pagination);
      setBrands(brandRes.data?.data || []);
      setCategories(catRes.data?.data || []);
      setStatuses(statusRes.data?.data || []);
    } catch { setError('Failed to load data.'); }
    finally  { setLoading(false); }
  };

  useEffect(() => { fetchAll(); }, [pagination.currentPage, searchQuery, sortBy, sortOrder]);

  const buildFd = (formData, isUpdate = false) => {
    const fd = new FormData();
    ['name','code','description','price','stock','brandId','status'].forEach(k => fd.append(k, formData[k] ?? ''));
    formData.categoryIds?.forEach(id => fd.append('categoryIds', id));
    if (isUpdate) {
      if (formData.imageUrls?.length > 0) formData.imageUrls.forEach(url => fd.append('imageUrls', url));
      else fd.append('imageUrls', '');
    }
    formData.imageFiles?.forEach(file => fd.append('images', file));
    return fd;
  };

  const priceRange = (prod) => {
    if (!prod.variants?.length) return 'N/A';
    const prices = prod.variants.map(v => v.price);
    const min = Math.min(...prices), max = Math.max(...prices);
    return min === max ? `$${min.toLocaleString('en-US')}` : `$${min.toLocaleString('en-US')} – $${max.toLocaleString('en-US')}`;
  };

  const handleCreate = async (formData) => {
    setSubmitting(true);
    try {
      const res = await fetch(`${API_URL}/api/products`, { method: 'POST', credentials: 'include', body: buildFd(formData) });
      const data = await res.json();
      if (res.ok && data.success) { window.showSuccess('Product created successfully'); setModal(null); setPagination(p => ({ ...p, currentPage: 0 })); fetchAll(); }
      else return data.errors || { _: data.message || 'Failed to create product' };
    } catch { window.showError('Server connection error'); }
    finally   { setSubmitting(false); }
  };

  const handleUpdate = async (formData) => {
    setSubmitting(true);
    try {
      const res = await fetch(`${API_URL}/api/products/${modal.product.id}`, { method: 'PUT', credentials: 'include', body: buildFd(formData, true) });
      const data = await res.json();
      if (res.ok && data.success) { window.showSuccess('Product updated successfully'); setModal(null); fetchAll(); }
      else return data.errors || { _: data.message || 'Failed to update product' };
    } catch { window.showError('Server connection error'); }
    finally   { setSubmitting(false); }
  };

  const handleDelete = async () => {
    if (!deleteTarget) return;
    try {
      const res = await productService.delete(deleteTarget.id);
      if (res.ok) { window.showSuccess('Product deleted successfully'); setPagination(p => ({ ...p, currentPage: 0 })); fetchAll(); }
      else window.showError(res.data?.message || 'Failed to delete product');
    } catch { window.showError('Server connection error'); }
    finally  { setDeleteTarget(null); }
  };

  const getStatusBadge = (status) => {
    const label = status.split('_').map(w => w.charAt(0) + w.slice(1).toLowerCase()).join(' ');
    return <span className={`admin-badge ${status === 'PUBLISHED' ? 'admin-badge-active' : 'admin-badge-inactive'}`}>{label}</span>;
  };

  const handleSort = (field) => {
    if (sortBy === field) setSortOrder(o => o === 'asc' ? 'desc' : 'asc');
    else { setSortBy(field); setSortOrder('asc'); }
    setPagination(p => ({ ...p, currentPage: 0 }));
  };

  return (
    <div className="admin-page">
      <div className="admin-page-header">
        <h2 className="admin-page-title">📦 Products</h2>
        <div className="admin-header-actions">
          {pagination.totalElements > 0 && (
            <span className="pagination-info">Page {pagination.currentPage + 1}/{pagination.totalPages} ({pagination.totalElements} total)</span>
          )}
          <button className="btn-admin-primary" onClick={() => setModal('create')}>+ Add New</button>
        </div>
      </div>

      {error && <div className="admin-alert admin-alert-error">{error}</div>}

      <div className="admin-search-bar">
        <input className="admin-search-input" type="text" placeholder="Search products by name or description..." value={searchQuery}
          onChange={e => { setSearchQuery(e.target.value); setPagination(p => ({ ...p, currentPage: 0 })); }} />
        {searchQuery && <button className="admin-search-clear" onClick={() => { setSearchQuery(''); setPagination(p => ({ ...p, currentPage: 0 })); }}>✕</button>}
      </div>

      <div className="admin-sort-bar">
        <span className="sort-label">Sort by:</span>
        {['name','price','stock','status'].map(f => (
          <button key={f} className={`sort-btn ${sortBy === f ? 'active' : ''}`} onClick={() => handleSort(f)}>
            {f.charAt(0).toUpperCase() + f.slice(1)} {sortBy === f ? (sortOrder === 'asc' ? '↑' : '↓') : ''}
          </button>
        ))}
      </div>

      {loading ? (
        <div className="admin-loading"><div className="admin-spinner" /><span>Loading...</span></div>
      ) : (
        <div className="admin-table-wrapper">
          <table className="admin-table">
            <thead>
              <tr><th>#</th><th>Image</th><th>Product Name</th><th>Price</th><th>Variants</th><th>Brand</th><th>Status</th><th>Actions</th></tr>
            </thead>
            <tbody>
              {products.length === 0 ? (
                <tr><td colSpan={8} className="admin-table-empty">No products found</td></tr>
              ) : products.map((prod, idx) => (
                <tr key={prod.id}>
                  <td>{idx + 1}</td>
                  <td>{prod.variants[0]?.images?.length > 0 ? <img src={prod.variants[0].images[0]} alt={prod.name} className="admin-table-img" /> : <span className="admin-no-image">—</span>}</td>
                  <td className="admin-table-name">{prod.name}</td>
                  <td>{priceRange(prod)}</td>
                  <td>{prod.variants?.length}</td>
                  <td>{prod.brand}</td>
                  <td>{getStatusBadge(prod.status)}</td>
                  <td>
                    <div className="admin-action-btns">
                      <button className="btn-admin-edit"   onClick={() => setModal({ mode: 'edit', product: prod })}>✏️ Edit</button>
                      <button className="btn-admin-delete" onClick={() => setDeleteTarget(prod)}>🗑️ Delete</button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
          {pagination.totalPages > 1 && (
            <div className="admin-pagination">
              <button className="btn-admin-secondary" disabled={!pagination.hasPrevious} onClick={() => setPagination(p => ({ ...p, currentPage: p.currentPage - 1 }))}>Previous</button>
              <div className="pagination-numbers">
                {Array.from({ length: pagination.totalPages }, (_, i) => (
                  <button key={i} className={`pagination-number ${pagination.currentPage === i ? 'active' : ''}`} onClick={() => setPagination(p => ({ ...p, currentPage: i }))}>{i + 1}</button>
                ))}
              </div>
              <button className="btn-admin-secondary" disabled={!pagination.hasNext} onClick={() => setPagination(p => ({ ...p, currentPage: p.currentPage + 1 }))}>Next</button>
            </div>
          )}
        </div>
      )}

      {modal === 'create' && (
        <div className="admin-modal-overlay" onClick={() => setModal(null)}>
          <div className="admin-modal" onClick={e => e.stopPropagation()}>
            <ProductForm brands={brands} categories={categories} statuses={statuses} onSubmit={handleCreate} onCancel={() => setModal(null)} loading={submitting} />
          </div>
        </div>
      )}

      {modal?.mode === 'edit' && (
        <div className="admin-modal-overlay" onClick={() => setModal(null)}>
          <div className="admin-modal" onClick={e => e.stopPropagation()}>
            <ProductForm product={modal.product} brands={brands} categories={categories} statuses={statuses} onSubmit={handleUpdate} onCancel={() => setModal(null)} loading={submitting} />
          </div>
        </div>
      )}

      {deleteTarget && (
        <div className="admin-modal-overlay" onClick={() => setDeleteTarget(null)}>
          <div className="admin-modal admin-confirm-modal" onClick={e => e.stopPropagation()}>
            <h3>Confirm Delete</h3>
            <p>Are you sure you want to delete product <strong>{deleteTarget.name}</strong>?</p>
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
