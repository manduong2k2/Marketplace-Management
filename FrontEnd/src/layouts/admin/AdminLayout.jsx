// src/layouts/admin/AdminLayout.jsx
import { useState, useEffect, useContext } from 'react';
import { Outlet, NavLink, useNavigate } from 'react-router-dom';
import { AdminContext } from '../../contexts/AdminContext';
import { adminAuthService } from '../../services/adminAuthService';
import PopupManager from '../../components/master/popup/PopupManager';
import Chatbot from '../../components/master/chatbot';
import { useTheme } from '../../hooks/useTheme';
import './AdminLayout.css';

// ── Sidebar mini state helpers ──────────────────────────────────────────────
function isDesktop() { return window.matchMedia('(min-width: 992px)').matches; }

// ── Nav items mapped to existing routes ─────────────────────────────────────
const NAV_ITEMS = [
  { to: '/admin/dashboard', icon: 'bi-speedometer2', label: 'Dashboard' },
  { to: '/admin/brands',    icon: 'bi-tags',          label: 'Brands' },
  { to: '/admin/categories',icon: 'bi-diagram-3',     label: 'Categories' },
  { to: '/admin/products',  icon: 'bi-box-seam',      label: 'Products' },
  { to: '/admin/vendors',   icon: 'bi-shop',          label: 'Vendors' },
  { to: '/admin/orders',    icon: 'bi-receipt',       label: 'Orders' },
];

