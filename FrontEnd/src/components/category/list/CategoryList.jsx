// src/components/category/CategoryList.jsx
import React, { useState, useEffect } from 'react';
import CategoryCard from '../card/CategoryCard';
import { categoryService } from '../../../services/categoryService';
import './CategoryList.css';

export default function CategoryList() {
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchCategories();
  }, []);

  const fetchCategories = async () => {
    try {
      setLoading(true);
      const response = await categoryService.getAll();
      setCategories(response.data.data || response);
    } catch (err) {
      setError('Cannot load categories');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="category-list-container user-view">
      <div className="list-header">
        <h2>Categories</h2>
      </div>

      {loading ? (
        <div className="loading-container">
          <div className="spinner"></div>
          <p>Loading categories...</p>
        </div>
      ) : error ? (
        <div className="error-state">
          <p>{error}</p>
        </div>
      ) : categories.length === 0 ? (
        <div className="empty-state">
          <div className="empty-icon">📦</div>
          <h3>No categories available</h3>
          <p>Please check back later</p>
        </div>
      ) : (
        <div className="category-list">
          {categories.map(category => (
            <CategoryCard
              key={category.id}
              category={category}
            />
          ))}
        </div>
      )}
    </div>
  );
}
