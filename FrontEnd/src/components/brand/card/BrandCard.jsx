// src/components/brand/BrandCard.jsx
import React from 'react';
import { useNavigate } from 'react-router-dom';
import './BrandCard.css';
import defaultBrandImage from '../../../assets/product.png'

export default function BrandCard({ brand }) {
  const navigate = useNavigate();

  const handleClick = () => {
    // Navigate to brand products page
    navigate(`/brand/${brand.id}`);
  };

  return (
    <div className="brand-card user-view" onClick={handleClick}>
      <div className="brand-image-container">
        <img 
          src={brand.image || defaultBrandImage} 
          alt={brand.name}
          className="brand-image"
          onError={(e) => {
            e.target.src = defaultBrandImage;
          }}
        />
      </div>
      
      <div className="brand-info">
        <h3 className="brand-name">{brand.name}</h3>
      </div>
    </div>
  );
}
