// src/routes/AppRouter.jsx
import React, { useContext } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';

import MasterLayout from '../layouts/master/MasterLayout';
import AuthLayout from '../layouts/auth/AuthLayout';

import LoginPage from '../pages/auth/login/LoginPage';
import RegisterPage from '../pages/auth/register/RegisterPage';
import ForgotPasswordPage from '../pages/auth/forgot/ForgotPasswordPage';
import ProfilePage from '../pages/auth/profile/ProfilePage';
import HomePage from '../pages/home/HomePage';
import CartPage from '../pages/cart/CartPage';
import CartCheckoutPage from '../pages/cart/CartCheckoutPage';

import { AuthContext } from '../contexts/AuthContext';
import '../index.css';

// ===== PrivateRoute =====
const PrivateRoute = ({ children }) => {
  const { user, loading } = useContext(AuthContext);

  if (loading) return <p>Loading...</p>;
  return user ? children : <Navigate to="/login" replace />;
};

// ===== PublicRoute (optional logic) =====
const PublicRoute = ({ children }) => {
  return children;
};

export default function AppRouter() {
  return (
    <Router>
      <Routes>

        {/* Auth pages */}
        <Route element={<AuthLayout />}>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route path="/forgot" element={<ForgotPasswordPage />} />
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
    </Router>
  );
}