// src/components/category/CategoryCard.jsx
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './CategoryCard.css';
import defaultCategoryImage from '../../../assets/category.png';

export default function CategoryCard({ category, depth = 0, selectedCategoryId, onSelectCategory }) {
  const navigate = useNavigate();
  const [expanded, setExpanded] = useState(false);

  const hasChildren = category.children && category.children.length > 0;
  const selected = selectedCategoryId === category.id;

  const handleCardClick = () => {
    if (hasChildren) {
      setExpanded((prev) => !prev);
    } else {
      navigate(`/category/${category.id}`);
    }
  };

  const handleCheckboxChange = (e) => {
    e.stopPropagation();
    onSelectCategory(category.id);
  };

  return (
    <div className="category-card-wrapper" style={{ '--depth': depth }}>
      {/* Card row */}
      <div
        className={`category-card user-view ${hasChildren ? 'has-children' : ''} ${expanded ? 'expanded' : ''}`}
        style={{ paddingLeft: depth > 0 ? `${depth * 12}px` : undefined }}
        onClick={handleCardClick}
        role="button"
        tabIndex={0}
        onKeyDown={(e) => e.key === 'Enter' && handleCardClick()}
        aria-expanded={hasChildren ? expanded : undefined}
      >
        {/* Checkbox */}
        {onSelectCategory && (
          <input
            type="checkbox"
            className="category-checkbox"
            checked={selected}
            onChange={handleCheckboxChange}
            onClick={(e) => e.stopPropagation()}
          />
        )}

        {/* Depth indicator line */}
        {depth > 0 && <span className="depth-indicator" aria-hidden="true" />}

        <div className="category-image-container">
          <img
            src={category.image || defaultCategoryImage}
            alt={category.name}
            className="category-image"
            onError={(e) => { e.target.src = defaultCategoryImage; }}
          />
        </div>

        <div className="category-info">
          <h3 className="category-name">{category.name}</h3>
          {hasChildren && (
            <span className="category-children-count">
              {category.children.length} sub{category.children.length === 1 ? '' : 's'}
            </span>
          )}
        </div>

        {hasChildren && (
          <span className="category-chevron" aria-hidden="true">
            {expanded ? '▲' : '▼'}
          </span>
        )}
      </div>

      {/* Recursive children */}
      {hasChildren && expanded && (
        <div className="category-children">
          {category.children.map((child) => (
            <CategoryCard
              key={child.id}
              category={child}
              depth={depth + 1}
              selectedCategoryId={selectedCategoryId}
              onSelectCategory={onSelectCategory}
            />
          ))}
        </div>
      )}
    </div>
  );
}
