import React, { useState, useEffect, useContext } from 'react';
import Navbar from '../../components/master/navbar/Navbar';
import Sidebar from '../../components/master/sidebar/Sidebar';
import Footer from '../../components/master/footer/Footer';
import PopupManager from '../../components/master/popup/PopupManager';
import Spinner from '../../components/master/spinner/Spinner';
import { AuthContext } from '../../contexts/AuthContext';
import './MasterLayout.css';
import { Outlet } from 'react-router-dom';

export default function MasterLayout() {
  const [sidebarExpanded, setSidebarExpanded] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const { user, setUser } = useContext(AuthContext);

  const toggleSidebar = () => {
    setSidebarExpanded(!sidebarExpanded);
  };

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
    <div className="master-layout">
      <Navbar onToggleSidebar={toggleSidebar} sidebarExpanded={sidebarExpanded} />
      <div className="layout-body">
        <Sidebar expanded={sidebarExpanded} />
        <main
          className="layout-main"
          style={{ marginLeft: sidebarExpanded ? 200 : 0 }}
        >
          <Outlet />
        </main>
      </div>
      <Footer />
      <PopupManager />
      {isLoading && <Spinner />}
    </div>
  );
}