// src/pages/product/ProductDetailPage.jsx
import { useEffect, useState, useContext } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { productService } from '../../services/productService';
import { AuthContext } from '../../contexts/AuthContext';
import ProductVariantPopup from '../../components/product/popup/ProductVariantPopup';
import defaultProductImage from '../../assets/product.png';
import './ProductDetailPage.css';
import { CartContext } from '../../contexts/CartContext';

export default function ProductDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { user } = useContext(AuthContext);
  const { setCart } = useContext(CartContext);

  const [product, setProduct] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showVariantPopup, setShowVariantPopup] = useState(false);
  const [selectedVariant, setSelectedVariant] = useState(null);
  const [selectedOptions, setSelectedOptions] = useState({});
  const [currentImageIndex, setCurrentImageIndex] = useState(0);

  useEffect(() => {
    const fetchProduct = async () => {
      try {
        setLoading(true);
        const res = await productService.getById(id);
        if (res.ok && res.data && res.data.data) {
          setProduct(res.data.data);
          console.log('Product loaded:', res.data.data);
        } else {
          setError('Product not found');
        }
      } catch {
        setError('Failed to load product');
      } finally {
        setLoading(false);
      }
    };

    fetchProduct();
  }, [id]);

  // Initialize selectedVariant when product loads
  useEffect(() => {
    if (product && product.variants && product.variants.length > 0) {
      setSelectedVariant(product.variants[0]);
      setCurrentImageIndex(0);
    }
  }, [product]);

  // Reset image index when variant changes
  useEffect(() => {
    setCurrentImageIndex(0);
  }, [selectedVariant]);

  // Group options by name
  const groupedOptions = product?.options ? product.options.reduce((acc, option) => {
    if (!acc[option.name]) {
      acc[option.name] = [];
    }
    acc[option.name].push(option);
    return acc;
  }, {}) : {};

  // Find variant based on selected options
  const findVariantByOptions = (options) => {
    if (!product?.variants || product.variants.length === 0) return null;
    
    // If no options selected, return first variant
    if (Object.keys(options).length === 0) {
      return product.variants[0];
    }
    
    const optionIds = Object.values(options).map(id => parseInt(id)).sort((a, b) => a - b);
    const optionListString = optionIds.join(', ');
    
    const matchingVariant = product.variants.find(variant => {
      // Handle different formats: "31, 32", "31,32", "31 32"
      const variantOptionList = variant.optionList || '';
      const variantOptionIds = variantOptionList
        .split(/[, ]+/) // Split by comma or space
        .map(id => parseInt(id.trim()))
        .filter(id => !isNaN(id))
        .sort((a, b) => a - b);
      const variantOptionListString = variantOptionIds.join(', ');
      return variantOptionListString === optionListString;
    });
    
    return matchingVariant || product.variants[0];
  };

  const handleOptionChange = (optionName, optionId) => {
    // Toggle: if clicking the same option, unselect it
    const newSelectedOptions = { ...selectedOptions };
    const currentSelected = newSelectedOptions[optionName];
    
    if (currentSelected == optionId) { // Use == for loose comparison to handle string/number mismatch
      delete newSelectedOptions[optionName];
    } else {
      newSelectedOptions[optionName] = optionId;
    }
    setSelectedOptions(newSelectedOptions);
    
    const matchingVariant = findVariantByOptions(newSelectedOptions);
    if (matchingVariant) {
      setSelectedVariant(matchingVariant);
    } else {
      setSelectedVariant(product.variants[0]);
    }
  };

  const handlePreviousImage = () => {
    if (images.length > 0) {
      setCurrentImageIndex(prev => prev === 0 ? images.length - 1 : prev - 1);
    }
  };

  const handleNextImage = () => {
    if (images.length > 0) {
      setCurrentImageIndex(prev => prev === images.length - 1 ? 0 : prev + 1);
    }
  };

  const handleAddToCart = () => {
    if (!user) {
      navigate('/login');
      return;
    }
    setShowVariantPopup(true);
  };

  const handleCloseVariantPopup = () => {
    setShowVariantPopup(false);
  };

  if (loading) {
    return (
      <div className="product-detail-page">
        <div className="loading-container">
          <div className="spinner"></div>
          <p>Loading product...</p>
        </div>
      </div>
    );
  }

  if (error || !product) {
    return (
      <div className="product-detail-page">
        <div className="error-state">
          <p>{error || 'Product not found'}</p>
          <button className="btn-back" onClick={() => navigate('/home')}>
            Back to Home
          </button>
        </div>
      </div>
    );
  }

  const images = selectedVariant?.images || (product.images || []);
  const currentImage = images[currentImageIndex] || defaultProductImage;

  const price = selectedVariant?.price || product.price || 0;
  const stock = selectedVariant?.stock || product.stock || 0;
  const amount = price * stock;

  return (
    <div className="product-detail-page">
      <button className="btn-back" onClick={() => navigate(-1)}>
        ← Back
      </button>

      <div className="product-detail-container">
        <div className="product-image-section">
          <div className="main-image">
            <img
              src={currentImage}
              alt={product.name}
              onError={(e) => { e.target.src = defaultProductImage; }}
            />
            {images.length > 1 && (
              <>
                <button className="image-nav-btn prev" onClick={handlePreviousImage}>‹</button>
                <button className="image-nav-btn next" onClick={handleNextImage}>›</button>
              </>
            )}
          </div>
          {images.length > 1 && (
            <div className="thumbnail-gallery">
              {images.map((img, idx) => (
                <div
                  key={idx}
                  className={`thumbnail ${idx === currentImageIndex ? 'active' : ''}`}
                  onClick={() => setCurrentImageIndex(idx)}
                >
                  <img src={img} alt={`${product.name} ${idx + 1}`} />
                </div>
              ))}
            </div>
          )}
        </div>

        <div className="product-info-section">
          <h1 className="product-title">{product.name}</h1>
          {product.description && (
            <div className="product-description">
              <h3>Description</h3>
              <p>{product.description}</p>
            </div>
          )}

          {product.brand && (
            <div className="product-brand">
              <span className="label">Brand: </span>
              <span className="value">{product.brand.name}</span>
            </div>
          )}

          {product.categories && product.categories.length > 0 && (
            <div className="product-categories">
              <span className="label">Categories:</span>
              <div className="category-tags">
                {product.categories.map((cat) => (
                  <span key={cat.id} className="category-tag">
                    {cat.name}
                  </span>
                ))}
              </div>
            </div>
          )}

          <div className="product-price-section">
            <p className="product-detail-price">${Number(price).toLocaleString('en-US')}</p>
          </div>

          <div className="product-stock-section">
            <span className="stock-label">Stock:</span>
            <span className={`stock-value ${stock > 0 ? 'in-stock' : 'out-of-stock'}`}>
              {stock > 0 ? `${stock} available` : 'Out of stock'}
            </span>
          </div>

          {Object.keys(groupedOptions).length > 0 && (
            <div className="product-options-section">
              {Object.entries(groupedOptions).map(([optionName, options]) => (
                <div key={optionName} className="option-group">
                  <h4 className="option-label">{optionName}</h4>
                  <div className="option-values">
                    {options.map(option => {
                      return (
                        <button
                          key={option.id}
                          className={`option-value-btn ${selectedOptions[optionName] == option.id ? 'selected' : ''}`}
                          onClick={() => handleOptionChange(optionName, option.id)}
                        >
                          {option.value}
                        </button>
                      );
                    })}
                  </div>
                </div>
              ))}
            </div>
          )}

          <div className="product-amount-section">
            <span className="amount-label">Total Amount (Price × Stock):</span>
            <span className="amount-value">${Number(amount).toLocaleString('en-US')}</span>
          </div>

          <div className="product-actions">
            <button
              className="btn-add-to-cart"
              onClick={handleAddToCart}
              disabled={stock === 0}
            >
              {stock === 0 ? (
                'Out of Stock'
              ) : (
                <>
                  <i className="fas fa-cart-plus"></i>
                  Add to Cart
                </>
              )}
            </button>
          </div>
        </div>
      </div>

      {showVariantPopup && product && (
        <ProductVariantPopup
          product={product}
          onClose={handleCloseVariantPopup}
        />
      )}
    </div>
  );
}
