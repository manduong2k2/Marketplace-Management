// src/contexts/AuthContext.jsx
import React, { createContext, useState, useEffect, useCallback } from 'react';
import { authService } from '../services/authService';
import { AUTH_REFRESHED_EVENT } from '../services/authInterceptor';

export const AuthContext = createContext();

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  const fetchProfile = useCallback(async () => {
    try {
      const profile = await authService.profile();
      if (profile.data.data && profile.data.data.id) setUser(profile.data.data);
      else setUser(null);
    } catch {
      setUser(null);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchProfile();
    // Token refreshed (e.g. roles changed by an admin): reload the profile so the UI follows
    window.addEventListener(AUTH_REFRESHED_EVENT, fetchProfile);
    return () => window.removeEventListener(AUTH_REFRESHED_EVENT, fetchProfile);
  }, [fetchProfile]);

  return (
    <AuthContext.Provider value={{ user, setUser, loading }}>
      {children}
    </AuthContext.Provider>
  );
}