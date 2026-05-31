// src/components/popup/Popup.jsx
import React, { useEffect, useState } from 'react';
import './Popup.css';

export default function Popup({ 
  type = 'info', 
  message, 
  title, 
  duration = 3000, 
  onClose,
  show = false 
}) {
  const [isHiding, setIsHiding] = useState(false);

  useEffect(() => {
    if (show && duration > 0) {
      const timer = setTimeout(() => {
        setIsHiding(true);
        setTimeout(() => {
          onClose();
        }, 300); // Wait for animation to complete
      }, duration);
      return () => clearTimeout(timer);
    }
  }, [show, duration, onClose]);

  if (!show) return null;

  const getIcon = () => {
    switch (type) {
      case 'success':
        return '✓';
      case 'warning':
        return '⚠';
      case 'error':
        return '✕';
      case 'fail':
        return '✕';
      default:
        return 'ℹ';
    }
  };

  const getPopupClass = () => {
    return `popup popup-${type}`;
  };

  return (
    <div className={`${getPopupClass()} ${isHiding ? 'hiding' : ''}`}>
      <div className="popup-content">
        <div className="popup-icon">
          {getIcon()}
        </div>
        <div className="popup-message">
          {title && <h4 className="popup-title">{title}</h4>}
          <p className="popup-text">{message}</p>
        </div>
        <button 
          className="popup-close" 
          onClick={() => {
            setIsHiding(true);
            setTimeout(() => {
              onClose();
            }, 300);
          }}
          aria-label="Close popup"
        >
          ×
        </button>
      </div>
    </div>
  );
}
