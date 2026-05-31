// src/components/category/CategoryCard.jsx
import React from 'react';
import { useNavigate } from 'react-router-dom';
import './CategoryCard.css';
import defaultCategoryImage from '../../../assets/category.png'

export default function CategoryCard({ category }) {
  const navigate = useNavigate();

  const handleClick = () => {
    // Navigate to category products page
    navigate(`/category/${category.id}`);
  };

  return (
    <div className="category-card user-view" onClick={handleClick}>
      <div className="category-image-container">
        <img 
          src={category.image || defaultCategoryImage} 
          alt={category.name}
          className="category-image"
          onError={(e) => {
            e.target.src = defaultCategoryImage;
          }}
        />
      </div>
      
      <div className="category-info">
        <h3 className="category-name">{category.name}</h3>
      </div>
    </div>
  );
}
