// src/components/brand/BrandForm.jsx
import React, { useState } from 'react';
import './BrandForm.css';

export default function BrandForm({ brand, onSubmit, onCancel, loading = false }) {
  const [formData, setFormData] = useState({
    name: brand?.name || '',
    description: brand?.description || '',
    image: brand?.image || '',
    status: brand?.status || 'active'
  });
  
  const [errors, setErrors] = useState({});
  const [imagePreview, setImagePreview] = useState(brand?.image || '');

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
    
    // Clear error when user starts typing
    if (errors[name]) {
      setErrors(prev => ({
        ...prev,
        [name]: ''
      }));
    }
  };

  const handleImageChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      // Check file type
      if (!file.type.startsWith('image/')) {
        setErrors({ image: 'Vui lòng chọn file ảnh' });
        return;
      }
      
      // Check file size (max 5MB)
      if (file.size > 5 * 1024 * 1024) {
        setErrors({ image: 'Kích thước ảnh không được vượt quá 5MB' });
        return;
      }

      const reader = new FileReader();
      reader.onloadend = () => {
        setImagePreview(reader.result);
        setFormData(prev => ({
          ...prev,
          image: reader.result
        }));
      };
      reader.readAsDataURL(file);
    }
  };

  const validateForm = () => {
    const newErrors = {};
    
    if (!formData.name.trim()) {
      newErrors.name = 'Vui lòng nhập tên thương hiệu';
    }
    
    if (!formData.image) {
      newErrors.image = 'Vui lòng chọn ảnh thương hiệu';
    }
    
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    
    if (!validateForm()) {
      return;
    }
    
    onSubmit(formData);
  };

  const handleReset = () => {
    setFormData({
      name: '',
      description: '',
      image: '',
      status: 'active'
    });
    setImagePreview('');
    setErrors({});
  };

  return (
    <div className="brand-form-container">
      <form onSubmit={handleSubmit} className="brand-form">
        <h2>{brand ? 'Cập nhật thương hiệu' : 'Thêm thương hiệu mới'}</h2>
        
        <div className="form-row">
          <div className="form-group">
            <label htmlFor="name">Tên thương hiệu *</label>
            <input
              type="text"
              id="name"
              name="name"
              value={formData.name}
              onChange={handleChange}
              className={errors.name ? 'error' : ''}
              placeholder="Nhập tên thương hiệu"
              required
            />
            {errors.name && <span className="error-message">{errors.name}</span>}
          </div>
          
          <div className="form-group">
            <label htmlFor="status">Trạng thái</label>
            <select
              id="status"
              name="status"
              value={formData.status}
              onChange={handleChange}
              className="form-select"
            >
              <option value="active">Hoạt động</option>
              <option value="inactive">Không hoạt động</option>
            </select>
          </div>
        </div>

        <div className="form-group">
          <label htmlFor="description">Mô tả</label>
          <textarea
            id="description"
            name="description"
            value={formData.description}
            onChange={handleChange}
            placeholder="Nhập mô tả thương hiệu"
            rows="4"
            className="form-textarea"
          />
        </div>

        <div className="form-group">
          <label htmlFor="image">Ảnh thương hiệu *</label>
          <div className="image-upload-container">
            <input
              type="file"
              id="image"
              accept="image/*"
              onChange={handleImageChange}
              className="image-input"
            />
            
            {imagePreview ? (
              <div className="image-preview">
                <img 
                  src={imagePreview} 
                  alt="Preview" 
                  className="preview-image"
                />
                <button 
                  type="button" 
                  className="remove-image-btn"
                  onClick={() => {
                    setImagePreview('');
                    setFormData(prev => ({ ...prev, image: '' }));
                  }}
                >
                  ×
                </button>
              </div>
            ) : (
              <div className="upload-placeholder">
                <div className="upload-icon">📷</div>
                <p>Chọn ảnh thương hiệu</p>
                <small>Hỗ trợ: JPG, PNG, GIF (tối đa 5MB)</small>
              </div>
            )}
          </div>
          {errors.image && <span className="error-message">{errors.image}</span>}
        </div>

        <div className="form-actions">
          <button 
            type="button" 
            onClick={handleReset}
            className="btn btn-secondary"
            disabled={loading}
          >
            Làm mới
          </button>
          
          <button 
            type="button" 
            onClick={onCancel}
            className="btn btn-cancel"
            disabled={loading}
          >
            Hủy
          </button>
          
          <button 
            type="submit" 
            className="btn btn-primary"
            disabled={loading}
          >
            {loading ? 'Đang xử lý...' : (brand ? 'Cập nhật' : 'Thêm mới')}
          </button>
        </div>
      </form>
    </div>
  );
}
