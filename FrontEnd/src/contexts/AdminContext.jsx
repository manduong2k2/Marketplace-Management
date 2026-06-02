// src/contexts/AdminContext.jsx
import React, { createContext, useState, useEffect, useContext } from 'react';
import { authService } from '../services/authService';

export const AdminContext = createContext();

export function AdminProvider({ children }) {
  const [admin, setAdmin] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchProfile = async () => {
      try {
        const profile = await authService.profile();
        const user = profile.data?.data;
        // Check if user has Admin role
        if (user && user.roles && user.roles.includes('Admin')) {
          setAdmin(user);
        } else {
          setAdmin(null);
        }
      } catch {
        setAdmin(null);
      } finally {
        setLoading(false);
      }
    };
    fetchProfile();
  }, []);

  return (
    <AdminContext.Provider value={{ admin, setAdmin, loading }}>
      {children}
    </AdminContext.Provider>
  );
}
