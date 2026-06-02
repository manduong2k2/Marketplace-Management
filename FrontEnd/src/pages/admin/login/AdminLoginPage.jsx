// src/pages/admin/login/AdminLoginPage.jsx
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
    <div className="admin-login-wrapper">
      <div className="admin-login-card">
        <div className="admin-login-header">
          <span className="admin-login-icon">🏪</span>
          <h1>Admin Panel</h1>
          <p>Sign in to manage your store</p>
        </div>

        <form className="admin-login-form" onSubmit={handleSubmit}>
          {error && (
            <div className="admin-login-error" role="alert">
              ⚠️ {error}
            </div>
          )}

          <div className="admin-form-group">
            <label htmlFor="admin-email">Email</label>
            <input
              id="admin-email"
              type="email"
              placeholder="admin@example.com"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
              autoComplete="email"
            />
          </div>

          <div className="admin-form-group">
            <label htmlFor="admin-password">Password</label>
            <input
              id="admin-password"
              type="password"
              placeholder="••••••••"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              autoComplete="current-password"
            />
          </div>

          <button type="submit" className="admin-login-btn" disabled={loading}>
            {loading ? 'Signing in...' : 'Sign In'}
          </button>
        </form>
      </div>
    </div>
  );
}
