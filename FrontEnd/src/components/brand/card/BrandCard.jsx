// src/components/brand/BrandCard.jsx
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './BrandCard.css';

const FALLBACK_IMAGES = [
  "https://images.unsplash.com/photo-1560179707-f14e90ef3623?auto=format&fit=crop&w=300&q=80",
  "https://images.unsplash.com/photo-1559056199-641a0ac8b3f4?auto=format&fit=crop&w=300&q=80",
  "https://images.unsplash.com/photo-1611162617213-7d7a39e9b1d7?auto=format&fit=crop&w=300&q=80"
];

export default function BrandCard({ brand, selected, onSelect }) {
  const navigate = useNavigate();
  const [imageError, setImageError] = useState(false);

  const handleClick = () => {
    navigate(`/brand/${brand.id}`);
  };

  const handleCheckboxChange = (e) => {
    e.stopPropagation();
    onSelect(brand.id);
  };

  const handleImageError = () => {
    setImageError(true);
  };

  const displayImage = imageError ? FALLBACK_IMAGES[0] : (brand.image || FALLBACK_IMAGES[0]);
  const productCount = brand.productCount || 0;

  return (
    <div 
      className={`brand-card ${selected ? 'selected' : ''}`} 
      onClick={handleClick}
    >
      {/* Circular Image Wrapper */}
      <div className="brand-img-wrap">
        <img 
          src={displayImage} 
          alt={brand.name}
          onError={handleImageError}
        />
      </div>

      {/* Brand Name */}
      <div className="brand-name" title={brand.name}>
        {brand.name}
      </div>

      {/* Product Count Badge */}
      <div className="brand-badge">
        <i className="fa-solid fa-box"></i>
        <span>{productCount} Products</span>
      </div>

      {/* Explore Button */}
      <button 
        className="btn-explore"
        onClick={(e) => {
          e.stopPropagation();
          navigate(`/brand/${brand.id}`);
        }}
      >
        View Products
        <i className="fa-solid fa-arrow-right"></i>
      </button>

      {/* Selection Checkbox (if needed) */}
      {onSelect && (
        <input
          type="checkbox"
          className="brand-checkbox"
          checked={selected}
          onChange={handleCheckboxChange}
          onClick={(e) => e.stopPropagation()}
        />
      )}
    </div>
  );
}
