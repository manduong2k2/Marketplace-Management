// src/contexts/AdminContext.jsx
import React, { createContext, useState, useEffect, useContext, useCallback } from 'react';
import { authService } from '../services/authService';
import { AUTH_REFRESHED_EVENT } from '../services/authInterceptor';

export const AdminContext = createContext();

export function AdminProvider({ children }) {
  const [admin, setAdmin] = useState(null);
  const [loading, setLoading] = useState(true);

  const fetchProfile = useCallback(async () => {
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
  }, []);

  useEffect(() => {
    fetchProfile();
    // Token refreshed: roles may have changed (e.g. ADMIN revoked) -> re-check admin access
    window.addEventListener(AUTH_REFRESHED_EVENT, fetchProfile);
    return () => window.removeEventListener(AUTH_REFRESHED_EVENT, fetchProfile);
  }, [fetchProfile]);

  return (
    <AdminContext.Provider value={{ admin, setAdmin, loading }}>
      {children}
    </AdminContext.Provider>
  );
}
