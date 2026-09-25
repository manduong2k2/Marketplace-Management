import React, { useState, useContext, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { AdminContext } from '../../../contexts/AdminContext';
import { adminAuthService } from '../../../services/adminAuthService';
import './AdminLoginPage.css';

export default function AdminLoginPage() {
  useEffect(() => {
    document.title = 'Admin - Login';
  }, []);

  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const { setAdmin } = useContext(AdminContext);
  const navigate = useNavigate();

  const togglePassword = (inputId, iconId) => {
    const input = document.getElementById(inputId);
    const icon = document.getElementById(iconId);
    if (input.type === "password") {
      input.type = "text";
      icon.classList.remove("fa-eye");
      icon.classList.add("fa-eye-slash");
    } else {
      input.type = "password";
      icon.classList.remove("fa-eye-slash");
      icon.classList.add("fa-eye");
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const response = await adminAuthService.login({ email, password });
      if (response.ok) {
        const profileRes = await adminAuthService.profile();
        const user = profileRes.data?.data;
        if (user && user.roles && user.roles.includes('Admin')) {
          setAdmin(user);
          navigate('/admin/brands');
        } else {
          setError('This account does not have Admin privileges.');
        }
      } else {
        setError(response.data?.message || 'Login failed. Please check your credentials.');
      }
    } catch {
      setError('Unable to connect to the server. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="admin-auth-page-container">
      <div className="admin-auth-container">
        <div className="admin-auth-form">
          <div className="admin-form-header">
            <div className="admin-icon-wrapper">
              <i className="fa-solid fa-shield-halved"></i>
            </div>
            <h2>Admin Panel</h2>
            <p>Sign in to manage your store</p>
          </div>

          <div className="admin-input-fields">
            {error && (
              <div className="admin-error-message">
                <i className="fa-solid fa-circle-exclamation"></i>
                {error}
              </div>
            )}

            <div className="admin-auth-input-group">
              <label>Email Address</label>
              <span className="admin-input-icon">
                <i className="fa-regular fa-envelope"></i>
              </span>
              <input 
                type="email" 
                name="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="admin@example.com" 
                required
                autoComplete="email"
              />
            </div>

            <div className="admin-auth-input-group">
              <label>Password</label>
              <span className="admin-input-icon">
                <i className="fa-solid fa-lock"></i>
              </span>
              <input 
                type="password" 
                id="admin-password"
                name="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="••••••••" 
                required
                autoComplete="current-password"
              />
              <button type="button" onClick={() => togglePassword('admin-password', 'admin-eye')} className="admin-toggle-password">
                <i id="admin-eye" className="fa-regular fa-eye"></i>
              </button>
            </div>

            <div className="admin-checkbox-group">
              <input type="checkbox" id="admin-remember" />
              <label htmlFor="admin-remember">Remember me</label>
            </div>
          </div>

          <div className="admin-submit-section">
            <button type="submit" className="admin-submit-btn" disabled={loading}>
              {loading ? 'Signing In...' : (
                <>
                  <span>Sign In</span>
                  <i className="fa-solid fa-arrow-right"></i>
                </>
              )}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}