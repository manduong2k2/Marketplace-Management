// src/routes/PrivateRoute.jsx
import React, { useContext } from 'react';
import { Navigate } from 'react-router-dom';
import { AuthContext } from '../contexts/AuthContext';

const PrivateRoute = ({ children }) => {
  const { user, loading } = useContext(AuthContext);

  // Nếu đang load profile, hiển thị loading
  if (loading) return <p>Loading...</p>;

  // Nếu đã login (user tồn tại) -> render children, nếu không -> redirect login
  return user ? children : <Navigate to="/login" replace />;
};

export default PrivateRoute;