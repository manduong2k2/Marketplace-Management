// src/components/popup/index.js
export { default as Popup } from './Popup';
export { default as PopupManager } from './PopupManager';

// Convenience functions for importing
export const showSuccess = (message, title, duration) => {
  if (window.showSuccess) {
    window.showSuccess(message, title, duration);
  }
};

export const showWarning = (message, title, duration) => {
  if (window.showWarning) {
    window.showWarning(message, title, duration);
  }
};

export const showError = (message, title, duration) => {
  if (window.showError) {
    window.showError(message, title, duration);
  }
};

export const showFail = (message, title, duration) => {
  if (window.showFail) {
    window.showFail(message, title, duration);
  }
};

export const showInfo = (message, title, duration) => {
  if (window.showInfo) {
    window.showInfo(message, title, duration);
  }
};
