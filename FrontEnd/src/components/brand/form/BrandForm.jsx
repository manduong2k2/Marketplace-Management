// src/components/brand/form/BrandForm.jsx
import React, { useState, useRef, useEffect } from 'react';
import './BrandForm.css';

// Utility function to format file size
const formatBytes = (bytes, decimals = 2) => {
  if (bytes === 0) return '0 Bytes';
  const k = 1024;
  const dm = decimals < 0 ? 0 : decimals;
  const sizes = ['Bytes', 'KB', 'MB', 'GB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return parseFloat((bytes / Math.pow(k, i)).toFixed(dm)) + ' ' + sizes[i];
};

// Sample brands for mock data
const SAMPLE_BRANDS = [
  'ApexTech', 'Nova Gear', 'Aura Studio', 'Vortex Digital', 'Elysium Labs'
];

const SAMPLE_LOGOS = [
  'https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=300&q=80',
  'https://images.unsplash.com/photo-1611162617213-7d7a39e9b1d7?w=300&q=80',
  'https://images.unsplash.com/photo-1599305445671-ac291c95aaa9?w=300&q=80'
];

export default function BrandForm({ brand, onSubmit, onCancel, loading = false }) {
  const [formData, setFormData] = useState({
    name: brand?.name || '',
    description: brand?.description || '',
    image: null,
    imageFile: null,
  });

  const [errors, setErrors] = useState({});
  const [imagePreview, setImagePreview] = useState(brand?.image || null);
  const [imageMeta, setImageMeta] = useState({ fileName: '', fileSize: '', dimensions: '' });
  const [dragActive, setDragActive] = useState(false);
  const [payloadView, setPayloadView] = useState('flat');
  const [toastMessage, setToastMessage] = useState('');
  const [showToast, setShowToast] = useState(false);
  const fileInputRef = useRef(null);

  // Show toast notification
  const showNotification = (message) => {
    setToastMessage(message);
    setShowToast(true);
    setTimeout(() => setShowToast(false), 3000);
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
    if (errors[name]) {
      setErrors(prev => ({ ...prev, [name]: '' }));
    }
  };

  const processImageFile = (file) => {
    if (!file.type.startsWith('image/')) {
      showNotification('Please upload a valid image file!');
      setErrors(prev => ({ ...prev, image: 'Invalid image file' }));
      return;
    }
    if (file.size > 5 * 1024 * 1024) {
      showNotification('Warning: Image size exceeds 5MB limit');
    }

    setImageMeta({ fileName: file.name, fileSize: formatBytes(file.size), dimensions: 'Loading...' });

    const reader = new FileReader();
    reader.onloadend = () => {
      const base64 = reader.result;
      const img = new Image();
      img.onload = function () {
        setImageMeta(prev => ({ ...prev, dimensions: `${this.width} x ${this.height} px` }));
        setImagePreview(base64);
        setFormData(prev => ({
          ...prev,
          image: base64,
          imageFile: file,
        }));
        setErrors(prev => ({ ...prev, image: '' }));
      };
      img.src = base64;
    };
    reader.readAsDataURL(file);
  };

  const handleImageChange = (e) => {
    const file = e.target.files[0];
    if (file) processImageFile(file);
  };

  const handleDragOver = (e) => {
    e.preventDefault();
    e.stopPropagation();
    setDragActive(true);
  };

  const handleDragLeave = (e) => {
    e.preventDefault();
    e.stopPropagation();
    setDragActive(false);
  };

  const handleDrop = (e) => {
    e.preventDefault();
    e.stopPropagation();
    setDragActive(false);
    if (e.dataTransfer.files && e.dataTransfer.files[0]) {
      processImageFile(e.dataTransfer.files[0]);
    }
  };

  const removeImage = () => {
    setImagePreview(null);
    setImageMeta({ fileName: '', fileSize: '', dimensions: '' });
    setFormData(prev => ({ ...prev, image: null, imageFile: null }));
    if (fileInputRef.current) fileInputRef.current.value = '';
    setErrors(prev => ({ ...prev, image: '' }));
    showNotification('Brand logo removed');
  };

  const autoFillMockData = () => {
    const randInt = Math.floor(Math.random() * 900) + 100;
    const chosenBrand = SAMPLE_BRANDS[Math.floor(Math.random() * SAMPLE_BRANDS.length)] + ' ' + randInt;
    const chosenLogo = SAMPLE_LOGOS[Math.floor(Math.random() * SAMPLE_LOGOS.length)];

    setFormData(prev => ({
      ...prev,
      name: chosenBrand,
      description: `${chosenBrand} is a premier brand specializing in high-performance innovative electronics and lifestyle accessories.`,
      image: chosenLogo,
    }));

    setImageMeta({
      fileName: `logo_${randInt}.jpg`,
      fileSize: '142 KB',
      dimensions: '600 x 600 px'
    });
    setImagePreview(chosenLogo);
    showNotification('Mock brand data pre-filled!');
  };

  const handleReset = () => {
    setFormData({ name: '', description: '', image: null, imageFile: null });
    setImagePreview(null);
    setImageMeta({ fileName: '', fileSize: '', dimensions: '' });
    setErrors({});
    if (fileInputRef.current) fileInputRef.current.value = '';
    showNotification('Brand form has been reset!');
  };

  const validateForm = () => {
    const newErrors = {};
    if (!formData.name.trim()) newErrors.name = 'Brand name is required';
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validateForm()) return;

    const serverErrors = await onSubmit(formData);
    
    // Handle server errors
    if (serverErrors && typeof serverErrors === 'object') {
      // Check if response has message and errors structure
      if (serverErrors.message && serverErrors.errors) {
        // Show message in popup
        window.showError(serverErrors.message, 'Validation Error');
        // Convert server error keys to client format if needed
        const mappedErrors = {};
        Object.entries(serverErrors.errors).forEach(([key, value]) => {
          // Convert variants[0].sku -> variant_0_sku
          let mappedKey = key;
          mappedKey = mappedKey.replace(/variants\[(\d+)\]\./, 'variant_$1_');
          mappedKey = mappedKey.replace(/options\[(\d+)\]\./, 'option_$1_');
          mappedErrors[mappedKey] = value;
        });
        // Bind field errors
        setErrors(mappedErrors);
      } else if (serverErrors._ || serverErrors.message) {
        // Legacy format or simple message
        const errorMessage = serverErrors._ || serverErrors.message;
        window.showError(errorMessage, 'Validation Error');
        const { _: _ignored, message: _msgIgnored, ...fieldErrors } = serverErrors;
        if (Object.keys(fieldErrors).length > 0) {
          setErrors(fieldErrors);
        }
      } else {
        // Field errors only
        setErrors(serverErrors);
      }
    }
  };

  // Generate payload for preview
  const generatePayload = () => {
    const flatLines = [];
    if (formData.name) flatLines.push(`name: ${formData.name}`);
    if (formData.description) flatLines.push(`description: ${formData.description}`);
    if (imagePreview) {
      const imgDisplay = imagePreview.startsWith('data:image')
        ? `${imagePreview.substring(0, 35)}... [Base64 Binary]`
        : imagePreview;
      flatLines.push(`image: ${imgDisplay}`);
    }
    return flatLines;
  };

  const flatPayload = generatePayload();
  const jsonPayload = {
    name: formData.name,
    description: formData.description,
    image: imagePreview,
  };

  const copyPayload = async () => {
    let textToCopy = '';
    if (payloadView === 'flat') {
      textToCopy = flatPayload.join('\n');
    } else {
      textToCopy = JSON.stringify(jsonPayload, null, 2);
    }

    try {
      await navigator.clipboard.writeText(textToCopy);
      showNotification('Payload copied to clipboard!');
    } catch (err) {
      showNotification('Failed to copy payload!');
    }
  };

  return (
    <div className="brand-form-wrapper">
      {/* Header */}
      <header className="brand-form-header">
        <div className="header-left">
          <div className="header-logo">
            <i className="fa-solid fa-copyright"></i>
          </div>
          <div className="header-title">
            <h1>Brand Creator</h1>
            <p>Create & manage brand profiles, descriptions, and logos</p>
          </div>
        </div>
        <div className="header-actions">
          <button
            type="button"
            onClick={autoFillMockData}
            className="btn-action btn-fill-mock"
            title="Auto-fill with sample data"
          >
            <i className="fa-solid fa-wand-magic-sparkles"></i>
            <span>Fill Mock Data</span>
          </button>
          <button
            type="button"
            onClick={handleReset}
            className="btn-action btn-reset"
            title="Clear all form fields"
          >
            <i className="fa-solid fa-rotate-left"></i>
            <span className="hidden-sm">Reset Form</span>
          </button>
        </div>
      </header>

      {/* Main Content */}
      <main className="brand-form-main">
        {/* Left Column: Form */}
        <div className="brand-form-column-left">
          <form onSubmit={handleSubmit} className="brand-form">
            {/* Brand Details Section */}
            <div className="form-section">
              <div className="section-header">
                <div className="section-header-title">
                  <i className="fa-solid fa-tag"></i>
                  <h2>Brand Details</h2>
                </div>
                <span className="section-badge">Brand Profile</span>
              </div>

              {/* Brand Name */}
              <div className="form-group">
                <label htmlFor="brand-name">
                  Brand Name <span className="required">*</span>
                </label>
                <div className="form-input-wrapper">
                  <input
                    type="text"
                    id="brand-name"
                    name="name"
                    value={formData.name}
                    onChange={handleChange}
                    placeholder="e.g. Apple, Sony, Nike"
                    className={`form-input ${errors.name ? 'error' : ''}`}
                  />
                  <span className="input-label">name</span>
                </div>
                {errors.name && <span className="error-message">{errors.name}</span>}
              </div>

              {/* Description */}
              <div className="form-group">
                <label htmlFor="brand-description">Description</label>
                <textarea
                  id="brand-description"
                  name="description"
                  value={formData.description}
                  onChange={handleChange}
                  placeholder="Enter a comprehensive overview of the brand's identity, vision, or products..."
                  rows="4"
                  className="form-textarea"
                />
              </div>

              {/* Brand Logo Upload */}
              <div className="form-group">
                <label>Brand Logo / Image</label>
                <p className="upload-hint">Upload a single image file (PNG, JPG, SVG, WebP up to 5MB).</p>

                <input
                  ref={fileInputRef}
                  type="file"
                  id="brand-image-input"
                  accept="image/*"
                  onChange={handleImageChange}
                  className="image-input-hidden"
                />

                {imagePreview ? (
                  <div className="image-preview-container">
                    <div className="image-preview-box">
                      <img src={imagePreview} alt="Brand Logo Preview" className="preview-image" />
                    </div>
                    <div className="image-meta-info">
                      <div className="meta-header">
                        <h4 className="file-name">{imageMeta.fileName || 'logo.png'}</h4>
                        <span className="file-badge">Image</span>
                      </div>
                      <p className="meta-item">Dimensions: {imageMeta.dimensions || 'N/A'}</p>
                      <p className="meta-item">Size: {imageMeta.fileSize || 'N/A'}</p>
                      <button
                        type="button"
                        onClick={removeImage}
                        className="remove-image-link"
                      >
                        <i className="fa-solid fa-trash-can"></i>
                        <span>Remove Image</span>
                      </button>
                    </div>
                  </div>
                ) : (
                  <div
                    className={`dropzone ${dragActive ? 'active' : ''}`}
                    onDragOver={handleDragOver}
                    onDragLeave={handleDragLeave}
                    onDrop={handleDrop}
                    onClick={() => fileInputRef.current?.click()}
                    role="button"
                    tabIndex={0}
                    onKeyDown={(e) => e.key === 'Enter' && fileInputRef.current?.click()}
                  >
                    <div className="dropzone-icon">
                      <i className="fa-solid fa-cloud-arrow-up"></i>
                    </div>
                    <div className="dropzone-text">
                      <span className="dropzone-highlight">Click to upload</span> or drag and drop logo image
                    </div>
                    <p className="dropzone-hint">Supports PNG, JPG, WebP, SVG (Max size: 5MB)</p>
                  </div>
                )}
                {errors.image && <span className="error-message">{errors.image}</span>}
              </div>
            </div>

            {/* Form Actions */}
            <div className="form-actions">
              <button
                type="button"
                onClick={onCancel}
                className="btn-cancel"
                disabled={loading}
              >
                Cancel
              </button>
              <button type="submit" className="btn-submit" disabled={loading}>
                <i className="fa-solid fa-check"></i>
                <span>{loading ? 'Creating...' : brand ? 'Update' : 'Create'} Brand</span>
              </button>
            </div>
          </form>
        </div>
      </main>

      {/* Toast Notification */}
      {showToast && (
        <div className="brand-toast">
          <span className="toast-message">
            <i className="fa-solid fa-circle-check"></i>
            {toastMessage}
          </span>
          <button
            onClick={() => setShowToast(false)}
            className="toast-close"
          >
            <i className="fa-solid fa-xmark"></i>
          </button>
        </div>
      )}
    </div>
  );
}
