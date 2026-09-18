// src/components/brand/BrandList.jsx
import React, { useState, useEffect, useRef } from 'react';
import BrandCard from '../card/BrandCard';
import { brandService } from '../../../services/brandService';
import './BrandList.css';

export default function BrandList({ selectedBrandId, onSelectBrand }) {
  const [brands, setBrands] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [currentIndex, setCurrentIndex] = useState(0);
  const sliderTrackRef = useRef(null);

  useEffect(() => {
    fetchBrands();
  }, []);

  useEffect(() => {
    window.addEventListener('resize', updateSliderPosition);
    return () => window.removeEventListener('resize', updateSliderPosition);
  }, [brands]);

  const fetchBrands = async () => {
    try {
      setLoading(true);
      const response = await brandService.getAll();
      const brandsData = response.data?.data || response;
      setBrands(brandsData);
    } catch (err) {
      setError('Cannot load brands');
    } finally {
      setLoading(false);
    }
  };

  const getVisibleCards = () => {
    const width = window.innerWidth;
    if (width <= 480) return 1;
    if (width <= 768) return 2;
    if (width <= 1024) return 3;
    return 4;
  };

  const updateSliderPosition = () => {
    const visibleCards = getVisibleCards();
    const maxIndex = Math.max(0, brands.length - visibleCards);
    if (currentIndex > maxIndex) {
      setCurrentIndex(maxIndex);
    }
  };

  const handlePrevious = () => {
    if (currentIndex > 0) {
      setCurrentIndex(currentIndex - 1);
    }
  };

  const handleNext = () => {
    const visibleCards = getVisibleCards();
    if (currentIndex < brands.length - visibleCards) {
      setCurrentIndex(currentIndex + 1);
    }
  };

  const visibleCards = getVisibleCards();
  const maxIndex = Math.max(0, brands.length - visibleCards);
  const canGoLeft = currentIndex > 0;
  const canGoRight = currentIndex < maxIndex;

  return (
    <div className="brand-list-container">
      <div className="showcase-header">
        <div className="header-title-wrap">
          <h2>
            <i className="fa-solid fa-tags"></i> Featured Brands
          </h2>
          <p>Explore top brands with thousands of quality products</p>
        </div>

        <div className="slider-controls">
          <button 
            className="nav-btn" 
            onClick={handlePrevious}
            disabled={!canGoLeft}
            aria-label="Previous brands"
          >
            <i className="fa-solid fa-chevron-left"></i>
          </button>
          <button 
            className="nav-btn" 
            onClick={handleNext}
            disabled={!canGoRight}
            aria-label="Next brands"
          >
            <i className="fa-solid fa-chevron-right"></i>
          </button>
        </div>
      </div>

      {loading ? (
        <div className="loading-container">
          <div className="spinner"></div>
          <p>Loading brands...</p>
        </div>
      ) : error ? (
        <div className="error-state">
          <p>{error}</p>
        </div>
      ) : brands.length === 0 ? (
        <div className="empty-state">
          <div className="empty-icon">📦</div>
          <h3>No brands available</h3>
          <p>Please check back later</p>
        </div>
      ) : (
        <>
          <div className="slider-viewport">
            <div 
              className="slider-track" 
              ref={sliderTrackRef}
              style={{
                transform: `translateX(calc(-${currentIndex * (100 / visibleCards)}% - ${currentIndex * (20 / visibleCards)}px))`
              }}
            >
              {brands.map(brand => (
                <BrandCard
                  key={brand.id}
                  brand={brand}
                  selected={selectedBrandId === brand.id}
                  onSelect={onSelectBrand}
                />
              ))}
            </div>
          </div>

          {/* Pagination Dots */}
          {maxIndex > 0 && (
            <div className="slider-pagination">
              {Array.from({ length: maxIndex + 1 }, (_, i) => (
                <div
                  key={i}
                  className={`dot ${i === currentIndex ? 'active' : ''}`}
                  onClick={() => setCurrentIndex(i)}
                />
              ))}
            </div>
          )}
        </>
      )}
    </div>
  );
}
