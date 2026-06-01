import React from 'react';
import ProductList from '../../components/product/list/ProductList';
import './HomePage.css';
import CategoryList from '../../components/category/list/CategoryList';
import BrandList from '../../components/brand/list/BrandList';

function HomePage() {
  return (
    <div className="homepage">
      <main className="homepage-content">
        <h1 className="homepage-title">Home</h1>
        <div className='catalog-section'>
          <div className="category-section">
            <CategoryList />
          </div>
          <div className="product-section">
            <BrandList />
            <ProductList />
          </div>
        </div>
      </main>
    </div>
  );
}

export default HomePage;