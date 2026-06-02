// src/routes/AppRouter.jsx
import React, { useContext } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';

import MasterLayout from '../layouts/master/MasterLayout';
import AuthLayout from '../layouts/auth/AuthLayout';
import AdminLayout from '../layouts/admin/AdminLayout';

import LoginPage from '../pages/auth/login/LoginPage';
import RegisterPage from '../pages/auth/register/RegisterPage';
import ForgotPasswordPage from '../pages/auth/forgot/ForgotPasswordPage';
import ProfilePage from '../pages/auth/profile/ProfilePage';
import HomePage from '../pages/home/HomePage';
import CartPage from '../pages/cart/CartPage';
import CartCheckoutPage from '../pages/cart/CartCheckoutPage';

import AdminLoginPage from '../pages/admin/login/AdminLoginPage';
import AdminBrandsPage from '../pages/admin/brands/AdminBrandsPage';
import AdminCategoriesPage from '../pages/admin/categories/AdminCategoriesPage';
import AdminProductsPage from '../pages/admin/products/AdminProductsPage';

import { AuthContext } from '../contexts/AuthContext';
import { AdminContext, AdminProvider } from '../contexts/AdminContext';
import '../index.css';

// ===== PrivateRoute =====
const PrivateRoute = ({ children }) => {
  const { user, loading } = useContext(AuthContext);
  if (loading) return <p>Loading...</p>;
  return user ? children : <Navigate to="/login" replace />;
};

// ===== AdminRoute — redirect to /admin/login if not admin =====
const AdminRoute = ({ children }) => {
  const { admin, loading } = useContext(AdminContext);
  if (loading) return <p>Loading...</p>;
  return admin ? children : <Navigate to="/admin/login" replace />;
};

// ===== PublicRoute =====
const PublicRoute = ({ children }) => children;

export default function AppRouter() {
  return (
    <Router>
      <AdminProvider>
        <Routes>

          {/* Auth pages */}
          <Route element={<AuthLayout />}>
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />
            <Route path="/forgot" element={<ForgotPasswordPage />} />
          </Route>

          {/* Admin login — standalone (no AdminLayout) */}
          <Route path="/admin/login" element={<AdminLoginPage />} />

          {/* Admin protected pages */}
          <Route
            element={
              <AdminRoute>
                <AdminLayout />
              </AdminRoute>
            }
          >
            <Route path="/admin/brands" element={<AdminBrandsPage />} />
            <Route path="/admin/categories" element={<AdminCategoriesPage />} />
            <Route path="/admin/products" element={<AdminProductsPage />} />
            {/* Default admin redirect */}
            <Route path="/admin" element={<Navigate to="/admin/brands" replace />} />
          </Route>

          {/* Route for all users */}
          <Route element={<PublicRoute><MasterLayout /></PublicRoute>}>
            <Route path="/home" element={<HomePage />} />
            <Route path="/profile" element={<PrivateRoute><ProfilePage /></PrivateRoute>} />
            <Route path="/cart" element={<PrivateRoute><CartPage /></PrivateRoute>} />
            <Route path="/checkout" element={<PrivateRoute><CartCheckoutPage /></PrivateRoute>} />
          </Route>

          {/* Root redirect */}
          <Route path="/" element={<Navigate to="/home" replace />} />

        </Routes>
      </AdminProvider>
    </Router>
  );
}
