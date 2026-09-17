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
    description: product?.description || '',
    brandId: product?.brandId || product?.brand?.id || '',
    categoryIds: product?.categoryIds || product?.categories?.map((c) => c.id) || [],
    status: product?.status || defaultStatus,
    options: product?.options?.map((opt, idx) => ({ ...opt, tempId: idx })) || [],
    variants: product?.variants?.map((v) => ({
      ...v,
      tempId: v.tempId || Date.now() + Math.random(),
      images: v.images || [],
    })) || [
      {
        tempId: Date.now(),
        name: '',
        price: '',
        stock: '',
        sku: '',
        optionIds: [],
        images: [],
      },
    ],
  });

  // Track next available option tempId
  const [nextOptionTempId, setNextOptionTempId] = useState(
    product?.options?.length || 0
  );

  // Store file input refs for each variant
  const variantFileInputRefs = useRef({});

  const [errors, setErrors] = useState({});

  // ── Field change ──────────────────────────────────────────────
  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
    if (errors[name]) setErrors((prev) => ({ ...prev, [name]: '' }));
  };

  // ── Variant Image handling ────────────────────────────────────
  const handleAddVariantImages = (variantIdx, e) => {
    const files = Array.from(e.target.files);
    // Reset input so same file can be re-added after removal
    e.target.value = '';

    files.forEach((file) => {
      if (!file.type.startsWith('image/')) return;
      if (file.size > 5 * 1024 * 1024) return;

      const reader = new FileReader();
      reader.onloadend = () => {
        setFormData((prev) => {
          const updatedVariants = [...prev.variants];
          updatedVariants[variantIdx] = {
            ...updatedVariants[variantIdx],
            images: [
              ...updatedVariants[variantIdx].images,
              { type: 'new', file, preview: reader.result },
            ],
          };
          return { ...prev, variants: updatedVariants };
        });
      };
      reader.readAsDataURL(file);
    });
  };

  const handleRemoveVariantImage = (variantIdx, imageIdx) => {
    setFormData((prev) => {
      const updatedVariants = [...prev.variants];
      updatedVariants[variantIdx] = {
        ...updatedVariants[variantIdx],
        images: updatedVariants[variantIdx].images.filter((_, i) => i !== imageIdx),
      };
      return { ...prev, variants: updatedVariants };
    });
  };

  // ── Validation ────────────────────────────────────────────────
  const validate = () => {
    const newErrors = {};
    if (!formData.name.trim()) newErrors.name = 'Product name is required';
    if (!formData.brandId) newErrors.brandId = 'Please select a brand';
    
    // Validate variants
    if (!formData.variants || formData.variants.length === 0) {
      newErrors.variants = 'At least one variant is required';
    } else {
      formData.variants.forEach((v, idx) => {
        if (!v.name?.trim()) newErrors[`variant_${idx}_name`] = 'Variant name is required';
        if (v.price === '' || isNaN(Number(v.price)) || Number(v.price) < 0)
          newErrors[`variant_${idx}_price`] = 'Invalid price (must be ≥ 0)';
        if (v.stock === '' || isNaN(Number(v.stock)) || Number(v.stock) < 0)
          newErrors[`variant_${idx}_stock`] = 'Invalid stock (must be ≥ 0)';
        if (!v.sku?.trim()) newErrors[`variant_${idx}_sku`] = 'SKU is required';
      });
    }
    
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validate()) return;
    
    const serverErrors = await onSubmit({ 
      ...formData, 
      options: formData.options,
      variants: formData.variants,
    });
    if (serverErrors && typeof serverErrors === 'object') {
      // Show general error in toast if no field-level errors
      if (serverErrors._) {
        window.showError(serverErrors._);
      }
      // Merge field errors — backend uses camelCase field names matching formData keys
      const { _: _ignored, ...fieldErrors } = serverErrors;
      if (Object.keys(fieldErrors).length > 0) {
        setErrors((prev) => ({ ...prev, ...fieldErrors }));
      }
    }
  };

  // ── Options Handlers ──────────────────────────────────────────
  const handleAddOption = () => {
    const newOption = {
      tempId: nextOptionTempId,
      name: '',
      value: '',
    };
    setFormData((prev) => ({
      ...prev,
      options: [...prev.options, newOption],
    }));
    setNextOptionTempId((prev) => prev + 1);
  };

  const handleRemoveOption = (index) => {
    setFormData((prev) => {
      const removedOptionTempId = prev.options[index].tempId;
      const updatedOptions = prev.options.filter((_, i) => i !== index);
      // Remove this option's ID from all variants
      const updatedVariants = prev.variants.map((variant) => ({
        ...variant,
        optionIds: variant.optionIds.filter((id) => id !== removedOptionTempId),
      }));
      return { ...prev, options: updatedOptions, variants: updatedVariants };
    });
  };

  const handleOptionChange = (index, field, value) => {
    setFormData((prev) => {
      const updatedOptions = [...prev.options];
      updatedOptions[index] = { ...updatedOptions[index], [field]: value };
      return { ...prev, options: updatedOptions };
    });
    if (errors[`option_${index}_${field}`]) {
      setErrors((prev) => ({ ...prev, [`option_${index}_${field}`]: '' }));
    }
  };

  // ── Variants Handlers ─────────────────────────────────────────
  const handleAddVariant = () => {
    const newVariant = {
      tempId: Date.now() + Math.random(),
      name: '',
      price: '',
      stock: '',
      sku: '',
      optionIds: [],
      images: [],
    };
    setFormData((prev) => ({
      ...prev,
      variants: [...prev.variants, newVariant],
    }));
  };

  const handleRemoveVariant = (index) => {
    if (formData.variants.length > 1) {
      setFormData((prev) => ({
        ...prev,
        variants: prev.variants.filter((_, i) => i !== index),
      }));
    }
  };

  const handleVariantChange = (index, field, value) => {
    setFormData((prev) => {
      const updatedVariants = [...prev.variants];
      updatedVariants[index] = { ...updatedVariants[index], [field]: value };
      return { ...prev, variants: updatedVariants };
    });
    if (errors[`variant_${index}_${field}`]) {
      setErrors((prev) => ({ ...prev, [`variant_${index}_${field}`]: '' }));
    }
  };

  const handleVariantOptionToggle = (variantIndex, optionTempId) => {
    setFormData((prev) => {
      const updatedVariants = [...prev.variants];
      const variant = updatedVariants[variantIndex];
      
      if (variant.optionIds.includes(optionTempId)) {
        variant.optionIds = variant.optionIds.filter((id) => id !== optionTempId);
      } else {
        variant.optionIds = [...variant.optionIds, optionTempId];
      }
      
      return { ...prev, variants: updatedVariants };
    });
  };

  // ── Helpers ───────────────────────────────────────────────────
  const formatStatus = (s) =>
    s.split('_').map((w) => w.charAt(0) + w.slice(1).toLowerCase()).join(' ');

  return (
    <div className="product-form-container">
      <form onSubmit={handleSubmit} className="product-form">
        <h2>{product ? 'Edit Product' : 'Add New Product'}</h2>

        <div className="form-columns">
          {/* Left Column - Basic Information */}
          <div className="form-column-left">
            {/* Name */}
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
          </div>

          {/* Middle Column - Selection Fields */}
          <div className="form-column-middle">
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
          </div>

          {/* Right Column - Description */}
          <div className="form-column-right">
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
          </div>
        </div>

        {/* Options Section */}
        <div className="form-section">
          <div className="section-header">
            <h3>Product Options</h3>
            <button
              type="button"
              className="btn btn-secondary btn-sm"
              onClick={handleAddOption}
            >
              + Add Option
            </button>
          </div>

          {formData.options.length === 0 ? (
            <p className="empty-message">No options added yet. Click "Add Option" to create one.</p>
          ) : (
            <div className="options-list">
              {formData.options.map((option, idx) => (
                <div key={option.tempId || idx} className="option-item">
                  <div className="option-fields">
                    <div className="form-group">
                      <label htmlFor={`option-name-${idx}`}>Option Name *</label>
                      <input
                        id={`option-name-${idx}`}
                        type="text"
                        placeholder="e.g., Color, Size"
                        value={option.name}
                        onChange={(e) => handleOptionChange(idx, 'name', e.target.value)}
                        className={errors[`option_${idx}_name`] ? 'error' : ''}
                      />
                      {errors[`option_${idx}_name`] && (
                        <span className="error-message">{errors[`option_${idx}_name`]}</span>
                      )}
                    </div>

                    <div className="form-group">
                      <label htmlFor={`option-value-${idx}`}>Option Value *</label>
                      <input
                        id={`option-value-${idx}`}
                        type="text"
                        placeholder="e.g., Red, Medium"
                        value={option.value}
                        onChange={(e) => handleOptionChange(idx, 'value', e.target.value)}
                        className={errors[`option_${idx}_value`] ? 'error' : ''}
                      />
                      {errors[`option_${idx}_value`] && (
                        <span className="error-message">{errors[`option_${idx}_value`]}</span>
                      )}
                    </div>
                  </div>

                  <button
                    type="button"
                    className="btn btn-danger btn-sm"
                    onClick={() => handleRemoveOption(idx)}
                  >
                    Remove
                  </button>
                </div>
              ))}
            </div>
          )}
        </div>

        {/* Variants Section */}
        <div className="form-section">
          <div className="section-header">
            <h3>Product Variants</h3>
            <button
              type="button"
              className="btn btn-secondary btn-sm"
              onClick={handleAddVariant}
            >
              + Add Variant
            </button>
          </div>

          {errors.variants && <span className="error-message">{errors.variants}</span>}

          {formData.variants.length === 0 ? (
            <p className="empty-message">No variants added.</p>
          ) : (
            <div className="variants-list">
              {formData.variants.map((variant, idx) => (
                <div key={variant.tempId || idx} className="variant-item">
                  <div className="variant-header">
                    <span className="variant-index">Variant {idx + 1}</span>
                    <button
                      type="button"
                      className="btn btn-danger btn-sm"
                      onClick={() => handleRemoveVariant(idx)}
                      disabled={formData.variants.length === 1}
                      title={formData.variants.length === 1 ? 'At least one variant is required' : ''}
                    >
                      Remove
                    </button>
                  </div>

                  <div className="variant-fields">
                    <div className="form-group">
                      <label htmlFor={`variant-name-${idx}`}>Variant Name *</label>
                      <input
                        id={`variant-name-${idx}`}
                        type="text"
                        placeholder="e.g., 16GB RAM / 512GB SSD"
                        value={variant.name}
                        onChange={(e) => handleVariantChange(idx, 'name', e.target.value)}
                        className={errors[`variant_${idx}_name`] ? 'error' : ''}
                      />
                      {errors[`variant_${idx}_name`] && (
                        <span className="error-message">{errors[`variant_${idx}_name`]}</span>
                      )}
                    </div>

                    <div className="form-group">
                      <label htmlFor={`variant-price-${idx}`}>Price ($) *</label>
                      <input
                        id={`variant-price-${idx}`}
                        type="number"
                        placeholder="0"
                        value={variant.price}
                        onChange={(e) => handleVariantChange(idx, 'price', e.target.value)}
                        min="0"
                        step="0.01"
                        className={errors[`variant_${idx}_price`] ? 'error' : ''}
                      />
                      {errors[`variant_${idx}_price`] && (
                        <span className="error-message">{errors[`variant_${idx}_price`]}</span>
                      )}
                    </div>

                    <div className="form-group">
                      <label htmlFor={`variant-stock-${idx}`}>Stock *</label>
                      <input
                        id={`variant-stock-${idx}`}
                        type="number"
                        placeholder="0"
                        value={variant.stock}
                        onChange={(e) => handleVariantChange(idx, 'stock', e.target.value)}
                        min="0"
                        className={errors[`variant_${idx}_stock`] ? 'error' : ''}
                      />
                      {errors[`variant_${idx}_stock`] && (
                        <span className="error-message">{errors[`variant_${idx}_stock`]}</span>
                      )}
                    </div>

                    <div className="form-group">
                      <label htmlFor={`variant-sku-${idx}`}>SKU *</label>
                      <input
                        id={`variant-sku-${idx}`}
                        type="text"
                        placeholder="e.g., VAR_001"
                        value={variant.sku}
                        onChange={(e) => handleVariantChange(idx, 'sku', e.target.value)}
                        className={errors[`variant_${idx}_sku`] ? 'error' : ''}
                      />
                      {errors[`variant_${idx}_sku`] && (
                        <span className="error-message">{errors[`variant_${idx}_sku`]}</span>
                      )}
                    </div>
                  </div>

                  {/* Variant Images */}
                  <div className="variant-images">
                    <label>
                      Variant Images
                      <span className="image-count"> ({variant.images.length} selected)</span>
                    </label>

                    {/* Hidden input */}
                    <input
                      ref={(el) => {
                        if (el) variantFileInputRefs.current[idx] = el;
                      }}
                      type="file"
                      accept="image/*"
                      multiple
                      onChange={(e) => handleAddVariantImages(idx, e)}
                      className="image-input-hidden"
                    />

                    {/* Image grid */}
                    {variant.images.length > 0 && (
                      <div className="product-image-grid">
                        {variant.images.map((img, imgIdx) => (
                          <div key={imgIdx} className="product-image-item">
                            <img
                              src={img.type === 'existing' ? img.url : img.preview}
                              alt={`variant-${idx}-${imgIdx}`}
                              className="product-image-thumb"
                            />
                            {img.type === 'new' && (
                              <span className="image-new-badge">New</span>
                            )}
                            <button
                              type="button"
                              className="image-remove-btn"
                              onClick={() => handleRemoveVariantImage(idx, imgIdx)}
                              aria-label="Remove image"
                            >
                              ×
                            </button>
                          </div>
                        ))}

                        {/* Add more tile */}
                        <div
                          className="product-image-add"
                          onClick={() => variantFileInputRefs.current[idx]?.click()}
                          role="button"
                          tabIndex={0}
                          onKeyDown={(e) =>
                            e.key === 'Enter' && variantFileInputRefs.current[idx]?.click()
                          }
                        >
                          <span className="add-icon">+</span>
                          <span>Add more</span>
                        </div>
                      </div>
                    )}

                    {/* Empty state */}
                    {variant.images.length === 0 && (
                      <div
                        className="upload-placeholder"
                        onClick={() => variantFileInputRefs.current[idx]?.click()}
                        role="button"
                        tabIndex={0}
                        onKeyDown={(e) =>
                          e.key === 'Enter' && variantFileInputRefs.current[idx]?.click()
                        }
                      >
                        <div className="upload-icon">🖼️</div>
                        <p>Click to add variant images</p>
                        <small>JPG, PNG, GIF (max 5MB each)</small>
                      </div>
                    )}
                  </div>

                  {/* Options Selection */}
                  {formData.options.length > 0 && (
                    <div className="variant-options">
                      <label>Product Options</label>
                      <div className="options-checkboxes">
                        {formData.options.map((option) => (
                          <label key={option.tempId} className="option-checkbox">
                            <input
                              type="checkbox"
                              checked={variant.optionIds.includes(option.tempId)}
                              onChange={() =>
                                handleVariantOptionToggle(idx, option.tempId)
                              }
                            />
                            <span>
                              {option.name}: {option.value}
                            </span>
                          </label>
                        ))}
                      </div>
                    </div>
                  )}
                </div>
              ))}
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
