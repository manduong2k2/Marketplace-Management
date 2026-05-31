import { Link, useNavigate } from 'react-router-dom';
import { useState, useContext, useEffect } from 'react';
import { authService } from '../../../services/authService';
import { AuthContext } from '../../../contexts/AuthContext';
import { showSuccess, showError } from '../../../components/master/popup';
import './LoginPage.css';

export default function LoginPage() {
  useEffect(() => {
    document.title = 'My Store - Login';
  }, []);

  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const { setUser } = useContext(AuthContext); // get setUser from context
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      var response = await authService.login({ email, password });
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
      console.log(err);
      showError('Login failed! Please try again.', 'Connection Error');
    }
  };

  return (
    <form id="login-form" onSubmit={handleSubmit} className="login-form">
      <h2>Login</h2>
      <input
        type="email"
        placeholder="Email"
        value={email}
        onChange={(e) => setEmail(e.target.value)}
        required
      />
      <input
        type="password"
        placeholder="Password"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
        required
      />
      <button type="submit">Login</button>
      <div className="forgot-links">
        <Link to="/forgot">Forgot password?</Link>
      </div>

      <div className="register-link">
        Don't have an account? <Link to="/register">Register now</Link>
      </div>
    </form>
  );
}