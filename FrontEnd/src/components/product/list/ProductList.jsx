// src/components/product/ProductList.jsx
import React, { useEffect, useState } from 'react';
import ProductCard from '../card/ProductCard';
import { productService } from '../../../services/productService';
import './ProductList.css';

function ProductList({ categoryIds = [], brandId = null, searchQuery: initialSearchQuery = '' }) {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchQuery, setSearchQuery] = useState(initialSearchQuery);
  const [sortBy, setSortBy] = useState('updatedAt');
  const [sortOrder, setSortOrder] = useState('desc');
  const [pagination, setPagination] = useState({
    currentPage: 0,
    pageSize: 16,
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
          sortBy: sortBy,
          sortOrder: sortOrder,
        };

        if (categoryIds.length > 0) {
          params.categoryIds = categoryIds;
        }

        if (brandId) {
          params.brandId = brandId;
        }

        if (searchQuery.trim()) {
          params.search = searchQuery.trim();
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
  }, [categoryIds, brandId, searchQuery, sortBy, sortOrder, pagination.currentPage]);

  useEffect(() => {
    setSearchQuery(initialSearchQuery);
  }, [initialSearchQuery]);

  const handlePageChange = (newPage) => {
    setPagination(prev => ({ ...prev, currentPage: newPage }));
  };

  const handleSearchChange = (e) => {
    setSearchQuery(e.target.value);
    setPagination(prev => ({ ...prev, currentPage: 0 }));
  };

  const handleSearchClear = () => {
    setSearchQuery('');
    setPagination(prev => ({ ...prev, currentPage: 0 }));
  };

  const handleSort = (field) => {
    if (sortBy === field) {
      setSortOrder(prev => prev === 'asc' ? 'desc' : 'asc');
    } else {
      setSortBy(field);
      setSortOrder('asc');
    }
    setPagination(prev => ({ ...prev, currentPage: 0 }));
  };

  if (loading) {
    return (
      <div className="product-list-container">
        <div className="product-list-loading-container">
          <div className="product-list-spinner"></div>
          <p>Loading products...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="product-list-container">
        <div className="product-list-error-state">
          <p>{error}</p>
        </div>
      </div>
    );
  }

  return (
    <div className="product-list-container user-view">
      <div className="list-header">
        <h2>Products</h2>
        {pagination.totalElements > 0 && (
          <span className="pagination-info">
            Page {pagination.currentPage + 1} of {pagination.totalPages} ({pagination.totalElements} total)
          </span>
        )}
      </div>

      <div className="search-bar">
        <input
          type="text"
          className="search-input"
          placeholder="Search products..."
          value={searchQuery}
          onChange={handleSearchChange}
        />
        {searchQuery && (
          <button className="search-clear" onClick={handleSearchClear}>
            ✕
          </button>
        )}
      </div>

      <div className="sort-bar">
        <span className="sort-label">Sort by:</span>
        <button
          className={`sort-btn ${sortBy === 'name' ? 'active' : ''}`}
          onClick={() => handleSort('name')}
        >
          Name {sortBy === 'name' ? (sortOrder === 'asc' ? '↑' : '↓') : ''}
        </button>
        <button
          className={`sort-btn ${sortBy === 'price' ? 'active' : ''}`}
          onClick={() => handleSort('price')}
        >
          Price {sortBy === 'price' ? (sortOrder === 'asc' ? '↑' : '↓') : ''}
        </button>
        <button
          className={`sort-btn ${sortBy === 'updatedAt' ? 'active' : ''}`}
          onClick={() => handleSort('updatedAt')}
        >
          Newest {sortBy === 'updatedAt' ? (sortOrder === 'asc' ? '↑' : '↓') : ''}
        </button>
      </div>
      
      {products.length === 0 ? (
        <div className="empty-state">
          <div className="empty-icon">📦</div>
          <h3>No products available</h3>
          <p>Please check back later</p>
        </div>
      ) : (
        <>
          <div className="product-grid">
            {products.map(product => (
              <ProductCard key={product.id} product={product} />
            ))}
          </div>
          
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
        </>
      )}
    </div>
  );
}

export default ProductList;