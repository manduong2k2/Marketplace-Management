// src/components/brand/BrandList.jsx
import React, { useState, useEffect, useRef } from 'react';
import BrandCard from '../card/BrandCard';
import { brandService } from '../../../services/brandService';
import './BrandList.css';

export default function BrandList() {
  const [brands, setBrands] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [canScrollLeft, setCanScrollLeft] = useState(false);
  const [canScrollRight, setCanScrollRight] = useState(false);
  const carouselRef = useRef(null);

  useEffect(() => {
    fetchBrands();
  }, []);

  useEffect(() => {
    const carousel = carouselRef.current;
    if (carousel) {
      const checkScroll = () => {
        setCanScrollLeft(carousel.scrollLeft > 0);
        setCanScrollRight(carousel.scrollWidth > carousel.scrollLeft + carousel.clientWidth);
      };
      checkScroll();
      carousel.addEventListener('scroll', checkScroll);
      return () => carousel.removeEventListener('scroll', checkScroll);
    }
  }, [brands]);

  const fetchBrands = async () => {
    try {
      setLoading(true);
      const response = await brandService.getAll();
      setBrands(response.data.data || response);
    } catch (err) {
      setError('Cannot load brands');
    } finally {
      setLoading(false);
    }
  };

  const scrollLeft = () => {
    if (carouselRef.current) {
      carouselRef.current.scrollBy({ left: -170, behavior: 'smooth' });
    }
  };

  const scrollRight = () => {
    if (carouselRef.current) {
      carouselRef.current.scrollBy({ left: 170, behavior: 'smooth' });
    }
  };

  return (
    <div className="brand-list-container user-view">
      <div className="list-header">
        <h2>Brands</h2>
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
        <div className="brand-carousel-wrapper">
          <button 
            className={`scroll-button scroll-left ${!canScrollLeft ? 'disabled' : ''}`}
            onClick={scrollLeft}
            disabled={!canScrollLeft}
          >
            ‹
          </button>
          <div className="brand-carousel" ref={carouselRef}>
            {brands.map(brand => (
              <BrandCard
                key={brand.id}
                brand={brand}
              />
            ))}
          </div>
          <button 
            className={`scroll-button scroll-right ${!canScrollRight ? 'disabled' : ''}`}
            onClick={scrollRight}
            disabled={!canScrollRight}
          >
            ›
          </button>
        </div>
      )}
    </div>
  );
}
