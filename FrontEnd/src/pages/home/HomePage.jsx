import { useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import ProductList from '../../components/product/list/ProductList';
import './HomePage.css';
import CategoryList from '../../components/category/list/CategoryList';
import BrandList from '../../components/brand/list/BrandList';

function HomePage() {
  const [searchParams] = useSearchParams();
  const [selectedCategoryId, setSelectedCategoryId] = useState(null);
  const [selectedBrandId, setSelectedBrandId] = useState(null);
  const searchQuery = searchParams.get('search') || '';

  const handleCategorySelect = (categoryId) => {
    setSelectedCategoryId(prev => prev === categoryId ? null : categoryId);
  };

  const handleBrandSelect = (brandId) => {
    setSelectedBrandId(prev => prev === brandId ? null : brandId);
  };

  return (
    <div className="homepage">
      <main className="homepage-content">
        <h1 className="homepage-title">Home</h1>
        <div className='catalog-section'>
          <div className="category-section">
            <CategoryList 
              selectedCategoryId={selectedCategoryId}
              onSelectCategory={handleCategorySelect}
            />
          </div>
          <div className="product-section">
            <BrandList 
              selectedBrandId={selectedBrandId}
              onSelectBrand={handleBrandSelect}
            />
            <ProductList
              categoryIds={selectedCategoryId ? [selectedCategoryId] : []}
              brandId={selectedBrandId}
              searchQuery={searchQuery}
            />
          </div>
        </div>
      </main>
    </div>
  );
}

export default HomePage;