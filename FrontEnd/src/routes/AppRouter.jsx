// src/routes/AppRouter.jsx
import { useContext } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';

import MasterLayout from '../layouts/master/MasterLayout';
import AuthLayout from '../layouts/auth/AuthLayout';
import AdminLayout from '../layouts/admin/AdminLayout';

import LoginPage from '../pages/auth/login/LoginPage';
import RegisterPage from '../pages/auth/register/RegisterPage';
import ForgotPasswordPage from '../pages/auth/forgot/ForgotPasswordPage';
import ProfilePage from '../pages/auth/profile/ProfilePage';
import AddressPage from '../pages/auth/address/AddressPage';
import HomePage from '../pages/home/HomePage';
import CartPage from '../pages/cart/CartPage';
import CartCheckoutPage from '../pages/cart/CartCheckoutPage';

import AdminLoginPage from '../pages/admin/login/AdminLoginPage';
import AdminDashboardPage from '../pages/admin/dashboard/AdminDashboardPage';
import AdminBrandsPage from '../pages/admin/brands/AdminBrandsPage';
import AdminCategoriesPage from '../pages/admin/categories/AdminCategoriesPage';
import AdminProductsPage from '../pages/admin/products/AdminProductsPage';
import AdminProductDetailPage from '../pages/admin/products/AdminProductDetailPage';
import AdminVendorsPage from '../pages/admin/vendors/AdminVendorsPage';

import ProductDetailPage from '../pages/product/ProductDetailPage';
import BrandDetailPage from '../pages/brand/BrandDetailPage';
import OrderHistoryPage from '../pages/order/OrderHistoryPage';
import OrderDetailPage from '../pages/order/OrderDetailPage';
import AdminOrderHistoryPage from '../pages/admin/orders/AdminOrderHistoryPage';
import AdminOrderDetailPage from '../pages/admin/orders/AdminOrderDetailPage';
import OnboardingPage from '../pages/onboarding/OnboardingPage';
import VendorDetailPage from '../pages/vendor/detail/VendorDetailPage';
import MyVendorPage from '../pages/vendor/my/MyVendorPage';
import VendorCreatePage from '../pages/vendor/create/VendorCreatePage';

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

          <Route path="/onboarding" element={<OnboardingPage />} />

          {/* Admin protected pages */}
          <Route
            element={
              <AdminRoute>
                <AdminLayout />
              </AdminRoute>
            }
          >
            <Route path="/admin/dashboard" element={<AdminDashboardPage />} />
            <Route path="/admin/brands" element={<AdminBrandsPage />} />
            <Route path="/admin/categories" element={<AdminCategoriesPage />} />
            <Route path="/admin/products" element={<AdminProductsPage />} />
            <Route path="/admin/products/:id" element={<AdminProductDetailPage />} />
            <Route path="/admin/vendors" element={<AdminVendorsPage />} />
            <Route path="/admin/orders" element={<AdminOrderHistoryPage />} />
            <Route path="/admin/orders/:id" element={<AdminOrderDetailPage />} />
            {/* Default admin redirect */}
            <Route path="/admin" element={<Navigate to="/admin/dashboard" replace />} />
          </Route>

          {/* Route for all users */}
          <Route element={<PublicRoute><MasterLayout /></PublicRoute>}>
            <Route path="/home" element={<HomePage />} />
            <Route path="/product/:id" element={<ProductDetailPage />} />
            <Route path="/brand/:id" element={<BrandDetailPage />} />
            <Route path="/vendor/:id" element={<VendorDetailPage />} />
          </Route>

          {/* Routes for logged in users */}
          <Route element={<PrivateRoute><MasterLayout /></PrivateRoute>}>
            <Route path="/profile" element={<ProfilePage />} />
            <Route path="/addresses" element={<AddressPage />} />
            <Route path="/cart" element={<CartPage />} />
            <Route path="/checkout" element={<CartCheckoutPage />} />
            <Route path="/orders" element={<OrderHistoryPage />} />
            <Route path="/orders/:id" element={<OrderDetailPage />} />
            <Route path="/my-vendor" element={<MyVendorPage />} />
            <Route path="/vendor-create" element={<VendorCreatePage />} />
          </Route>

          {/* Root redirect */}
          <Route path="/" element={<Navigate to="/home" replace />} />

        </Routes>
      </AdminProvider>
    </Router>
  );
}
