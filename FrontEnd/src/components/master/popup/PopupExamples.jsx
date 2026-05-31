// src/components/popup/PopupExamples.jsx
import React from 'react';
import { showSuccess, showWarning, showError, showFail, showInfo } from './index';

export default function PopupExamples() {
  const handleSuccess = () => {
    showSuccess('Thao tác thành công!', 'Thành công');
  };

  const handleWarning = () => {
    showWarning('Vui lòng kiểm tra lại thông tin!', 'Cảnh báo');
  };

  const handleError = () => {
    showError('Đã xảy ra lỗi, vui lòng thử lại!', 'Lỗi');
  };

  const handleFail = () => {
    showFail('Thao tác thất bại!', 'Thất bại');
  };

  const handleInfo = () => {
    showInfo('Đây là thông báo thông tin!', 'Thông tin');
  };

  const handleCustomDuration = () => {
    showSuccess('Thông báo sẽ tự động đóng sau 5 giây!', 'Tùy chỉnh', 5000);
  };

  return (
    <div className="popup-examples">
      <h3>Ví dụ Popup Notifications</h3>
      <div className="example-buttons">
        <button onClick={handleSuccess} className="btn btn-success">
          Success Popup
        </button>
        <button onClick={handleWarning} className="btn btn-warning">
          Warning Popup
        </button>
        <button onClick={handleError} className="btn btn-danger">
          Error Popup
        </button>
        <button onClick={handleFail} className="btn btn-fail">
          Fail Popup
        </button>
        <button onClick={handleInfo} className="btn btn-info">
          Info Popup
        </button>
        <button onClick={handleCustomDuration} className="btn btn-custom">
          Custom Duration (5s)
        </button>
      </div>
      
      <div className="usage-info">
        <h4>Cách sử dụng:</h4>
        <pre>
{`// Import functions
import { showSuccess, showWarning, showError, showFail, showInfo } from '../components/popup';

// Usage in any component
showSuccess('Message here', 'Title (optional)');
showWarning('Warning message', 'Warning');
showError('Error message', 'Error');
showFail('Failed message', 'Failed');
showInfo('Info message', 'Info');

// Custom duration (milliseconds)
showSuccess('Message', 'Title', 5000); // 5 seconds

// Or use global window functions
window.showSuccess('Message', 'Title');
window.showError('Error message');`}
        </pre>
      </div>
    </div>
  );
}
