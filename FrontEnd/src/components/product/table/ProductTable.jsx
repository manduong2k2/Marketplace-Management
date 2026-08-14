import React, { useState, useEffect } from 'react';
import { productService } from '../../../services/productService';
import defaultProductImage from '../../../assets/product.png';
import './ProductTable.css';

const EMPTY_ARRAY = [];

function ProductTable({ vendorId = null }) {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [pagination, setPagination] = useState({
    currentPage: 0,
    pageSize: 10,
    totalElements: 0,
    totalPages: 0,
    hasNext: false,
    hasPrevious: false,
  });

  useEffect(() => {
    async function fetchProducts() {
      try {
        setLoading(true);
        const params = {
          page: pagination.currentPage,
          size: pagination.pageSize,
          sortBy: 'updatedAt',
          sortOrder: 'desc',
        };

        if (vendorId) {
          params.vendorId = vendorId;
        }

        const response = await productService.getAll(params);
        setProducts(response.data.data);
        setPagination(response.data.pagination);
      } catch (err) {
        setError('Cannot load products');
      } finally {
        setLoading(false);
      }
    }

    fetchProducts();
  }, [vendorId, pagination.currentPage]);

  const handlePageChange = (newPage) => {
    setPagination(prev => ({ ...prev, currentPage: newPage }));
  };

  const handleEdit = (productId) => {
    window.location.href = `/product-edit/${productId}`;
  };

  const getPriceRange = (variants) => {
    if (!variants || variants.length === 0) return '-';
    const prices = variants.map(v => v.price).filter(p => p != null);
    if (prices.length === 0) return '-';
    const min = Math.min(...prices);
    const max = Math.max(...prices);
    return min === max ? `$${min}` : `$${min} ~ $${max}`;
  };

  const ImageSlider = ({ images }) => {
    const [currentIndex, setCurrentIndex] = useState(0);
    const imageList = images && images.length > 0 ? images : [defaultProductImage];

    useEffect(() => {
      if (imageList.length > 1) {
        const interval = setInterval(() => {
          setCurrentIndex((prev) => (prev + 1) % imageList.length);
        }, 3000);
        return () => clearInterval(interval);
      }
    }, [imageList.length]);

    const handlePrev = (e) => {
      e.stopPropagation();
      setCurrentIndex((prev) => (prev - 1 + imageList.length) % imageList.length);
    };

    const handleNext = (e) => {
      e.stopPropagation();
      setCurrentIndex((prev) => (prev + 1) % imageList.length);
    };

    return (
      <div className="image-slider">
        <img
          src={imageList[currentIndex]}
          alt="Product"
          className="product-table-image"
          onError={(e) => {
            e.target.src = defaultProductImage;
          }}
        />
        {imageList.length > 1 && (
          <>
            <button className="slider-btn slider-btn-prev" onClick={handlePrev}>
              ‹
            </button>
            <button className="slider-btn slider-btn-next" onClick={handleNext}>
              ›
            </button>
            <div className="slider-dots">
              {imageList.map((_, idx) => (
                <span
                  key={idx}
                  className={`slider-dot ${idx === currentIndex ? 'active' : ''}`}
                  onClick={(e) => {
                    e.stopPropagation();
                    setCurrentIndex(idx);
                  }}
                />
              ))}
            </div>
          </>
        )}
      </div>
    );
  };

  if (loading) {
    return (
      <div className="product-table-container">
        <div className="product-table-loading">
          <div className="spinner"></div>
          <p>Loading products...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="product-table-container">
        <div className="product-table-error">
          <p>{error}</p>
        </div>
      </div>
    );
  }

  return (
    <div className="product-table-container">
      <table className="product-table">
        <thead>
          <tr>
            <th className="col-image">Image</th>
            <th className="col-name">Name</th>
            <th className="col-price">Price</th>
            <th className="col-actions">Actions</th>
          </tr>
        </thead>
        <tbody>
          {products.length === 0 ? (
            <tr>
              <td colSpan="4" className="empty-row">
                <div className="empty-state">
                  <div className="empty-icon">📦</div>
                  <h3>No products available</h3>
                </div>
              </td>
            </tr>
          ) : (
            products.map((product) => {
              const variantImages = product.variants && product.variants.length > 0
                ? product.variants.flatMap(v => v.images || [])
                : (product.images || []);
              
              return (
                <tr key={product.id}>
                  <td className="col-image">
                    <ImageSlider images={variantImages} />
                  </td>
                  <td className="col-name">
                    <span className="product-name">{product.name}</span>
                  </td>
                  <td className="col-price">
                    <span className="price-range">{getPriceRange(product.variants)}</span>
                  </td>
                  <td className="col-actions">
                    <button
                      className="btn-edit"
                      onClick={() => handleEdit(product.id)}
                      title="Edit product"
                    >
                      <i className="fas fa-edit"></i>
                      Edit
                    </button>
                  </td>
                </tr>
              );
            })
          )}
        </tbody>
      </table>

      {pagination.totalPages > 1 && (
        <div className="pagination-controls">
          <button
            className="pagination-btn"
            disabled={!pagination.hasPrevious}
            onClick={() => handlePageChange(pagination.currentPage - 1)}
          >
            Previous
          </button>
          
          <div className="pagination-numbers">
            {Array.from({ length: pagination.totalPages }, (_, i) => (
              <button
                key={i}
                className={`pagination-number ${pagination.currentPage === i ? 'active' : ''}`}
                onClick={() => handlePageChange(i)}
              >
                {i + 1}
              </button>
            ))}
          </div>
          
          <button
            className="pagination-btn"
            disabled={!pagination.hasNext}
            onClick={() => handlePageChange(pagination.currentPage + 1)}
          >
            Next
          </button>
        </div>
      )}
    </div>
  );
}

export default ProductTable;
