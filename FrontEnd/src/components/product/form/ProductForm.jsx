// src/components/product/form/ProductForm.jsx
import { useState, useRef } from 'react';
import CategoryTreeSelect from '../../category/tree/CategoryTreeSelect';
import './ProductForm.css';

export default function ProductForm({
  product,
  brands = [],
  categories = [],
  statuses = [],
  onSubmit,
  onCancel,
  loading = false,
}) {
  const defaultStatus = statuses.length > 0 ? statuses[0] : '';

  const [formData, setFormData] = useState({
    name: product?.name || '',
    code: product?.code || '',
    description: product?.description || '',
    price: product?.price ?? '',
    stock: product?.stock ?? '',
    brandId: product?.brandId || product?.brand?.id || '',
    categoryIds: product?.categoryIds || product?.categories?.map((c) => c.id) || [],
    status: product?.status || defaultStatus,
  });

  // Each entry: { type: 'existing', url } | { type: 'new', file, preview }
  const [images, setImages] = useState(() => {
    const existing = product?.images || [];
    return existing.map((url) => ({ type: 'existing', url }));
  });

  const [errors, setErrors] = useState({});
  const fileInputRef = useRef(null);

  // ── Field change ──────────────────────────────────────────────
  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
    if (errors[name]) setErrors((prev) => ({ ...prev, [name]: '' }));
  };

  // ── Image handling ─────────────────────────────────────────────
  const handleAddImages = (e) => {
    const files = Array.from(e.target.files);
    // Reset input so same file can be re-added after removal
    e.target.value = '';

    files.forEach((file) => {
      if (!file.type.startsWith('image/')) return;
      if (file.size > 5 * 1024 * 1024) return;

      const reader = new FileReader();
      reader.onloadend = () => {
        setImages((prev) => [...prev, { type: 'new', file, preview: reader.result }]);
      };
      reader.readAsDataURL(file);
    });
  };

  const handleRemoveImage = (index) => {
    setImages((prev) => prev.filter((_, i) => i !== index));
  };

  // ── Validation ────────────────────────────────────────────────
  const validate = () => {
    const newErrors = {};
    if (!formData.name.trim()) newErrors.name = 'Product name is required';
    if (!formData.code.trim()) newErrors.code = 'Product code is required';
    if (formData.price === '' || isNaN(Number(formData.price)) || Number(formData.price) < 0)
      newErrors.price = 'Invalid price (must be ≥ 0)';
    if (formData.stock === '' || isNaN(Number(formData.stock)) || Number(formData.stock) < 0)
      newErrors.stock = 'Invalid stock (must be ≥ 0)';
    if (!formData.brandId) newErrors.brandId = 'Please select a brand';
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validate()) return;
    const newImageFiles = images.filter((img) => img.type === 'new').map((img) => img.file);
    const keptImageUrls = images.filter((img) => img.type === 'existing').map((img) => img.url);
    const serverErrors = await onSubmit({ ...formData, imageFiles: newImageFiles, imageUrls: keptImageUrls });
    if (serverErrors && typeof serverErrors === 'object') {
      // Show general error in toast if no field-level errors
      if (serverErrors._ ) {
        window.showError(serverErrors._);
      }
      // Merge field errors — backend uses camelCase field names matching formData keys
      const { _: _ignored, ...fieldErrors } = serverErrors;
      if (Object.keys(fieldErrors).length > 0) {
        setErrors((prev) => ({ ...prev, ...fieldErrors }));
      }
    }
  };

  // ── Helpers ───────────────────────────────────────────────────
  const formatStatus = (s) =>
    s.split('_').map((w) => w.charAt(0) + w.slice(1).toLowerCase()).join(' ');

  return (
    <div className="product-form-container">
      <form onSubmit={handleSubmit} className="product-form">
        <h2>{product ? 'Edit Product' : 'Add New Product'}</h2>

        {/* Name + Code */}
        <div className="form-row">
          <div className="form-group">
            <label htmlFor="prod-name">Product Name *</label>
            <input
              id="prod-name"
              type="text"
              name="name"
              value={formData.name}
              onChange={handleChange}
              placeholder="Enter product name"
              className={errors.name ? 'error' : ''}
            />
            {errors.name && <span className="error-message">{errors.name}</span>}
          </div>

          <div className="form-group">
            <label htmlFor="prod-code">Product Code *</label>
            <input
              id="prod-code"
              type="text"
              name="code"
              value={formData.code}
              onChange={handleChange}
              placeholder="SKU001"
              className={errors.code ? 'error' : ''}
            />
            {errors.code && <span className="error-message">{errors.code}</span>}
          </div>
        </div>

        {/* Price + Stock */}
        <div className="form-row">
          <div className="form-group">
            <label htmlFor="prod-price">Price ($) *</label>
            <input
              id="prod-price"
              type="number"
              name="price"
              value={formData.price}
              onChange={handleChange}
              placeholder="0"
              min="0"
              step="0.01"
              className={errors.price ? 'error' : ''}
            />
            {errors.price && <span className="error-message">{errors.price}</span>}
          </div>

          <div className="form-group">
            <label htmlFor="prod-stock">Stock *</label>
            <input
              id="prod-stock"
              type="number"
              name="stock"
              value={formData.stock}
              onChange={handleChange}
              placeholder="0"
              min="0"
              className={errors.stock ? 'error' : ''}
            />
            {errors.stock && <span className="error-message">{errors.stock}</span>}
          </div>
        </div>

        {/* Brand + Status */}
        <div className="form-row">
          <div className="form-group">
            <label htmlFor="prod-brand">Brand *</label>
            <select
              id="prod-brand"
              name="brandId"
              value={formData.brandId}
              onChange={handleChange}
              className={`form-select ${errors.brandId ? 'error' : ''}`}
            >
              <option value="">-- Select a brand --</option>
              {brands.map((b) => (
                <option key={b.id} value={b.id}>{b.name}</option>
              ))}
            </select>
            {errors.brandId && <span className="error-message">{errors.brandId}</span>}
          </div>

          <div className="form-group">
            <label htmlFor="prod-status">Status</label>
            <select
              id="prod-status"
              name="status"
              value={formData.status}
              onChange={handleChange}
              className="form-select"
            >
              {statuses.length === 0 ? (
                <option value="">Loading...</option>
              ) : (
                statuses.map((s) => (
                  <option key={s} value={s}>{formatStatus(s)}</option>
                ))
              )}
            </select>
          </div>
        </div>

        {/* Description */}
        <div className="form-group">
          <label htmlFor="prod-desc">Description</label>
          <textarea
            id="prod-desc"
            name="description"
            value={formData.description}
            onChange={handleChange}
            placeholder="Enter product description"
            rows={4}
            className="form-textarea"
          />
        </div>

        {/* Categories */}
        {categories.length > 0 && (
          <div className="form-group">
            <label>
              Categories
              {formData.categoryIds.length > 0 && (
                <span className="cat-selected-summary"> — {formData.categoryIds.length} selected</span>
              )}
            </label>
            <CategoryTreeSelect
              categories={categories}
              selectedIds={formData.categoryIds}
              onChange={(ids) => setFormData((prev) => ({ ...prev, categoryIds: ids }))}
            />
          </div>
        )}

        {/* Images */}
        <div className="form-group">
          <label>
            Product Images
            <span className="image-count"> ({images.length} selected)</span>
          </label>

          {/* Hidden input */}
          <input
            ref={fileInputRef}
            type="file"
            accept="image/*"
            multiple
            onChange={handleAddImages}
            className="image-input-hidden"
          />

          {/* Image grid */}
          {images.length > 0 && (
            <div className="product-image-grid">
              {images.map((img, i) => (
                <div key={i} className="product-image-item">
                  <img
                    src={img.type === 'existing' ? img.url : img.preview}
                    alt={`product-${i + 1}`}
                    className="product-image-thumb"
                  />
                  {img.type === 'new' && (
                    <span className="image-new-badge">New</span>
                  )}
                  <button
                    type="button"
                    className="image-remove-btn"
                    onClick={() => handleRemoveImage(i)}
                    aria-label="Remove image"
                  >
                    ×
                  </button>
                </div>
              ))}

              {/* Add more tile */}
              <div
                className="product-image-add"
                onClick={() => fileInputRef.current?.click()}
                role="button"
                tabIndex={0}
                onKeyDown={(e) => e.key === 'Enter' && fileInputRef.current?.click()}
              >
                <span className="add-icon">+</span>
                <span>Add more</span>
              </div>
            </div>
          )}

          {/* Empty state */}
          {images.length === 0 && (
            <div
              className="upload-placeholder-wide"
              onClick={() => fileInputRef.current?.click()}
              role="button"
              tabIndex={0}
              onKeyDown={(e) => e.key === 'Enter' && fileInputRef.current?.click()}
            >
              <div className="upload-icon">🖼️</div>
              <p>Click to add product images</p>
              <small>Supported: JPG, PNG, GIF (max 5MB each)</small>
            </div>
          )}
        </div>

        <div className="form-actions">
          <button type="button" onClick={onCancel} className="btn btn-cancel" disabled={loading}>
            Cancel
          </button>
          <button type="submit" className="btn btn-primary" disabled={loading}>
            {loading ? 'Saving...' : product ? 'Update' : 'Create'}
          </button>
        </div>
      </form>
    </div>
  );
}