export default function AdminLayout() {
  const { admin, setAdmin } = useContext(AdminContext);
  const navigate = useNavigate();

  // ── Theme ────────────────────────────────────────────────────────────────
  const { theme, toggleTheme } = useTheme();

  // ── Sidebar mini (desktop collapse) — handled purely by CSS :hover ───────

  // ── Sidebar open (mobile overlay) ────────────────────────────────────────
  const [mobileOpen, setMobileOpen] = useState(false);

  // Respond to viewport changes
  useEffect(() => {
    const mq = window.matchMedia('(min-width: 992px)');
    const handler = (e) => {
      if (!e.matches) setMobileOpen(false);
    };
    mq.addEventListener('change', handler);
    return () => mq.removeEventListener('change', handler);
  }, []);

  const handleSidebarToggle = () => {
    if (!isDesktop()) setMobileOpen(o => !o);
  };

  const closeMobile = () => setMobileOpen(false);

  // Close mobile sidebar on nav click
  const handleNavClick = () => { if (!isDesktop()) closeMobile(); };

  // ── Logout ───────────────────────────────────────────────────────────────
  const handleLogout = async () => {
    try { await adminAuthService.logout(); } catch (_) { }
    setAdmin(null);
    navigate('/admin/login');
  };

  // ── body overflow when mobile sidebar open ────────────────────────────────
  useEffect(() => {
    document.body.style.overflow = mobileOpen ? 'hidden' : '';
    return () => { document.body.style.overflow = ''; };
  }, [mobileOpen]);

  // On desktop: sidebar is always mini, hover expand is handled by CSS
  const sidebarClass = [
    'admin-sidebar',
    mobileOpen ? 'admin-sidebar-mobile-open' : '',
  ].filter(Boolean).join(' ');

  const mainClass = 'admin-main';

  const adminName = admin?.name || admin?.email || 'Admin';

  return (
    <div className="admin-shell">
      {/* Mobile backdrop */}
      {mobileOpen && <div className="admin-sidebar-backdrop" onClick={closeMobile} />}

      {/* ── SIDEBAR ──────────────────────────────────────────────────────── */}
      <aside
        className={sidebarClass}
        id="adminSidebar"
        aria-label="Main navigation"
      >
        <div className="admin-sidebar-header">
          <a className="admin-brand-mark" href="/admin" aria-label="Marketplace Admin">
            <span className="admin-brand-icon">
              <i className="bi bi-grid-1x2-fill" aria-hidden="true"></i>
            </span>
            <span className="admin-brand-copy">
              <span className="admin-banner-title">Marketplace</span>
              <span className="admin-banner-subtitle">Admin Panel</span>
            </span>
          </a>
        </div>

        <nav className="admin-sidebar-nav">
          {NAV_ITEMS.map(({ to, icon, label }) => (
            <NavLink
              key={to}
              to={to}
              className={({ isActive }) => `nav-link${isActive ? ' active' : ''}`}
              onClick={handleNavClick}
            >
              <span className="admin-nav-icon">
                <i className={`bi ${icon}`} aria-hidden="true"></i>
              </span>
              <span className="admin-nav-text">{label}</span>
            </NavLink>
          ))}
        </nav>

        <div className="admin-sidebar-footer">
          <span className="admin-status-dot"></span>
          <span className="admin-sidebar-footer-text">System running smoothly</span>
        </div>
      </aside>

      {/* ── MAIN ─────────────────────────────────────────────────────────── */}
      <div className={mainClass}>
        {/* Navbar */}
        <nav className="navbar admin-navbar navbar-expand bg-white">
          <div className="container-fluid px-3 px-lg-4">
            {/* Sidebar toggle (hamburger) */}
            <button
              className="admin-sidebar-toggle d-lg-none"
              type="button"
              onClick={handleSidebarToggle}
              aria-controls="adminSidebar"
              aria-expanded={isDesktop() ? false : mobileOpen}
              aria-label="Toggle sidebar"
            >
              <span></span>
              <span></span>
              <span></span>
            </button>

            {/* Search */}
            <form className="d-none d-md-flex flex-grow-1 gap-2 align-items-center" role="search" onSubmit={e => e.preventDefault()}>
              <svg xmlns="http://w3.org" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <circle cx="11" cy="11" r="8"></circle>
                <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
              </svg>
              <input
                className="form-control admin-search-input"
                type="search"
                placeholder="Search brands, products, orders…"
                aria-label="Search"
              />
            </form>

            {/* Right actions */}
            <div className="admin-navbar-actions ms-auto">
              {/* Theme toggle */}
              <button
                className="admin-icon-button theme-toggle"
                type="button"
                onClick={toggleTheme}
                aria-label={theme === 'dark' ? 'Switch to light mode' : 'Switch to dark mode'}
                title={theme === 'dark' ? 'Switch to light mode' : 'Switch to dark mode'}
              >
                <i className={`bi ${theme === 'dark' ? 'bi-sun' : 'bi-moon-stars'}`} aria-hidden="true"></i>
              </button>

              {/* Notifications */}
              <div className="dropdown">
                <button
                  className="admin-icon-button"
                  type="button"
                  data-bs-toggle="dropdown"
                  aria-expanded="false"
                  aria-label="Notifications"
                >
                  <span className="admin-notification-dot"></span>
                  <i className="bi bi-bell" aria-hidden="true"></i>
                </button>
                <div className="dropdown-menu dropdown-menu-end admin-notification-menu">
                  <div className="dropdown-header fw-bold text-body">Notifications</div>
                  <a className="dropdown-item" href="#">
                    <span className="admin-notification-title">New order received</span>
                    <span className="admin-notification-time">Just now</span>
                  </a>
                  <a className="dropdown-item" href="#">
                    <span className="admin-notification-title">New vendor registered</span>
                    <span className="admin-notification-time">10 minutes ago</span>
                  </a>
                  <a className="dropdown-item" href="#">
                    <span className="admin-notification-title">Product stock low</span>
                    <span className="admin-notification-time">1 hour ago</span>
                  </a>
                </div>
              </div>

              {/* Profile dropdown — opens on hover */}
              <div className="admin-profile-dropdown">
                <button className="admin-profile-button" type="button" aria-label="Profile menu">
                  <span className="admin-profile-avatar">
                    <i className="bi bi-person" aria-hidden="true"></i>
                  </span>
                  <span className="admin-profile-name d-none d-sm-inline">{adminName}</span>
                  <i className="bi bi-chevron-down admin-profile-caret" aria-hidden="true"></i>
                </button>
                <ul className="admin-profile-menu">
                  <li>
                    <span className="admin-profile-menu-header">
                      <span className="admin-profile-menu-name">{adminName}</span>
                      <span className="admin-profile-menu-email">{admin?.email}</span>
                    </span>
                  </li>
                  <li><hr className="admin-profile-menu-divider" /></li>
                  <li>
                    <button className="admin-profile-menu-item" onClick={() => navigate('/admin/profile')}>
                      <i className="bi bi-person-gear" aria-hidden="true"></i>
                      Profile Management
                    </button>
                  </li>
                  <li>
                    <button className="admin-profile-menu-item admin-profile-menu-item--danger" onClick={handleLogout}>
                      <i className="bi bi-box-arrow-right" aria-hidden="true"></i>
                      Logout
                    </button>
                  </li>
                </ul>
              </div>
            </div>
          </div>
        </nav>

        {/* Page content */}
        <main className="admin-dashboard-content">
          <Outlet />
        </main>

        {/* Footer */}
        <footer className="admin-footer">
          <div className="container-fluid px-3 px-lg-4">
            <span>© 2026 Marketplace Management. All rights reserved.</span>
            <span>Admin Dashboard</span>
          </div>
        </footer>
      </div>

      <PopupManager />
      <Chatbot />
    </div>
  );
}
