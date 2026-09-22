// src/pages/admin/products/AdminProductsPage.jsx
import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { productService } from '../../../services/productService';
import '../shared/AdminPage.css';
import './AdminProductsPage.css';

export default function AdminProductsPage() {
  useEffect(() => { document.title = 'Admin - Products'; }, []);

  const navigate = useNavigate();
  const [allProducts, setAllProducts] = useState([]);
  const [filteredProducts, setFilteredProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [deleteTarget, setDeleteTarget] = useState(null);
  const [detailsTarget, setDetailsTarget] = useState(null);
  const [detailProduct, setDetailProduct] = useState(null);
  const [loadingDetail, setLoadingDetail] = useState(false);
  
  // Search & Filter
  const [searchQuery, setSearchQuery] = useState('');
  const [statusFilter, setStatusFilter] = useState('ALL');
  const [brandFilter, setBrandFilter] = useState('ALL');
  const [brands, setBrands] = useState([]);
  
  // Pagination
  const [currentPage, setCurrentPage] = useState(0);
  const [pageSize, setPageSize] = useState(10);
  
  // Selection
  const [selectedIds, setSelectedIds] = useState(new Set());

  // Fetch all products
  const fetchAll = async () => {
    try {
      setLoading(true);
      const res = await productService.getAll({ page: 0, size: 999 }); // Get all for client-side filtering
      setAllProducts(res.data?.data || []);
      
      // Extract unique brands
      const uniqueBrands = new Set();
      (res.data?.data || []).forEach(p => {
        if (p.brand?.name) uniqueBrands.add(p.brand.name);
      });
      setBrands(Array.from(uniqueBrands).sort());
    } catch {
      setError('Failed to load products');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchAll(); }, []);

  // Apply filters
  useEffect(() => {
    let filtered = [...allProducts];

    // Search filter
    if (searchQuery.trim()) {
      const q = searchQuery.toLowerCase();
      filtered = filtered.filter(p =>
        p.name?.toLowerCase().includes(q) ||
        p.id?.toLowerCase().includes(q) ||
        p.brand?.name?.toLowerCase().includes(q)
      );
    }

    // Status filter
    if (statusFilter !== 'ALL') {
      filtered = filtered.filter(p => p.status === statusFilter);
    }

    // Brand filter
    if (brandFilter !== 'ALL') {
      filtered = filtered.filter(p => p.brand?.name === brandFilter);
    }

    setFilteredProducts(filtered);
    setCurrentPage(0);
  }, [searchQuery, statusFilter, brandFilter, allProducts]);

  const paginatedData = filteredProducts.slice(currentPage * pageSize, (currentPage + 1) * pageSize);
  const totalPages = Math.ceil(filteredProducts.length / pageSize) || 1;

  const handleDelete = async () => {
    if (!deleteTarget) return;
    try {
      const res = await productService.delete(deleteTarget.id);
      if (res.ok) {
        window.showSuccess('Product deleted successfully');
        fetchAll();
      } else {
        window.showError(res.data?.message || 'Failed to delete product');
      }
    } catch {
      window.showError('Server connection error');
    } finally {
      setDeleteTarget(null);
    }
  };

  const handleViewDetails = async (product) => {
    console.log('handleViewDetails called with product:', product);
    setDetailsTarget(product);
    setLoadingDetail(true);
    try {
      console.log('Fetching product details for ID:', product.id);
      const res = await productService.getById(product.id);
      console.log('API response:', res);
      if (res.ok) {
        console.log('Setting detailProduct with data:', res.data);
        // Handle both response formats: { success: true, data: {...} } or direct {...}
        const productData = res.data?.data || res.data;
        setDetailProduct(productData);
      } else {
        console.error('API error:', res);
        window.showError(res.data?.message || 'Failed to fetch product details');
        setDetailProduct(product); // Fallback to table data
      }
    } catch (error) {
      console.error('Exception in handleViewDetails:', error);
      window.showError('Server connection error');
      setDetailProduct(product); // Fallback to table data
    } finally {
      setLoadingDetail(false);
    }
  };

  const getStatusBadge = (status) => {
    switch(status) {
      case 'PUBLISHED':
        return (
          <span className="status-badge status-badge-published">
            <span className="status-dot"></span> PUBLISHED
          </span>
        );
      case 'DRAFT':
        return (
          <span className="status-badge status-badge-draft">
            <span className="status-dot"></span> DRAFT
          </span>
        );
      case 'ARCHIVED':
        return (
          <span className="status-badge status-badge-archived">
            <span className="status-dot"></span> ARCHIVED
          </span>
        );
      default:
        return <span className="status-badge">{status}</span>;
    }
  };

  const getCountStats = () => {
    const published = allProducts.filter(p => p.status === 'PUBLISHED').length;
    const draft = allProducts.filter(p => p.status === 'DRAFT').length;
    return { published, draft, total: allProducts.length };
  };

  const stats = getCountStats();

  const groupOptionsByName = (options = []) => {
    const result = {};
    options.forEach(opt => {
      if (!result[opt.name]) result[opt.name] = [];
      if (!result[opt.name].includes(opt.value)) {
        result[opt.name].push(opt.value);
      }
    });
    return result;
  };

  const toggleSelectProduct = (id) => {
    const newSet = new Set(selectedIds);
    if (newSet.has(id)) {
      newSet.delete(id);
    } else {
      newSet.add(id);
    }
    setSelectedIds(newSet);
  };

  const toggleSelectAll = () => {
    if (selectedIds.size === paginatedData.length && paginatedData.length > 0) {
      setSelectedIds(new Set());
    } else {
      const newSet = new Set(paginatedData.map(p => p.id));
      setSelectedIds(newSet);
    }
  };

  if (loading) {
    return (
      <div className="admin-page">
        <div className="admin-loading">
          <div className="admin-spinner"></div>
          <span>Loading...</span>
        </div>
      </div>
    );
  }

  return (
    <div className="products-page">
      {/* Header */}
      <div className="products-header light-card">
        <div className="header-left">
          <div className="header-icon">
            <i className="fa-solid fa-boxes-stacked"></i>
          </div>
          <div>
            <h1 className="header-title">Product Management</h1>
            <p className="header-subtitle">Manage catalog, variant options, inventory & product pricing</p>
          </div>
        </div>

        {/* Stats Badges */}
        <div className="header-stats">
          <div className="stat-badge">
            <span className="stat-dot published"></span>
            <span className="stat-label">Published:</span>
            <span className="stat-value">{stats.published}</span>
          </div>
          <div className="stat-badge">
            <span className="stat-dot draft"></span>
            <span className="stat-label">Drafts:</span>
            <span className="stat-value">{stats.draft}</span>
          </div>
          <div className="stat-badge">
            <span className="stat-dot total"></span>
            <span className="stat-label">Total:</span>
            <span className="stat-value">{stats.total}</span>
          </div>
        </div>
      </div>

      {error && <div className="admin-alert admin-alert-error">{error}</div>}

      {/* Toolbar */}
      <div className="products-toolbar light-card">
        <div className="toolbar-filters">
          {/* Search */}
          <div className="search-input-wrapper">
            <i className="fa-solid fa-magnifying-glass"></i>
            <input
              type="text"
              placeholder="Search by name, product ID..."
              value={searchQuery}
              onChange={e => setSearchQuery(e.target.value)}
              className="search-input"
            />
          </div>

          {/* Status Filter */}
          <select
            value={statusFilter}
            onChange={e => setStatusFilter(e.target.value)}
            className="filter-select"
          >
            <option value="ALL">All Statuses</option>
            <option value="PUBLISHED">PUBLISHED</option>
            <option value="DRAFT">DRAFT</option>
            <option value="ARCHIVED">ARCHIVED</option>
          </select>

          {/* Brand Filter */}
          <select
            value={brandFilter}
            onChange={e => setBrandFilter(e.target.value)}
            className="filter-select"
          >
            <option value="ALL">All Brands</option>
            {brands.map(b => (
              <option key={b} value={b}>{b}</option>
            ))}
          </select>

          {/* Reset Button */}
          {(searchQuery || statusFilter !== 'ALL' || brandFilter !== 'ALL') && (
            <button
              className="reset-button"
              onClick={() => {
                setSearchQuery('');
                setStatusFilter('ALL');
                setBrandFilter('ALL');
              }}
            >
              <i className="fa-solid fa-rotate-left"></i> Reset filters
            </button>
          )}
        </div>

        {/* Add New Button */}
        <button
          className="btn-add-new"
          onClick={() => navigate('/admin/products/create')}
        >
          <i className="fa-solid fa-plus"></i>
          <span>Add New Product</span>
        </button>
      </div>

      {/* Table Container */}
      <div className="products-table-container light-card">
        <div className="table-wrapper">
          <table className="products-table">
            <thead>
              <tr>
                <th className="col-checkbox">
                  <input
                    type="checkbox"
                    checked={selectedIds.size === paginatedData.length && paginatedData.length > 0}
                    onChange={toggleSelectAll}
                  />
                </th>
                <th className="col-product">Product</th>
                <th className="col-brand">Brand</th>
                <th className="col-status">Status</th>
                <th className="col-options">Options</th>
                <th className="col-variants">Variants & Stock</th>
                <th className="col-actions">Actions</th>
              </tr>
            </thead>
            <tbody>
              {paginatedData.length === 0 ? (
                <tr>
                  <td colSpan="7" className="empty-state">
                    <i className="fa-solid fa-box-open"></i>
                    <p>No matching products found</p>
                    <span>Please try searching again or adjust your filter selection</span>
                  </td>
                </tr>
              ) : (
                paginatedData.map((prod) => {
                  const isSelected = selectedIds.has(prod.id);
                  const groupedOptions = groupOptionsByName(prod.options || []);
                  const firstVariant = prod.variants?.[0];
                  const defaultPrice = firstVariant ? `$${firstVariant.price.toLocaleString('en-US')}` : 'N/A';
                  const defaultStock = firstVariant?.stock || 0;

                  return (
                    <tr key={prod.id} className={isSelected ? 'selected' : ''}>
                      <td className="col-checkbox">
                        <input
                          type="checkbox"
                          checked={isSelected}
                          onChange={() => toggleSelectProduct(prod.id)}
                        />
                      </td>
                      <td className="col-product">
                        <div className="product-info">
                          <div className="product-image">
                            {firstVariant?.images?.[0] ? (
                              <img src={firstVariant.images[0].url} alt={prod.name} />
                            ) : (
                              <div className="no-image">No Image</div>
                            )}
                          </div>
                          <div>
                            <h4
                              className="product-name"
                              onClick={() => handleViewDetails(prod)}
                            >
                              {prod.name}
                            </h4>
                            <span className="product-id">ID: {prod.id}</span>
                          </div>
                        </div>
                      </td>
                      <td className="col-brand">
                        <div className="brand-cell">
                          <i className="fa-solid fa-copyright"></i>
                          <span>{prod.brand?.name || 'Uncategorized'}</span>
                        </div>
                      </td>
                      <td className="col-status">
                        {getStatusBadge(prod.status)}
                      </td>
                      <td className="col-options">
                        <div className="options-list">
                          {Object.keys(groupedOptions).length > 0 ? (
                            Object.entries(groupedOptions).map(([key, vals]) => (
                              <span key={key} className="option-tag">
                                <strong>{key}:</strong> {vals.join(', ')}
                              </span>
                            ))
                          ) : (
                            <span className="options-empty">Not configured</span>
                          )}
                        </div>
                      </td>
                      <td className="col-variants">
                        <div className="variants-info">
                          <div className="price-stock">
                            <span className="price">{defaultPrice}</span>
                            <span className={`stock ${defaultStock > 0 ? 'in-stock' : 'out-of-stock'}`}>
                              {defaultStock > 0 ? `Stock: ${defaultStock}` : 'Out of stock'}
                            </span>
                          </div>
                          <div className="variant-count">
                            <i className="fa-solid fa-layer-group"></i>
                            <span>{prod.variants?.length || 0} Variant SKUs</span>
                          </div>
                        </div>
                      </td>
                      <td className="col-actions">
                        <div className="action-buttons">
                          <button
                            className="btn-action btn-view"
                            title="View Details"
                            onClick={() => handleViewDetails(prod)}
                          >
                            <i className="fa-solid fa-eye"></i>
                          </button>
                          <button
                            className="btn-action btn-edit"
                            title="Edit"
                            onClick={() => navigate(`/admin/products/edit/${prod.id}`)}
                          >
                            <i className="fa-solid fa-pen-to-square"></i>
                          </button>
                          <button
                            className="btn-action btn-delete"
                            title="Delete"
                            onClick={() => setDeleteTarget(prod)}
                          >
                            <i className="fa-solid fa-trash-can"></i>
                          </button>
                        </div>
                      </td>
                    </tr>
                  );
                })
              )}
            </tbody>
          </table>
        </div>

        {/* Pagination */}
        {filteredProducts.length > 0 && (
          <div className="pagination-footer">
            <div className="pagination-info">
              <span>
                Showing <strong>{currentPage * pageSize + 1}</strong> - <strong>{Math.min((currentPage + 1) * pageSize, filteredProducts.length)}</strong> of <strong>{filteredProducts.length}</strong> products
              </span>
              <span className="separator">|</span>
              <select
                value={pageSize}
                onChange={e => {
                  setPageSize(Number(e.target.value));
                  setCurrentPage(0);
                }}
                className="page-size-select"
              >
                <option value="10">10 / page</option>
                <option value="20">20 / page</option>
                <option value="50">50 / page</option>
              </select>
            </div>

            <div className="pagination-controls">
              <button
                className="btn-pagination"
                disabled={currentPage === 0}
                onClick={() => setCurrentPage(p => p - 1)}
              >
                <i className="fa-solid fa-chevron-left"></i> Previous
              </button>

              <div className="pagination-numbers">
                {Array.from({ length: totalPages }, (_, i) => (
                  <button
                    key={i}
                    className={`page-number ${currentPage === i ? 'active' : ''}`}
                    onClick={() => setCurrentPage(i)}
                  >
                    {i + 1}
                  </button>
                ))}
              </div>

              <button
                className="btn-pagination"
                disabled={currentPage >= totalPages - 1}
                onClick={() => setCurrentPage(p => p + 1)}
              >
                Next <i className="fa-solid fa-chevron-right"></i>
              </button>
            </div>
          </div>
        )}
      </div>

      {/* Details Modal */}
      {detailsTarget && (
        <div className="admin-modal-overlay" onClick={() => { setDetailsTarget(null); setDetailProduct(null); }}>
          <div className="admin-modal details-modal" onClick={e => e.stopPropagation()}>
            {/* Header */}
            <div className="modal-header">
              <div className="modal-header-content">
                <h3>{detailProduct?.name || detailsTarget.name}</h3>
                <p className="modal-product-id">ID: {detailProduct?.id || detailsTarget.id}</p>
              </div>
              <button
                className="btn-close"
                onClick={() => { setDetailsTarget(null); setDetailProduct(null); }}
              >
                <i className="fa-solid fa-xmark"></i>
              </button>
            </div>

            {/* Body */}
            <div className="modal-body">
              {loadingDetail ? (
                <div className="modal-loading">
                  <div className="admin-spinner"></div>
                  <span>Loading product details...</span>
                </div>
              ) : detailProduct ? (
                <>
                  {/* Summary Grid */}
                  <div className="summary-grid">
                    <div className="summary-item">
                      <label>Brand</label>
                      <div className="brand-info">
                        {detailProduct.brand?.image && (
                          <img src={detailProduct.brand.image} alt={detailProduct.brand.name} className="brand-thumb" />
                        )}
                        <p>{detailProduct.brand?.name || 'Uncategorized'}</p>
                      </div>
                    </div>
                    <div className="summary-item">
                      <label>Status</label>
                      {getStatusBadge(detailProduct.status)}
                    </div>
                    <div className="summary-item">
                      <label>Total Variants</label>
                      <p>{detailProduct.variants?.length || 0} Variants</p>
                    </div>
                  </div>

                  {/* Description */}
                  {detailProduct.description && (
                    <div className="modal-section">
                      <h4>Description</h4>
                      <p className="description-text">{detailProduct.description}</p>
                    </div>
                  )}

                  {/* Brand Details */}
                  {detailProduct.brand && (
                    <div className="modal-section">
                      <h4>Brand Details</h4>
                      <div className="brand-details">
                        <div className="brand-row">
                          <label>Brand Name:</label>
                          <p>{detailProduct.brand.name}</p>
                        </div>
                        {detailProduct.brand.description && (
                          <div className="brand-row">
                            <label>Description:</label>
                            <p>{detailProduct.brand.description}</p>
                          </div>
                        )}
                      </div>
                    </div>
                  )}

                  {/* Categories */}
                  {detailProduct.categories && detailProduct.categories.length > 0 && (
                    <div className="modal-section">
                      <h4>Categories</h4>
                      <div className="categories-list">
                        {detailProduct.categories.map(cat => (
                          <div key={cat.id} className="category-item">
                            {cat.image && (
                              <img src={cat.image} alt={cat.name} className="category-thumb" />
                            )}
                            <div className="category-info">
                              <span className="category-name">{cat.name}</span>
                              {cat.description && (
                                <span className="category-desc">{cat.description}</span>
                              )}
                            </div>
                          </div>
                        ))}
                      </div>
                    </div>
                  )}

                  {/* Vendor */}
                  {detailProduct.vendor && (
                    <div className="modal-section">
                      <h4>Vendor</h4>
                      <div className="vendor-info">
                        <p>{detailProduct.vendor.name || 'No vendor assigned'}</p>
                      </div>
                    </div>
                  )}

                  {/* Options */}
                  <div className="modal-section">
                    <h4>Option Configurations</h4>
                    {Object.keys(groupOptionsByName(detailProduct.options || [])).length > 0 ? (
                      <div className="options-grid">
                        {Object.entries(groupOptionsByName(detailProduct.options || [])).map(([key, vals]) => (
                          <div key={key} className="option-config">
                            <label>{key}</label>
                            <div className="option-values">
                              {vals.map(v => (
                                <span key={v} className="value-tag">{v}</span>
                              ))}
                            </div>
                          </div>
                        ))}
                      </div>
                    ) : (
                      <p className="empty-text">No options configured</p>
                    )}
                  </div>

                  {/* Variants Table */}
                  <div className="modal-section">
                    <h4>Variant Details</h4>
                    {detailProduct.variants?.length > 0 ? (
                      <table className="variants-table">
                        <thead>
                          <tr>
                            <th>Variant</th>
                            <th>SKU</th>
                            <th>Stock</th>
                            <th>Unit Price</th>
                            <th>Options</th>
                            <th>Images</th>
                          </tr>
                        </thead>
                        <tbody>
                          {detailProduct.variants.map(v => (
                            <tr key={v.id}>
                              <td><i className="fa-solid fa-box"></i> {v.name}</td>
                              <td className="mono">{v.sku || 'N/A'}</td>
                              <td><strong className={v.stock > 0 ? 'in-stock' : 'out-of-stock'}>{v.stock} pcs</strong></td>
                              <td>${v.price.toFixed(2)}</td>
                              <td className="mono">[{v.optionList || 'N/A'}]</td>
                              <td>
                                {v.images && v.images.length > 0 ? (
                                  <div className="details-variant-images">
                                    {v.images.slice(0, 2).map((img, idx) => (
                                      <img key={idx} src={img} alt={`${v.name}-${idx}`} className="variant-thumb" />
                                    ))}
                                    {v.images.length > 2 && <span className="more-images">+{v.images.length - 2}</span>}
                                  </div>
                                ) : (
                                  <span className="no-images">No images</span>
                                )}
                              </td>
                            </tr>
                          ))}
                        </tbody>
                      </table>
                    ) : (
                      <p className="empty-text">No variants found</p>
                    )}
                  </div>
                </>
              ) : (
                <p className="empty-text">Failed to load product details</p>
              )}
            </div>

            {/* Footer */}
            <div className="modal-footer">
              <button
                className="btn-close-modal"
                onClick={() => { setDetailsTarget(null); setDetailProduct(null); }}
              >
                Close
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Delete Confirmation */}
      {deleteTarget && (
        <div className="admin-modal-overlay" onClick={() => setDeleteTarget(null)}>
          <div className="admin-modal admin-confirm-modal" onClick={e => e.stopPropagation()}>
            <h3>Confirm Delete</h3>
            <p>Are you sure you want to delete product <strong>{deleteTarget.name}</strong>?</p>
            <div className="confirm-actions">
              <button
                className="btn-cancel"
                onClick={() => setDeleteTarget(null)}
              >
                Cancel
              </button>
              <button
                className="btn-danger"
                onClick={handleDelete}
              >
                Delete
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
