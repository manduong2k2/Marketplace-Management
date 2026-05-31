// src/pages/auth/forgot/ForgotPasswordPage.jsx
import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { authService } from '../../../services/authService';
import './ForgotPasswordPage.css';

export default function ForgotPasswordPage() {
  useEffect(() => {
    document.title = 'My Store - Forgot Password';
  }, []);

  const [email, setEmail] = useState('');
  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(false);
  const [submitted, setSubmitted] = useState(false);

  const handleChange = (e) => {
    const value = e.target.value;
    setEmail(value);

    // Clear error when user starts typing
    if (errors.email) {
      setErrors(prev => ({
        ...prev,
        email: ''
      }));
    }
  };

  const validateForm = () => {
    const newErrors = {};

    if (!email.trim()) {
      newErrors.email = 'Please enter email';
    } else if (!/\S+@\S+\.\S+/.test(email)) {
      newErrors.email = 'Invalid email format';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!validateForm()) {
      return;
    }

    setLoading(true);
    try {
      const response = await authService.forgotPassword(email);

      if (response.success || response.message) {
        setSubmitted(true);
      } else {
        setErrors({ email: 'Email not found in system' });
      }
    } catch (err) {
      console.error('Forgot password error:', err);
      setErrors({ email: err.message || 'An error occurred, please try again' });
    } finally {
      setLoading(false);
    }
  };

  const handleReset = () => {
    setEmail('');
    setErrors({});
    setSubmitted(false);
  };

  return (
    <div className="forgot-container">
      <div className="forgot-form">
        {!submitted ? (
          <>
            <h2>Forgot Password</h2>
            <p className="forgot-description">
              Enter your email to receive password reset link
            </p>

            <form onSubmit={handleSubmit}>
              <div className="form-group">
                <input
                  type="email"
                  placeholder="Email"
                  value={email}
                  onChange={handleChange}
                  className={errors.email ? 'error' : ''}
                  required
                />
                {errors.email && <span className="error-message">{errors.email}</span>}
              </div>

              <button type="submit" className="forgot-btn" disabled={loading}>
                {loading ? 'Sending...' : 'Send Reset Link'}
              </button>
            </form>
          </>
        ) : (
          <div className="success-message">
            <div className="success-icon">✓</div>
            <h2>Check your email!</h2>
            <p>
              We have sent password reset link to<br />
              <strong>{email}</strong>
            </p>
            <p className="success-note">
              If you don't receive email within few minutes, please check your spam folder.
            </p>

            <div className="success-actions">
              <button onClick={handleReset} className="reset-btn">
                Send Again
              </button>
              <Link to="/login" className="back-to-login">
                Back to Login
              </Link>
            </div>
          </div>
        )}

        {!submitted && (
          <div className="back-to-login">
            <Link to="/login">← Back to Login</Link>
          </div>
        )}
      </div>
    </div>
  );
}
