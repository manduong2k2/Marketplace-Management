// src/pages/auth/profile/ProfilePage.jsx
import { useState, useEffect, useContext, useRef } from 'react';
import { Link } from 'react-router-dom';
import { authService } from '../../../services/authService';
import { addressService } from '../../../services/addressService';
import { AuthContext } from '../../../contexts/AuthContext';
import { showSuccess, showError } from '../../../components/master/popup';
import './ProfilePage.css';

export default function ProfilePage() {
  useEffect(() => {
    document.title = 'My Store - Profile';
  }, []);

  const { user, setUser } = useContext(AuthContext);
  const [isEditing, setIsEditing] = useState(false);
  const [loading, setLoading] = useState(false);
  const [formData, setFormData] = useState({ name: '', email: '', phone: '' });

  // Avatar
  const [avatarFile, setAvatarFile] = useState(null);
  const [avatarPreview, setAvatarPreview] = useState('');
  const fileInputRef = useRef(null);

  // Default address
  const [defaultAddress, setDefaultAddress] = useState(null);
  const [addressLoading, setAddressLoading] = useState(true);

  useEffect(() => {
    if (user) {
      setFormData({
        name: user.name || '',
        email: user.email || '',
        phone: user.phone || '',
      });
    }
  }, [user]);

  useEffect(() => {
    return () => { if (avatarPreview) URL.revokeObjectURL(avatarPreview); };
  }, [avatarPreview]);

  useEffect(() => {
    const fetchDefaultAddress = async () => {
      setAddressLoading(true);
      try {
        const res = await addressService.getDefaultAddress();
        if (res.ok) setDefaultAddress(res.data.data);
      } catch { /* no default address */ }
      finally { setAddressLoading(false); }
    };
    fetchDefaultAddress();
  }, []);

  const formatDefaultAddress = (addr) => {
    if (!addr) return null;
    const ward = addr.ward?.fullName || addr.ward?.name || '';
    const province = addr.ward?.province?.name || addr.ward?.province?.fullName || '';
    const parts = [addr.houseNumber, addr.streetName, ward, province].filter(Boolean);
    if (addr.detail) parts.push(addr.detail);
    return parts.join(', ');
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleAvatarChange = (e) => {
    const file = e.target.files?.[0];
    if (!file) return;
    if (avatarPreview) URL.revokeObjectURL(avatarPreview);
    setAvatarFile(file);
    setAvatarPreview(URL.createObjectURL(file));
  };

  const handleEdit = () => setIsEditing(true);

  const handleCancel = () => {
    setIsEditing(false);
    setAvatarFile(null);
    if (avatarPreview) { URL.revokeObjectURL(avatarPreview); setAvatarPreview(''); }
    if (user) setFormData({ name: user.name || '', email: user.email || '', phone: user.phone || '' });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      const fd = new FormData();
      fd.append('name', formData.name);
      fd.append('phone', formData.phone);
      if (avatarFile) fd.append('avatar', avatarFile);

      const response = await authService.updateProfile(fd);
      if (response.ok) {
        const updated = await authService.profile();
        setUser(updated.data.data);
        showSuccess('Profile updated successfully!', 'Success');
        setIsEditing(false);
        setAvatarFile(null);
        if (avatarPreview) { URL.revokeObjectURL(avatarPreview); setAvatarPreview(''); }
      } else {
        showError('Failed to update profile!' + (response.data?.message ? ' - ' + response.data.message : ''), 'Error');
      }
    } catch (err) {
      showError('Failed to update profile!', err.message || 'Please try again');
    } finally {
      setLoading(false);
    }
  };

  const displayAvatar = avatarPreview || user?.avatar || null;

  return (
    <div className="profile-container">
      <h2>My Profile</h2>

      {!isEditing ? (
        /* ────── VIEW MODE ────── */
        <div className="profile-layout">

          {/* Left — avatar + name */}
          <div className="profile-left">
            <div className="profile-avatar-wrap">
              {displayAvatar ? (
                <img src={displayAvatar} alt="Avatar" className="profile-avatar" />
              ) : (
                <div className="profile-avatar profile-avatar--fallback">
                  {user?.name?.charAt(0)?.toUpperCase() || '?'}
                </div>
              )}
            </div>
            <p className="profile-name">{formData.name || 'No name'}</p>
            <p className="profile-email-sub">{formData.email}</p>
          </div>

          {/* Right — fields + actions */}
          <div className="profile-right">
            <div className="profile-field">
              <label>Email:</label>
              <span>{formData.email || 'Not provided'}</span>
            </div>
            <div className="profile-field">
              <label>Phone:</label>
              <span>{formData.phone || 'Not provided'}</span>
            </div>
            <div className="profile-field">
              <label>Default Address:</label>
              {addressLoading ? (
                <span className="profile-address-loading">Loading...</span>
              ) : defaultAddress ? (
                <span>{formatDefaultAddress(defaultAddress)}</span>
              ) : (
                <span className="profile-address-empty">No default address set</span>
              )}
            </div>
            <Link to="/addresses" className="profile-address-link">
              Manage my addresses →
            </Link>
            <button onClick={handleEdit} className="edit-btn">
              Edit Profile
            </button>
          </div>
        </div>
      ) : (
        /* ────── EDIT MODE ────── */
        <form onSubmit={handleSubmit} className="profile-layout profile-layout--form">

          {/* Left — avatar upload + name input */}
          <div className="profile-left">
            <div
              className="avatar-upload-area"
              onClick={() => fileInputRef.current?.click()}
              role="button"
              tabIndex={0}
              onKeyDown={(e) => e.key === 'Enter' && fileInputRef.current?.click()}
              aria-label="Upload avatar"
            >
              {displayAvatar ? (
                <img src={displayAvatar} alt="Avatar preview" className="avatar-preview" />
              ) : (
                <div className="avatar-preview avatar-preview--placeholder">
                  <span className="avatar-upload-icon">↑</span>
                  <span>Upload photo</span>
                </div>
              )}
              <div className="avatar-upload-overlay">
                <span>Change photo</span>
              </div>
            </div>
            <input
              ref={fileInputRef}
              type="file"
              accept="image/*"
              onChange={handleAvatarChange}
              style={{ display: 'none' }}
            />
            {avatarFile && (
              <span className="avatar-filename">{avatarFile.name}</span>
            )}

            {/* Name field lives in the left column */}
            <div className="form-group" style={{ width: '100%', marginTop: '16px' }}>
              <label>Name:</label>
              <input
                type="text"
                name="name"
                value={formData.name}
                onChange={handleChange}
                required
              />
            </div>
          </div>

          {/* Right — remaining fields + actions */}
          <div className="profile-right">
            <div className="form-group">
              <label>Email:</label>
              <input
                type="email"
                name="email"
                value={formData.email}
                onChange={handleChange}
                disabled
              />
            </div>

            <div className="form-group">
              <label>Phone:</label>
              <input
                type="tel"
                name="phone"
                value={formData.phone}
                onChange={handleChange}
              />
            </div>

            <div className="form-actions">
              <button type="submit" className="submit-btn" disabled={loading}>
                {loading ? 'Saving...' : 'Save Changes'}
              </button>
              <button type="button" onClick={handleCancel} className="cancel-btn">
                Cancel
              </button>
            </div>
          </div>
        </form>
      )}
    </div>
  );
}
