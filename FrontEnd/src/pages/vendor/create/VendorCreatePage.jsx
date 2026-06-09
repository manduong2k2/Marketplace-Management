import { useState, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { vendorService } from '../../../services/vendorService';
import { showSuccess, showError } from '../../../components/master/popup';
import './VendorCreatePage.css';

const INITIAL_FORM = {
  name: '',
  description: '',
  taxCode: '',
  email: '',
  phone: '',
  addressId: '',
};

const INITIAL_ERRORS = {};

export default function VendorCreatePage() {
  const navigate = useNavigate();
  const [form, setForm] = useState(INITIAL_FORM);
  const [errors, setErrors] = useState(INITIAL_ERRORS);
  const [loading, setLoading] = useState(false);

  // File states
  const [logoFile, setLogoFile] = useState(null);
  const [logoPreview, setLogoPreview] = useState(null);
  const [bannerFile, setBannerFile] = useState(null);
  const [bannerPreview, setBannerPreview] = useState(null);

  const logoInputRef = useRef(null);
  const bannerInputRef = useRef(null);

  // ── Handlers ──────────────────────────────────────────
  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
    if (errors[name]) setErrors((prev) => ({ ...prev, [name]: '' }));
  };

  const handleImageChange = (e, type) => {
    const file = e.target.files?.[0];
    if (!file) return;

    const url = URL.createObjectURL(file);
    if (type === 'logo') {
      setLogoFile(file);
      setLogoPreview(url);
      if (errors.logo) setErrors((prev) => ({ ...prev, logo: '' }));
    } else {
      setBannerFile(file);
      setBannerPreview(url);
      if (errors.banner) setErrors((prev) => ({ ...prev, banner: '' }));
    }
  };

  const removeImage = (type, e) => {
    e.stopPropagation();
    if (type === 'logo') {
      setLogoFile(null);
      setLogoPreview(null);
      if (logoInputRef.current) logoInputRef.current.value = '';
    } else {
      setBannerFile(null);
      setBannerPreview(null);
      if (bannerInputRef.current) bannerInputRef.current.value = '';
    }
  };

  // ── Validation ────────────────────────────────────────
  const validate = () => {
    const newErrors = {};

    if (!form.name.trim()) newErrors.name = 'Store name is required';
    if (!form.email.trim()) {
      newErrors.email = 'Email is required';
    } else if (!/\S+@\S+\.\S+/.test(form.email)) {
      newErrors.email = 'Invalid email format';
    }
    if (!form.taxCode.trim()) newErrors.taxCode = 'Tax code is required';
    // if (!logoFile) newErrors.logo = 'Logo is required';
    // if (!bannerFile) newErrors.banner = 'Banner is required';
    if (form.phone && !/^\d{9,11}$/.test(form.phone.replace(/\s/g, ''))) {
      newErrors.phone = 'Invalid phone number';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  // ── Submit ────────────────────────────────────────────
  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validate()) return;

    setLoading(true);
    try {
      // Build multipart/form-data — matches RegisterVendorCommand
      const fd = new FormData();
      fd.append('name', form.name.trim());
      fd.append('description', form.description.trim());
      fd.append('taxCode', form.taxCode.trim());
      fd.append('email', form.email.trim());
      fd.append('phone', form.phone.trim());
      if (form.addressId.trim()) fd.append('addressId', form.addressId.trim());
      if(logoFile) fd.append('logo', logoFile);
      if(bannerFile) fd.append('banner', bannerFile);

      const response = await vendorService.register(fd);

      if (response.ok) {
        showSuccess('Your vendor store has been created!', 'Welcome aboard 🎉');
        setTimeout(() => navigate('/home'), 1200);
      } else {
        // Handle backend validation errors
        if (response.data?.errors && typeof response.data.errors === 'object') {
          const backendErrors = {};
          Object.entries(response.data.errors).forEach(([field, msg]) => {
            backendErrors[field] = msg;
          });
          setErrors(backendErrors);
        } else {
          showError(
            response.data?.message || 'Failed to create vendor store',
            'Error'
          );
        }
      }
    } catch (err) {
      console.error(err);
      showError('Something went wrong. Please try again.', 'Connection Error');
    } finally {
      setLoading(false);
    }
  };

  // ── Render ────────────────────────────────────────────
  return (
    <div className="vendor-create-page">
      {/* Header */}
      <div className="vendor-create-header">
        <h1>🏪 Set up your vendor store</h1>
        <p>Fill in the details below to register your store on the platform.</p>
      </div>

      <form onSubmit={handleSubmit} noValidate>

        {/* ── Section 1: Store info ── */}
        <div className="vendor-create-section">
          <p className="vendor-create-section-title">Store Information</p>
          <div className="vendor-form-grid">

            <div className="form-field form-field--full">
              <label htmlFor="name">
                Store Name <span className="required">*</span>
              </label>
              <input
                id="name"
                name="name"
                type="text"
                placeholder="e.g. My Awesome Shop"
                value={form.name}
                onChange={handleChange}
                className={errors.name ? 'field-error' : ''}
              />
              {errors.name && <span className="field-error-msg">{errors.name}</span>}
            </div>

            <div className="form-field form-field--full">
              <label htmlFor="description">Description</label>
              <textarea
                id="description"
                name="description"
                rows={3}
                placeholder="Tell customers what your store is about..."
                value={form.description}
                onChange={handleChange}
              />
            </div>

          </div>
        </div>

        {/* ── Section 2: Contact & legal ── */}
        <div className="vendor-create-section">
          <p className="vendor-create-section-title">Contact &amp; Legal</p>
          <div className="vendor-form-grid">

            <div className="form-field">
              <label htmlFor="email">
                Business Email <span className="required">*</span>
              </label>
              <input
                id="email"
                name="email"
                type="email"
                placeholder="store@example.com"
                value={form.email}
                onChange={handleChange}
                className={errors.email ? 'field-error' : ''}
              />
              {errors.email && <span className="field-error-msg">{errors.email}</span>}
            </div>

            <div className="form-field">
              <label htmlFor="phone">Phone</label>
              <input
                id="phone"
                name="phone"
                type="tel"
                placeholder="0901 234 567"
                value={form.phone}
                onChange={handleChange}
                className={errors.phone ? 'field-error' : ''}
              />
              {errors.phone && <span className="field-error-msg">{errors.phone}</span>}
            </div>

            <div className="form-field">
              <label htmlFor="taxCode">
                Tax Code <span className="required">*</span>
              </label>
              <input
                id="taxCode"
                name="taxCode"
                type="text"
                placeholder="e.g. 0123456789"
                value={form.taxCode}
                onChange={handleChange}
                className={errors.taxCode ? 'field-error' : ''}
              />
              {errors.taxCode && <span className="field-error-msg">{errors.taxCode}</span>}
            </div>

            <div className="form-field">
              <label htmlFor="addressId">Address ID</label>
              <input
                id="addressId"
                name="addressId"
                type="text"
                placeholder="UUID of your address"
                value={form.addressId}
                onChange={handleChange}
                className={errors.addressId ? 'field-error' : ''}
              />
              {errors.addressId && <span className="field-error-msg">{errors.addressId}</span>}
            </div>

          </div>
        </div>

        {/* ── Section 3: Images ── */}
        <div className="vendor-create-section">
          <p className="vendor-create-section-title">Store Images</p>
          <div className="image-upload-grid">

            {/* Logo */}
            <div className="image-upload-box image-upload-box--logo">
              <label className="upload-label">
                Logo <span className="required">*</span>
              </label>
              <div
                className={`image-drop-zone ${logoPreview ? 'has-preview' : ''} ${errors.logo ? 'drop-error' : ''}`}
                onClick={() => logoInputRef.current?.click()}
              >
                <input
                  ref={logoInputRef}
                  type="file"
                  accept="image/*"
                  onChange={(e) => handleImageChange(e, 'logo')}
                  onClick={(e) => e.stopPropagation()}
                />
                {logoPreview ? (
                  <>
                    <img src={logoPreview} alt="Logo preview" className="image-preview" />
                    <button
                      type="button"
                      className="image-remove-btn"
                      onClick={(e) => removeImage('logo', e)}
                      aria-label="Remove logo"
                    >
                      ×
                    </button>
                  </>
                ) : (
                  <div className="image-placeholder">
                    <span className="image-placeholder-icon">🖼️</span>
                    <span className="image-placeholder-text">Click to upload logo</span>
                  </div>
                )}
              </div>
              {errors.logo && <span className="field-error-msg">{errors.logo}</span>}
            </div>

            {/* Banner */}
            <div className="image-upload-box">
              <label className="upload-label">
                Banner <span className="required">*</span>
              </label>
              <div
                className={`image-drop-zone ${bannerPreview ? 'has-preview' : ''} ${errors.banner ? 'drop-error' : ''}`}
                onClick={() => bannerInputRef.current?.click()}
              >
                <input
                  ref={bannerInputRef}
                  type="file"
                  accept="image/*"
                  onChange={(e) => handleImageChange(e, 'banner')}
                  onClick={(e) => e.stopPropagation()}
                />
                {bannerPreview ? (
                  <>
                    <img src={bannerPreview} alt="Banner preview" className="image-preview" />
                    <button
                      type="button"
                      className="image-remove-btn"
                      onClick={(e) => removeImage('banner', e)}
                      aria-label="Remove banner"
                    >
                      ×
                    </button>
                  </>
                ) : (
                  <div className="image-placeholder">
                    <span className="image-placeholder-icon">🏞️</span>
                    <span className="image-placeholder-text">Click to upload banner<br />(16:9 recommended)</span>
                  </div>
                )}
              </div>
              {errors.banner && <span className="field-error-msg">{errors.banner}</span>}
            </div>

          </div>
        </div>

        {/* ── Actions ── */}
        <div className="vendor-create-actions">
          <button
            type="button"
            className="btn-cancel"
            onClick={() => navigate(-1)}
            disabled={loading}
          >
            Cancel
          </button>
          <button type="submit" className="btn-submit" disabled={loading}>
            {loading ? 'Creating store...' : 'Create Store'}
          </button>
        </div>

      </form>
    </div>
  );
}
