import { useState, useEffect, useContext } from 'react';
import { useNavigate, useSearchParams, Link } from 'react-router-dom';
import { authService } from '../../services/authService';
import { AuthContext } from '../../contexts/AuthContext';
import { showSuccess, showError } from '../../components/master/popup';
import './AuthPage.css';

export default function AuthPage() {
  useEffect(() => {
    document.title = 'My Store - Authentication';
  }, []);

  const [searchParams] = useSearchParams();
  const action = searchParams.get('action') || 'login';
  const [isLogin, setIsLogin] = useState(action === 'login');
  const [rightPanelActive, setRightPanelActive] = useState(!isLogin);

  const [loginData, setLoginData] = useState({ email: '', password: '' });
  const [registerData, setRegisterData] = useState({
    name: '',
    email: '',
    password: '',
    confirmPassword: '',
    phone: '',
    address: ''
  });
  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(false);

  const { setUser } = useContext(AuthContext);
  const navigate = useNavigate();

  useEffect(() => {
    setIsLogin(action === 'login');
    setRightPanelActive(action !== 'login');
  }, [action]);

  const switchToSignUp = () => {
    setRightPanelActive(true);
    setIsLogin(false);
  };

  const switchToLogin = () => {
    setRightPanelActive(false);
    setIsLogin(true);
  };

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

  const handleLoginChange = (e) => {
    const { name, value } = e.target;
    setLoginData(prev => ({ ...prev, [name]: value }));
  };

  const handleRegisterChange = (e) => {
    const { name, value } = e.target;
    setRegisterData(prev => ({ ...prev, [name]: value }));
    if (errors[name]) {
      setErrors(prev => ({ ...prev, [name]: '' }));
    }
  };

  const validateRegisterForm = () => {
    const newErrors = {};

    if (!registerData.name.trim()) {
      newErrors.name = 'Please enter name';
    }

    if (!registerData.email.trim()) {
      newErrors.email = 'Please enter email';
    } else if (!/\S+@\S+\.\S+/.test(registerData.email)) {
      newErrors.email = 'Invalid email format';
    }

    if (!registerData.password) {
      newErrors.password = 'Please enter password';
    } else if (registerData.password.length < 6) {
      newErrors.password = 'Password must be at least 6 characters';
    }

    if (!registerData.confirmPassword) {
      newErrors.confirmPassword = 'Please confirm password';
    } else if (registerData.password !== registerData.confirmPassword) {
      newErrors.confirmPassword = 'Passwords do not match';
    }

    if (registerData.phone && !/^\d{10,11}$/.test(registerData.phone.replace(/\s/g, ''))) {
      newErrors.phone = 'Invalid phone number';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleLogin = async (e) => {
    e.preventDefault();

    if (!loginData.email || !loginData.password) {
      showError('Please complete all required fields!', 'Login Error');
      return;
    }

    setLoading(true);
    try {
      const response = await authService.login({
        email: loginData.email,
        password: loginData.password
      });

      if (response.ok) {
        const user = await authService.profile();
        setUser(user.data.data);
        showSuccess(response.data.message, 'Welcome back');
        setTimeout(() => {
          navigate('/home');
        }, 1000);
      } else {
        showError(response.data.message || 'Login failed', 'Login Error');
      }
    } catch (err) {
      showError('Login failed! Please try again.', 'Connection Error');
    } finally {
      setLoading(false);
    }
  };

  const handleRegister = async (e) => {
    e.preventDefault();

    if (!validateRegisterForm()) {
      return;
    }

    setLoading(true);
    try {
      const { confirmPassword, ...registerDataToSend } = registerData;
      const formDataToSend = new FormData();
      Object.keys(registerDataToSend).forEach(key => {
        formDataToSend.append(key, registerDataToSend[key]);
      });

      const response = await authService.register(formDataToSend);

      if (response.ok) {
        showSuccess('Registration successful!', 'Welcome to our platform');
        setTimeout(() => {
          switchToLogin();
        }, 1200);
      } else if (response.data && response.data.errors && typeof response.data.errors === 'object') {
        const backendErrors = {};
        Object.keys(response.data.errors).forEach(field => {
          backendErrors[field] = response.data.errors[field];
        });
        setErrors(backendErrors);
      } else {
        showError('Registration failed!' + (response.data.message ? ' - ' + response.data.message : ''), 'Please try again');
      }
    } catch (err) {
      showError('Registration failed!', err.message || 'Please try again');
    } finally {
      setLoading(false);
    }
  };

  const socialLogin = (provider) => {
    showSuccess('Connecting to authentication service...', `${provider} Login`);
  };

  return (
    <div className="auth-page-container">
      <div className={`auth-container ${rightPanelActive ? 'right-panel-active' : ''}`} id="container">

        {/* Mobile Header Tabs */}
        <div className="mobile-tabs">
          <button
            id="mobile-login-btn"
            onClick={switchToLogin}
            className={`mobile-tab-btn ${!rightPanelActive ? 'active' : ''}`}
          >
            <i className="fa-solid fa-right-to-bracket mr-2"></i>Sign In
          </button>
          <button
            id="mobile-signup-btn"
            onClick={switchToSignUp}
            className={`mobile-tab-btn ${rightPanelActive ? 'active' : ''}`}
          >
            <i className="fa-solid fa-user-plus mr-2"></i>Sign Up
          </button>
        </div>

        {/* Sign In Form Panel */}
        <div className="form-container sign-in-container">
          <form id="signInForm" onSubmit={handleLogin} className="auth-form">
            <div className="form-header">
              <div className="icon-wrapper">
                <i className="fa-solid fa-bolt"></i>
              </div>
              <h2>Welcome Back!</h2>
              <p>Please enter your credentials to access your account</p>
            </div>
            <div className="form-content">


              <div className="input-fields">
                <div className="auth-input-group">
                  <label>Email or Username</label>
                  <span className="input-icon">
                    <i className="fa-regular fa-envelope"></i>
                  </span>
                  <input
                    type="text"
                    name="email"
                    value={loginData.email}
                    onChange={handleLoginChange}
                    placeholder="name@company.com"
                    required
                  />
                </div>

                <div className="auth-input-group">
                  <div className="label-row">
                    <label>Password</label>
                  </div>
                  <span className="input-icon">
                    <i className="fa-solid fa-lock"></i>
                  </span>
                  <input
                    type="password"
                    id="login-password"
                    name="password"
                    value={loginData.password}
                    onChange={handleLoginChange}
                    placeholder="••••••••"
                    required
                  />
                  <button type="button" onClick={() => togglePassword('login-password', 'login-eye')} className="toggle-password">
                    <i id="login-eye" className="fa-regular fa-eye"></i>
                  </button>
                </div>

                <span className='d-flex justify-content-between align-items-center'>
                  <div className="checkbox-group">
                    <input type="checkbox" id="remember" />
                    <label htmlFor="remember">Remember me</label>
                  </div>
                  <Link to="/forgot" className="forgot-link">Forgot password?</Link>
                </span>
              </div>

              <div className="divider">
                <div className="divider-line"></div>
                <span className="divider-text">or</span>
              </div>

              <div className="social-buttons">
                <button type="button" onClick={() => socialLogin('Google')} className="social-btn">
                  <svg className="social-icon" viewBox="0 0 24 24"><path fill="#4285F4" d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z" /><path fill="#34A853" d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z" /><path fill="#FBBC05" d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.06H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.94l2.85-2.22.81-.63z" /><path fill="#EA4335" d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.06l3.66 2.84c.87-2.6 3.3-4.52 6.16-4.52z" /></svg>
                  Google
                </button>
                <button type="button" onClick={() => socialLogin('Facebook')} className="social-btn">
                  <i className="fa-brands fa-facebook social-icon"></i>
                  Facebook
                </button>
              </div>

            </div>

            <div className="submit-section">
              <button type="submit" className="auth-submit-btn" disabled={loading}>
                {loading ? 'Signing In...' : (
                  <>
                    <span>Sign In</span>
                    <i className="fa-solid fa-arrow-right"></i>
                  </>
                )}
              </button>
            </div>
          </form>
        </div>

        {/* Sign Up Form Panel */}
        <div className="form-container sign-up-container">
          <form id="signUpForm" onSubmit={handleRegister} className="auth-form">
            <div className="form-content">
              <div className="form-header">
                <div className="icon-wrapper purple">
                  <i className="fa-solid fa-user-plus"></i>
                </div>
                <h2>Create Account</h2>
                <p>Join us in just a few simple steps</p>
              </div>

              <div className="input-fields">
                <div className="auth-input-group">
                  <label>Full Name</label>
                  <span className="input-icon">
                    <i className="fa-regular fa-user"></i>
                  </span>
                  <input
                    type="text"
                    name="name"
                    value={registerData.name}
                    onChange={handleRegisterChange}
                    placeholder="John Doe"
                    required
                    className={errors.name ? 'error' : ''}
                  />
                  {errors.name && <span className="error-message">{errors.name}</span>}
                </div>

                <div className="auth-input-group">
                  <label>Email Address</label>
                  <span className="input-icon">
                    <i className="fa-regular fa-envelope"></i>
                  </span>
                  <input
                    type="email"
                    name="email"
                    value={registerData.email}
                    onChange={handleRegisterChange}
                    placeholder="name@company.com"
                    required
                    className={errors.email ? 'error' : ''}
                  />
                  {errors.email && <span className="error-message">{errors.email}</span>}
                </div>

                <div className="auth-input-group">
                  <label>Password</label>
                  <span className="input-icon">
                    <i className="fa-solid fa-lock"></i>
                  </span>
                  <input
                    type="password"
                    id="register-password"
                    name="password"
                    value={registerData.password}
                    onChange={handleRegisterChange}
                    placeholder="At least 6 characters"
                    required
                    className={errors.password ? 'error' : ''}
                  />
                  <button type="button" onClick={() => togglePassword('register-password', 'reg-eye-1')} className="toggle-password">
                    <i id="reg-eye-1" className="fa-regular fa-eye"></i>
                  </button>
                  {errors.password && <span className="error-message">{errors.password}</span>}
                </div>

                <div className="auth-input-group">
                  <label>Confirm Password</label>
                  <span className="input-icon">
                    <i className="fa-solid fa-shield-halved"></i>
                  </span>
                  <input
                    type="password"
                    id="register-confirm-password"
                    name="confirmPassword"
                    value={registerData.confirmPassword}
                    onChange={handleRegisterChange}
                    placeholder="Re-enter your password"
                    required
                    className={errors.confirmPassword ? 'error' : ''}
                  />
                  <button type="button" onClick={() => togglePassword('register-confirm-password', 'reg-eye-2')} className="toggle-password">
                    <i id="reg-eye-2" className="fa-regular fa-eye"></i>
                  </button>
                  {errors.confirmPassword && <span className="error-message">{errors.confirmPassword}</span>}
                </div>

                <div className="auth-input-group">
                  <label>Phone (optional)</label>
                  <span className="input-icon">
                    <i className="fa-solid fa-phone"></i>
                  </span>
                  <input
                    type="tel"
                    name="phone"
                    value={registerData.phone}
                    onChange={handleRegisterChange}
                    placeholder="Phone number"
                    className={errors.phone ? 'error' : ''}
                  />
                  {errors.phone && <span className="error-message">{errors.phone}</span>}
                </div>

                <div className="auth-input-group">
                  <label>Address (optional)</label>
                  <span className="input-icon">
                    <i className="fa-solid fa-location-dot"></i>
                  </span>
                  <input
                    type="text"
                    name="address"
                    value={registerData.address}
                    onChange={handleRegisterChange}
                    placeholder="Your address"
                  />
                </div>

                <div className="checkbox-group">
                  <input type="checkbox" id="terms" required />
                  <label htmlFor="terms">
                    I agree to the <a href="#">Terms of Service</a> and <a href="#">Privacy Policy</a>.
                  </label>
                </div>
              </div>
            </div>

            <div className="submit-section">
              <button type="submit" className="auth-submit-btn purple" disabled={loading}>
                {loading ? 'Registering...' : (
                  <>
                    <span>Sign Up Now</span>
                    <i className="fa-solid fa-user-check"></i>
                  </>
                )}
              </button>
            </div>
          </form>
        </div>

        {/* Overlay Container */}
        <div className="overlay-container">
          <div className="overlay">
            <div className="bg-shape shape-1"></div>
            <div className="bg-shape shape-2"></div>

            <div className="overlay-panel overlay-left">
              <div className="overlay-icon">
                <i className="fa-solid fa-sparkles"></i>
              </div>
              <h1>Already Have an Account?</h1>
              <p>Sign in now to seamlessly continue your journey and access all features.</p>
              <button id="signInBtn" onClick={switchToLogin} className="overlay-btn">
                Sign In
              </button>
            </div>

            <div className="overlay-panel overlay-right">
              <div className="overlay-icon">
                <i className="fa-solid fa-rocket"></i>
              </div>
              <h1>Don't Have an Account?</h1>
              <p>Sign up in seconds to start your amazing experience with us today!</p>
              <button id="signUpBtn" onClick={switchToSignUp} className="overlay-btn">
                Sign Up
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}