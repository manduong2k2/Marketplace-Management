// src/pages/auth/profile/ProfilePage.jsx
import { useState, useEffect, useContext } from 'react';
import { authService } from '../../../services/authService';
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
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    phone: '',
    address: ''
  });

  useEffect(() => {
    if (user) {
      setFormData({
        name: user.name || '',
        email: user.email || '',
        phone: user.phone || '',
        address: user.address || ''
      });
    }
  }, [user]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleEdit = () => {
    setIsEditing(true);
  };

  const handleCancel = () => {
    setIsEditing(false);
    // Reset form to original user data
    if (user) {
      setFormData({
        name: user.name || '',
        email: user.email || '',
        phone: user.phone || '',
        address: user.address || ''
      });
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      const formDataToSend = new FormData();
      Object.keys(formData).forEach(key => {
        formDataToSend.append(key, formData[key]);
      });

      const response = await authService.updateProfile(formDataToSend);

      if (response.ok) {
        // Fetch updated profile data
        const updatedProfile = await authService.profile();
        setUser(updatedProfile.data.data);
        showSuccess('Profile updated successfully!', 'Success');
        setIsEditing(false);
      } else {
        showError('Failed to update profile!' + (response.data.data.message ? ' - ' + response.data.data.message : ''), 'Error');
      }
    } catch (err) {
      showError('Failed to update profile!', err.message || 'Please try again');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="profile-container">
      <h2>My Profile</h2>
      
      {!isEditing ? (
        <div className="profile-view">
          <div className="profile-field">
            <label>Name:</label>
            <span>{formData.name || 'Not provided'}</span>
          </div>
          <div className="profile-field">
            <label>Email:</label>
            <span>{formData.email || 'Not provided'}</span>
          </div>
          <div className="profile-field">
            <label>Phone:</label>
            <span>{formData.phone || 'Not provided'}</span>
          </div>
          <div className="profile-field">
            <label>Address:</label>
            <span>{formData.address || 'Not provided'}</span>
          </div>
          <button onClick={handleEdit} className="edit-btn">Edit Profile</button>
        </div>
      ) : (
        <form onSubmit={handleSubmit} className="profile-form">
          <div className="form-group">
            <label>Name:</label>
            <input
              type="text"
              name="name"
              value={formData.name}
              onChange={handleChange}
              required
            />
          </div>

          <div className="form-group">
            <label>Email:</label>
            <input
              type="email"
              name="email"
              value={formData.email}
              onChange={handleChange}
              required
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

          <div className="form-group">
            <label>Address:</label>
            <input
              type="text"
              name="address"
              value={formData.address}
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
        </form>
      )}
    </div>
  );
}
