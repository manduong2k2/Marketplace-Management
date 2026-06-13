import { useState, useEffect, useContext } from 'react';
import { cartService } from '../../../services/cartService';
import { AuthContext } from '../../../contexts/AuthContext';
import { CartContext } from '../../../contexts/CartContext';
import { showSuccess, showError } from '../../master/popup';
import defaultProductImage from '../../../assets/product.png';
import './ProductVariantPopup.css';

function ProductVariantPopup({ product, onClose }) {
  const { user } = useContext(AuthContext);
  const { setCart } = useContext(CartContext);
  
  const [selectedVariant, setSelectedVariant] = useState(
    product.variants && product.variants.length > 0 ? product.variants[0] : null
  );
  const [currentImageIndex, setCurrentImageIndex] = useState(0);
  const [quantity, setQuantity] = useState(1);
  const [adding, setAdding] = useState(false);
  
  // Group options by name
  const groupedOptions = product.options ? product.options.reduce((acc, option) => {
    if (!acc[option.name]) {
      acc[option.name] = [];
    }
    acc[option.name].push(option);
    return acc;
  }, {}) : {};

  // Initialize selected options as empty (no options selected by default)
  const [selectedOptions, setSelectedOptions] = useState({});

  const images = selectedVariant?.images || [];
  const currentImage = images[currentImageIndex] || defaultProductImage;

  const price = selectedVariant?.price || 0;
  const stock = selectedVariant?.stock || 0;
  const amount = price * stock;

  // Find variant based on selected options
  const findVariantByOptions = (options) => {
    if (!product.variants || product.variants.length === 0) return null;
    
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
      setCurrentImageIndex(0);
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

  const handleQuantityChange = (e) => {
    const value = parseInt(e.target.value);
    if (value >= 1 && value <= (selectedVariant?.stock || 0)) {
      setQuantity(value);
    }
  };

  const handleQuantityDecrease = () => {
    if (quantity > 1) {
      setQuantity(quantity - 1);
    }
  };

  const handleQuantityIncrease = () => {
    if (quantity < (selectedVariant?.stock || 0)) {
      setQuantity(quantity + 1);
    }
  };

  const handleAddToCart = async () => {
    if (!user) {
      showError('Please login to add items to cart');
      return;
    }

    if (!selectedVariant) {
      showError('Please select a variant');
      return;
    }

    if (adding) return;

    setAdding(true);
    try {
      const res = await cartService.addItem(product.id, quantity, selectedVariant.id);
      if (res.ok) {
        const cartData = await cartService.getCart();
        if (cartData.data && cartData.data.cart) {
          setCart(cartData.data.cart);
        }
        showSuccess(`Added ${quantity} item${quantity > 1 ? 's' : ''} to cart!`);
        onClose();
      } else {
        showError('Failed to add to cart!');
      }
    } catch {
      showError('Failed to add to cart!');
    } finally {
      setAdding(false);
    }
  };

  return (
    <div className="variant-popup-overlay" onClick={onClose}>
      <div className="variant-popup-content" onClick={(e) => e.stopPropagation()}>
        <button className="variant-popup-close" onClick={onClose}>✕</button>
        
        <div className="variant-popup-body">
          {/* Image Slider */}
          <div className="variant-image-section">
            <div className="variant-main-image">
              <img
                src={currentImage}
                alt={selectedVariant?.name || product.name}
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
              <div className="variant-thumbnail-gallery">
                {images.map((img, idx) => (
                  <div
                    key={idx}
                    className={`variant-thumbnail ${idx === currentImageIndex ? 'active' : ''}`}
                    onClick={() => setCurrentImageIndex(idx)}
                  >
                    <img src={img} alt={`${product.name} ${idx + 1}`} />
                  </div>
                ))}
              </div>
            )}
          </div>

          {/* Product Info */}
          <div className="variant-info-section">
            <h2 className="variant-product-name">{product.name}</h2>
            {selectedVariant && (
              <p className="variant-name">{selectedVariant.name}</p>
            )}
            
            <div className="variant-price-stock">
              <p className="variant-price">${Number(price).toLocaleString('en-US')}</p>
              <p className={`variant-stock ${stock > 0 ? 'in-stock' : 'out-of-stock'}`}>
                {stock > 0 ? `${stock} in stock` : 'Out of stock'}
              </p>
            </div>

            <div className="variant-amount-section">
              <span className="amount-label">Total Amount (Price × Stock):</span>
              <span className="amount-value">${Number(amount).toLocaleString('en-US')}</span>
            </div>

            {/* Options Section */}
            {Object.keys(groupedOptions).length > 0 && (
              <div className="variant-options-section">
                {Object.entries(groupedOptions).map(([optionName, options]) => (
                  <div key={optionName} className="option-group">
                    <h4 className="option-label">{optionName}</h4>
                    <div className="option-values">
                      {options.map(option => (
                        <button
                          key={option.id}
                          className={`option-value-btn ${selectedOptions[optionName] === option.id ? 'selected' : ''}`}
                          onClick={() => handleOptionChange(optionName, option.id)}
                        >
                          {option.value}
                        </button>
                      ))}
                    </div>
                  </div>
                ))}
              </div>
            )}

            {/* Quantity Selector */}
            <div className="variant-quantity-section">
              <div className="quantity-selector">
                <button
                  className="quantity-btn"
                  onClick={handleQuantityDecrease}
                  disabled={quantity <= 1}
                >
                  -
                </button>
                <input
                  type="number"
                  className="quantity-input"
                  value={quantity}
                  onChange={handleQuantityChange}
                  min="1"
                  max={selectedVariant?.stock || 0}
                />
                <button
                  className="quantity-btn"
                  onClick={handleQuantityIncrease}
                  disabled={quantity >= (selectedVariant?.stock || 0)}
                >
                  +
                </button>
              </div>
            </div>

            {/* Add to Cart Button */}
            <button
              className={`variant-add-to-cart-btn ${adding ? 'loading' : ''}`}
              onClick={handleAddToCart}
              disabled={adding || !selectedVariant || selectedVariant.stock === 0}
            >
              {adding ? (
                <>
                  <i className="fas fa-spinner fa-spin"></i>
                  Adding...
                </>
              ) : !selectedVariant || selectedVariant.stock === 0 ? (
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
    </div>
  );
}

export default ProductVariantPopup;
