import React, { useContext, useState, useRef, useEffect } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { AuthContext } from '../../../contexts/AuthContext';
import { CartContext } from '../../../contexts/CartContext';
import { authService } from '../../../services/authService';
import './Navbar.css';
import shopIcon from '../../../assets/shop-icon.png';

function CartIcon({ count }) {
  return (
    <Link to="/cart" className="navbar-cart-btn" title="Cart">
      <i className="fas fa-shopping-cart"></i>
      {count > 0 && <span className="cart-badge">{count > 99 ? '99+' : count}</span>}
    </Link>
  );
}

export default function Navbar({ onToggleSidebar }) {
  const { user, setUser } = useContext(AuthContext);
  const { cart } = useContext(CartContext);
  const [showDropdown, setShowDropdown] = useState(false);
  const dropdownRef = useRef(null);

  const cartCount = cart?.totalItemCount || 0;
  const location = useLocation();

  const handleLogout = async () => {
    try {
      await authService.logout();
      await setUser(null);
      window.location.href = '/login';
    } catch (err) {
      alert('Logout failed!');
    }
  };

  const toggleDropdown = () => {
    setShowDropdown(!showDropdown);
  };

  const handleMenuClick = (path) => {
    setShowDropdown(false);
    window.location.href = path;
  };

  // Close dropdown when clicking outside
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (showDropdown && dropdownRef.current && !dropdownRef.current.contains(event.target)) {
        setShowDropdown(false);
      }
    };

    document.addEventListener('click', handleClickOutside);
    return () => {
      document.removeEventListener('click', handleClickOutside);
    };
  }, [showDropdown]);

  return (
    <nav className="navbar">
      <div className="navbar-left">
        {user ? <button className="toggle-btn" onClick={onToggleSidebar}>
          ☰
        </button> : null}
        <Link to="/home" className="navbar-logo-link">
          <img src={shopIcon} alt="Logo" className="navbar-icon" />
          <span className="navbar-logo">
            <span className="navbar-logo-title">My Store</span>
            <span className="navbar-logo-subtitle">Buy everything you need</span>
          </span>
        </Link>
      </div>

      {location.pathname === '/home' && (
        <div className="navbar-center">
          <i className="fa-solid fa-magnifying-glass"></i>
          <input type="text" placeholder="Search products..." className="search-input" />
        </div>
      )}

      <div className="navbar-right">
        {user ? (
          <>
            <CartIcon count={cartCount} />
            <div className="dropdown" ref={dropdownRef}>
              <button className="dropdown-toggle" onClick={toggleDropdown}>
                Welcome, {user.name} <i className="fas fa-chevron-down"></i>
              </button>
              {showDropdown && (
                <div className={`dropdown-menu ${showDropdown ? 'show' : ''} animate`}>
                  <button className="dropdown-item" onClick={() => handleMenuClick('/profile')}>
                    <i className="fas fa-user"></i> Profile
                  </button>
                  <button className="dropdown-item" onClick={() => handleMenuClick('/order-history')}>
                    <i className="fas fa-shopping-cart"></i> Order History
                  </button>
                  <div className="dropdown-divider"></div>
                  <button className="dropdown-item" onClick={handleLogout}>
                    <i className="fas fa-sign-out-alt"></i> Logout
                  </button>
                </div>
              )}
            </div>
          </>
        ) : (
          <>
            <Link to="/login" className="navbar-btn">
              Login
            </Link>
            <Link to="/register" className="navbar-btn">
              Register
            </Link>
          </>
        )}
      </div>
    </nav>
  );
}