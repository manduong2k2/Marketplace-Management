// src/components/brand/BrandForm.jsx
import React, { useState, useRef } from 'react';
import './BrandForm.css';

export default function BrandForm({ brand, onSubmit, onCancel, loading = false }) {
  const [formData, setFormData] = useState({
    name: brand?.name || '',
    description: brand?.description || '',
    image: brand?.image || '',
  });

  const [errors, setErrors] = useState({});
  const [imagePreview, setImagePreview] = useState(brand?.image || '');
  const fileInputRef = useRef(null);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
    if (errors[name]) {
      setErrors(prev => ({ ...prev, [name]: '' }));
    }
  };

  const handleImageChange = (e) => {
    const file = e.target.files[0];
    if (!file) return;

    if (!file.type.startsWith('image/')) {
      setErrors(prev => ({ ...prev, image: 'Please select an image file' }));
      return;
    }
    if (file.size > 5 * 1024 * 1024) {
      setErrors(prev => ({ ...prev, image: 'Image size must not exceed 5MB' }));
      return;
    }

    const reader = new FileReader();
    reader.onloadend = () => {
      setImagePreview(reader.result);
      setFormData(prev => ({
        ...prev,
        image: reader.result,
        imageFile: file,
      }));
      setErrors(prev => ({ ...prev, image: '' }));
    };
    reader.readAsDataURL(file);
  };

  const validateForm = () => {
    const newErrors = {};
    if (!formData.name.trim()) newErrors.name = 'Brand name is required';
    if (!formData.image) newErrors.image = 'Please select a brand image';
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validateForm()) return;
    const serverErrors = await onSubmit(formData);
    if (serverErrors && typeof serverErrors === 'object') {
      if (serverErrors._) window.showError(serverErrors._);
      const { _: _ignored, ...fieldErrors } = serverErrors;
      if (Object.keys(fieldErrors).length > 0) {
        setErrors((prev) => ({ ...prev, ...fieldErrors }));
      }
    }
  };

  const handleReset = () => {
    setFormData({ name: '', description: '', image: '' });
    setImagePreview('');
    setErrors({});
    if (fileInputRef.current) fileInputRef.current.value = '';
  };

  return (
    <div className="brand-form-container">
      <form onSubmit={handleSubmit} className="brand-form">
        <h2>{brand ? 'Edit Brand' : 'Add New Brand'}</h2>

        <div className="form-group">
          <label htmlFor="brand-name">Brand Name *</label>
          <input
            type="text"
            id="brand-name"
            name="name"
            value={formData.name}
            onChange={handleChange}
            className={errors.name ? 'error' : ''}
            placeholder="Enter brand name"
          />
          {errors.name && <span className="error-message">{errors.name}</span>}
        </div>

        <div className="form-group">
          <label htmlFor="brand-description">Description</label>
          <textarea
            id="brand-description"
            name="description"
            value={formData.description}
            onChange={handleChange}
            placeholder="Enter brand description"
            rows="4"
            className="form-textarea"
          />
        </div>

        <div className="form-group">
          <label>Brand Image *</label>
          {/* Hidden real file input */}
          <input
            ref={fileInputRef}
            type="file"
            id="brand-image"
            accept="image/*"
            onChange={handleImageChange}
            className="image-input-hidden"
          />

          {imagePreview ? (
            <div className="image-preview">
              <img src={imagePreview} alt="Preview" className="preview-image" />
              <button
                type="button"
                className="remove-image-btn"
                onClick={() => {
                  setImagePreview('');
                  setFormData(prev => ({ ...prev, image: '', imageFile: undefined }));
                  if (fileInputRef.current) fileInputRef.current.value = '';
                }}
              >
                ×
              </button>
            </div>
          ) : (
            <div
              className="upload-placeholder"
              onClick={() => fileInputRef.current?.click()}
              role="button"
              tabIndex={0}
              onKeyDown={(e) => e.key === 'Enter' && fileInputRef.current?.click()}
            >
              <div className="upload-icon">📷</div>
              <p>Click to select brand image</p>
              <small>Supported: JPG, PNG, GIF (max 5MB)</small>
            </div>
          )}
          {errors.image && <span className="error-message">{errors.image}</span>}
        </div>

        <div className="form-actions">
          <button type="button" onClick={handleReset} className="btn btn-secondary" disabled={loading}>
            Reset
          </button>
          <button type="button" onClick={onCancel} className="btn btn-cancel" disabled={loading}>
            Cancel
          </button>
          <button type="submit" className="btn btn-primary" disabled={loading}>
            {loading ? 'Saving...' : brand ? 'Update' : 'Create'}
          </button>
        </div>
      </form>
    </div>
  );
}
