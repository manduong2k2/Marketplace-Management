import React, { useState, useEffect } from 'react';
import { Outlet } from 'react-router-dom';
import Navbar from '../../components/master/navbar/Navbar';
import Footer from '../../components/master/footer/Footer';
import PopupManager from '../../components/master/popup/PopupManager';
import Spinner from '../../components/master/spinner/Spinner';
import '../../pages/auth/login/LoginPage.css';
import './AuthLayout.css';

export default function AuthLayout() {
  const [isLoading, setIsLoading] = useState(false);

  // Simple loading simulation for navigation
  useEffect(() => {
    const handleStart = () => setIsLoading(true);
    const handleEnd = () => setIsLoading(false);

    // Listen to navigation events (simplified approach)
    const originalPushState = window.history.pushState;
    const originalReplaceState = window.history.replaceState;

    window.history.pushState = function(...args) {
      handleStart();
      setTimeout(handleEnd, 300); // Simulate loading time
      return originalPushState.apply(window.history, args);
    };

    window.history.replaceState = function(...args) {
      handleStart();
      setTimeout(handleEnd, 300); // Simulate loading time
      return originalReplaceState.apply(window.history, args);
    };

    window.addEventListener('popstate', () => {
      handleStart();
      setTimeout(handleEnd, 300);
    });

    return () => {
      window.history.pushState = originalPushState;
      window.history.replaceState = originalReplaceState;
      window.removeEventListener('popstate', handleEnd);
    };
  }, []);

  return (
    <div className="auth-layout-wrapper">
      <PopupManager />
      <Navbar />
      <div className="auth-layout-page">
        <div className="auth-layout-card">
          <Outlet />
        </div>
      </div>
      <Footer />
      {isLoading && <Spinner />}
    </div>
  );
}