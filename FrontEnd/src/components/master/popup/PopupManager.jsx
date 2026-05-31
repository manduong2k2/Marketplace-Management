// src/components/popup/PopupManager.jsx
import React, { useState, useCallback } from 'react';
import Popup from './Popup';
import './Popup.css';

export default function PopupManager() {
  const [popups, setPopups] = useState([]);

  const addPopup = useCallback((config) => {
    const id = Date.now() + Math.random();
    const newPopup = {
      id,
      type: config.type || 'info',
      message: config.message,
      title: config.title,
      duration: config.duration || 3000,
    };
    
    setPopups(prev => [...prev, newPopup]);
    
    return id;
  }, []);

  const removePopup = useCallback((id) => {
    setPopups(prev => prev.filter(popup => popup.id !== id));
  }, []);

  // Global popup functions
  React.useEffect(() => {
    // Attach popup functions to window for global access
    window.showPopup = addPopup;
    window.hidePopup = removePopup;
    
    // Convenience functions
    window.showSuccess = (message, title, duration) => 
      addPopup({ type: 'success', message, title, duration });
    
    window.showWarning = (message, title, duration) => 
      addPopup({ type: 'warning', message, title, duration });
    
    window.showError = (message, title, duration) => 
      addPopup({ type: 'error', message, title, duration });
    
    window.showFail = (message, title, duration) => 
      addPopup({ type: 'fail', message, title, duration });
    
    window.showInfo = (message, title, duration) => 
      addPopup({ type: 'info', message, title, duration });
  }, [addPopup, removePopup]);

  return (
    <div className="popup-manager">
      {popups.map(popup => (
        <Popup
          key={popup.id}
          type={popup.type}
          message={popup.message}
          title={popup.title}
          duration={popup.duration}
          onClose={() => removePopup(popup.id)}
          show={true}
        />
      ))}
    </div>
  );
}
