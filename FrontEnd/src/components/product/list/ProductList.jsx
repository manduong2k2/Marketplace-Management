// src/components/product/ProductList.jsx
import React, { useEffect, useState } from 'react';
import ProductCard from '../card/ProductCard';
import { productService } from '../../../services/productService';
import './ProductList.css';

function ProductList() {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    async function fetchProducts() {
      try {
        setLoading(true);
        const response = await productService.getAll();
        setProducts(response.data.data);
      } catch (err) {
        setError('Cannot load products');
      } finally {
        setLoading(false);
      }
    }

    fetchProducts();
  }, []);

  if (loading) {
    return (
      <div className="product-list-container">
        <div className="loading-container">
          <div className="spinner"></div>
          <p>Loading products...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="product-list-container">
        <div className="error-state">
          <p>{error}</p>
        </div>
      </div>
    );
  }

  return (
    <div className="product-list-container user-view">
      <div className="list-header">
        <h2>Products</h2>
      </div>
      
      {products.length === 0 ? (
        <div className="empty-state">
          <div className="empty-icon">📦</div>
          <h3>No products available</h3>
          <p>Please check back later</p>
        </div>
      ) : (
        <div className="product-grid">
          {products.map(product => (
            <ProductCard key={product.id} product={product} />
          ))}
        </div>
      )}
    </div>
  );
}

export default ProductList;