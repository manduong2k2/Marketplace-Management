// src/layouts/admin/AdminLayout.jsx
import React, { useState, useContext } from 'react';
import { Outlet, NavLink, useNavigate } from 'react-router-dom';
import { AdminContext } from '../../contexts/AdminContext';
import { adminAuthService } from '../../services/adminAuthService';
import PopupManager from '../../components/master/popup/PopupManager';
import './AdminLayout.css';

export default function AdminLayout() {
  const [sidebarCollapsed, setSidebarCollapsed] = useState(false);
  const { admin, setAdmin } = useContext(AdminContext);
  const navigate = useNavigate();

  const handleLogout = async () => {
    try {
      await adminAuthService.logout();
    } catch (_) {
      // ignore
    }
    setAdmin(null);
    navigate('/admin/login');
  };

  return (
    <div className="admin-layout">
      {/* Sidebar */}
      <aside className={`admin-sidebar ${sidebarCollapsed ? 'collapsed' : ''}`}>
        <div className="admin-sidebar-header">
          <span className="admin-logo">🏪</span>
          {!sidebarCollapsed && <span className="admin-logo-text">Admin Panel</span>}
        </div>

        <nav className="admin-nav">
          <NavLink
            to="/admin/brands"
            className={({ isActive }) => `admin-nav-item ${isActive ? 'active' : ''}`}
          >
            <span className="nav-icon">🏷️</span>
            {!sidebarCollapsed && <span>Brands</span>}
          </NavLink>

          <NavLink
            to="/admin/categories"
            className={({ isActive }) => `admin-nav-item ${isActive ? 'active' : ''}`}
          >
            <span className="nav-icon">📂</span>
            {!sidebarCollapsed && <span>Categories</span>}
          </NavLink>

          <NavLink
            to="/admin/products"
            className={({ isActive }) => `admin-nav-item ${isActive ? 'active' : ''}`}
          >
            <span className="nav-icon">📦</span>
            {!sidebarCollapsed && <span>Products</span>}
          </NavLink>

          <NavLink
            to="/admin/orders"
            className={({ isActive }) => `admin-nav-item ${isActive ? 'active' : ''}`}
          >
            <span className="nav-icon">🧾</span>
            {!sidebarCollapsed && <span>Orders</span>}
          </NavLink>
        </nav>

        <button
          className="sidebar-toggle"
          onClick={() => setSidebarCollapsed(!sidebarCollapsed)}
          aria-label={sidebarCollapsed ? 'Expand sidebar' : 'Collapse sidebar'}
        >
          {sidebarCollapsed ? '›' : '‹'}
        </button>
      </aside>

      {/* Main content */}
      <div className={`admin-main ${sidebarCollapsed ? 'sidebar-collapsed' : ''}`}>
        {/* Top navbar */}
        <header className="admin-navbar">
          <div className="admin-navbar-left">
            <h1 className="admin-page-title">Store Management</h1>
          </div>
          <div className="admin-navbar-right">
            {admin && (
              <span className="admin-user-info">
                👤 {admin.name || admin.email}
              </span>
            )}
            <button className="admin-logout-btn" onClick={handleLogout}>
              Logout
            </button>
          </div>
        </header>

        {/* Page content */}
        <main className="admin-content">
          <Outlet />
        </main>
      </div>

      <PopupManager />
    </div>
  );
}
