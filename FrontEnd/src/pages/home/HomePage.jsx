import React from 'react';
import ProductList from '../../components/product/list/ProductList';
import './HomePage.css';
import CategoryList from '../../components/category/list/CategoryList';

function HomePage() {
  return (
    <div className="homepage">
      <main className="homepage-content">
        <h1>Home</h1>
        <CategoryList />
        <ProductList />
      </main>
    </div>
  );
}

export default HomePage;