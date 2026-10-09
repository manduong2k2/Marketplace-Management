// src/contexts/AdminContext.jsx
import React, { createContext, useContext, useCallback } from 'react';
import { AuthContext } from './AuthContext';

export const AdminContext = createContext();

// Admin access is derived from the shared AuthContext user (one session for the store and the admin area),
// so logging in from /auth, /admin/login or via Google/Facebook is reflected here immediately, and
// AuthContext's reload on token refresh also re-checks the Admin role.
export function AdminProvider({ children }) {
  const { user, setUser, loading } = useContext(AuthContext);

  const admin = user?.roles?.includes('Admin') ? user : null;

  // Setting/clearing the admin is setting/clearing the logged-in user
  const setAdmin = useCallback((value) => setUser(value), [setUser]);

  return (
    <AdminContext.Provider value={{ admin, setAdmin, loading }}>
      {children}
    </AdminContext.Provider>
  );
}
