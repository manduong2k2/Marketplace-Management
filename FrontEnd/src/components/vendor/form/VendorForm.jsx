// src/components/vendor/VendorForm.jsx
import React, { useState, useRef } from 'react';
import './VendorForm.css';

export default function VendorForm({ vendor, onSubmit, onCancel, loading = false }) {
  const [formData, setFormData] = useState({
    name: vendor?.name || '',
    email: vendor?.email || '',
    description: vendor?.description || '',
    phone: vendor?.phone || '',
    taxCode: vendor?.taxCode || '',
    logo: vendor?.logo || '',
    banner: vendor?.banner || '',
  });

  const [errors, setErrors] = useState({});
  const [logoPreview, setLogoPreview] = useState(vendor?.logo || '');
  const [bannerPreview, setBannerPreview] = useState(vendor?.banner || '');
  const logoInputRef = useRef(null);
  const bannerInputRef = useRef(null);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
    if (errors[name]) {
      setErrors(prev => ({ ...prev, [name]: '' }));
    }
  };

  const handleLogoChange = (e) => {
    const file = e.target.files[0];
    if (!file) return;

    if (!file.type.startsWith('image/')) {
      setErrors(prev => ({ ...prev, logo: 'Please select an image file' }));
      return;
    }
    if (file.size > 5 * 1024 * 1024) {
      setErrors(prev => ({ ...prev, logo: 'Image size must not exceed 5MB' }));
      return;
    }

    const reader = new FileReader();
    reader.onloadend = () => {
      setLogoPreview(reader.result);
      setFormData(prev => ({
        ...prev,
        logo: reader.result,
        logoFile: file,
      }));
      setErrors(prev => ({ ...prev, logo: '' }));
    };
    reader.readAsDataURL(file);
  };

  const handleBannerChange = (e) => {
    const file = e.target.files[0];
    if (!file) return;

    if (!file.type.startsWith('image/')) {
      setErrors(prev => ({ ...prev, banner: 'Please select an image file' }));
      return;
    }
    if (file.size > 5 * 1024 * 1024) {
      setErrors(prev => ({ ...prev, banner: 'Image size must not exceed 5MB' }));
      return;
    }

    const reader = new FileReader();
    reader.onloadend = () => {
      setBannerPreview(reader.result);
      setFormData(prev => ({
        ...prev,
        banner: reader.result,
        bannerFile: file,
      }));
      setErrors(prev => ({ ...prev, banner: '' }));
    };
    reader.readAsDataURL(file);
  };

  const validateForm = () => {
    const newErrors = {};
    if (!formData.name.trim()) newErrors.name = 'Vendor name is required';
    if (!formData.email.trim()) newErrors.email = 'Email is required';
    if (!formData.logo) newErrors.logo = 'Please select a vendor logo';
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
    setFormData({ name: '', email: '', description: '', phone: '', taxCode: '', logo: '', banner: '' });
    setLogoPreview('');
    setBannerPreview('');
    setErrors({});
    if (logoInputRef.current) logoInputRef.current.value = '';
    if (bannerInputRef.current) bannerInputRef.current.value = '';
  };

  return (
    <div className="vendor-form-container">
      <form onSubmit={handleSubmit} className="vendor-form">
        <h2>{vendor ? 'Edit Vendor' : 'Add New Vendor'}</h2>

        <div className="form-columns">
          {/* Left Column - Basic Information */}
          <div className="form-column-left">
            <div className="form-group">
              <label htmlFor="vendor-name">Vendor Name *</label>
              <input
                type="text"
                id="vendor-name"
                name="name"
                value={formData.name}
                onChange={handleChange}
                className={errors.name ? 'error' : ''}
                placeholder="Enter vendor name"
              />
              {errors.name && <span className="error-message">{errors.name}</span>}
            </div>

            <div className="form-group">
              <label htmlFor="vendor-email">Email *</label>
              <input
                type="email"
                id="vendor-email"
                name="email"
                value={formData.email}
                onChange={handleChange}
                className={errors.email ? 'error' : ''}
                placeholder="Enter vendor email"
              />
              {errors.email && <span className="error-message">{errors.email}</span>}
            </div>

            <div className="form-group">
              <label htmlFor="vendor-phone">Phone</label>
              <input
                type="tel"
                id="vendor-phone"
                name="phone"
                value={formData.phone}
                onChange={handleChange}
                placeholder="Enter vendor phone"
              />
            </div>

            <div className="form-group">
              <label htmlFor="vendor-taxCode">Tax Code</label>
              <input
                type="text"
                id="vendor-taxCode"
                name="taxCode"
                value={formData.taxCode}
                onChange={handleChange}
                placeholder="Enter vendor tax code"
              />
            </div>

            <div className="form-group">
              <label htmlFor="vendor-description">Description</label>
              <textarea
                id="vendor-description"
                name="description"
                value={formData.description}
                onChange={handleChange}
                placeholder="Enter vendor description"
                rows="4"
                className="form-textarea"
              />
            </div>
          </div>

          {/* Right Column - Images */}
          <div className="form-column-right">
            <div className="form-group">
              <label>Logo *</label>
              <input
                ref={logoInputRef}
                type="file"
                id="vendor-logo"
                accept="image/*"
                onChange={handleLogoChange}
                className="image-input-hidden"
              />

              {logoPreview ? (
                <div className="image-preview">
                  <img src={logoPreview} alt="Logo Preview" className="preview-image" />
                  <button
                    type="button"
                    className="remove-image-btn"
                    onClick={() => {
                      setLogoPreview('');
                      setFormData(prev => ({ ...prev, logo: '', logoFile: undefined }));
                      if (logoInputRef.current) logoInputRef.current.value = '';
                    }}
                  >
                    ×
                  </button>
                </div>
              ) : (
                <div
                  className="upload-placeholder"
                  onClick={() => logoInputRef.current?.click()}
                  role="button"
                  tabIndex={0}
                  onKeyDown={(e) => e.key === 'Enter' && logoInputRef.current?.click()}
                >
                  <div className="upload-icon">📷</div>
                  <p>Click to select logo</p>
                  <small>Supported: JPG, PNG, GIF (max 5MB)</small>
                </div>
              )}
              {errors.logo && <span className="error-message">{errors.logo}</span>}
            </div>

            <div className="form-group">
              <label>Banner</label>
              <input
                ref={bannerInputRef}
                type="file"
                id="vendor-banner"
                accept="image/*"
                onChange={handleBannerChange}
                className="image-input-hidden"
              />

              {bannerPreview ? (
                <div className="image-preview">
                  <img src={bannerPreview} alt="Banner Preview" className="preview-image" />
                  <button
                    type="button"
                    className="remove-image-btn"
                    onClick={() => {
                      setBannerPreview('');
                      setFormData(prev => ({ ...prev, banner: '', bannerFile: undefined }));
                      if (bannerInputRef.current) bannerInputRef.current.value = '';
                    }}
                  >
                    ×
                  </button>
                </div>
              ) : (
                <div
                  className="upload-placeholder"
                  onClick={() => bannerInputRef.current?.click()}
                  role="button"
                  tabIndex={0}
                  onKeyDown={(e) => e.key === 'Enter' && bannerInputRef.current?.click()}
                >
                  <div className="upload-icon">🖼️</div>
                  <p>Click to select banner</p>
                  <small>Supported: JPG, PNG, GIF (max 5MB)</small>
                </div>
              )}
              {errors.banner && <span className="error-message">{errors.banner}</span>}
            </div>
          </div>
        </div>

        <div className="form-actions">
          <button type="button" onClick={handleReset} className="btn btn-secondary" disabled={loading}>
            Reset
          </button>
          <button type="button" onClick={onCancel} className="btn btn-cancel" disabled={loading}>
            Cancel
          </button>
          <button type="submit" className="btn btn-primary" disabled={loading}>
            {loading ? 'Saving...' : vendor ? 'Update' : 'Create'}
          </button>
        </div>
      </form>
    </div>
  );
}
