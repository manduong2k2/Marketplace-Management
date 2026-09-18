import { useState, useContext } from 'react';
import { cartService } from '../../../services/cartService';
import { AuthContext } from '../../../contexts/AuthContext';
import { CartContext } from '../../../contexts/CartContext';
import { showSuccess, showError } from '../../master/popup';
import { useNavigate } from 'react-router-dom';
import './ProductVariantPopup.css';

// Fallback images for localhost URLs
const FALLBACK_IMAGES = [
  "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?auto=format&fit=crop&w=800&q=80",
  "https://images.unsplash.com/photo-1611186871348-b1ce696e52c9?auto=format&fit=crop&w=800&q=80",
  "https://images.unsplash.com/photo-1525547719571-a2d4ac8945e2?auto=format&fit=crop&w=800&q=80"
];

function ProductVariantPopup({ product, onClose }) {
  const { user } = useContext(AuthContext);
  const { setCart } = useContext(CartContext);
  const navigate = useNavigate();
  
  const [selectedVariant, setSelectedVariant] = useState(
    product.variants && product.variants.length > 0 ? product.variants[0] : null
  );
  const [currentImageIndex, setCurrentImageIndex] = useState(0);
  const [quantity, setQuantity] = useState(1);
  const [adding, setAdding] = useState(false);
  const [isWishlist, setIsWishlist] = useState(false);
  
  // Group options by name
  const groupedOptions = product.options ? product.options.reduce((acc, option) => {
    if (!acc[option.name]) {
      acc[option.name] = [];
    }
    acc[option.name].push(option);
    return acc;
  }, {}) : {};

  // Initialize selected options
  const [selectedOptions, setSelectedOptions] = useState({});

  // Get images with fallback handling
  const getValidImageUrl = (url) => {
    if (!url) return FALLBACK_IMAGES[0];
    // Don't filter localhost - let it try to load and fallback via onError
    return url;
  };

  const getImages = () => {
    if (selectedVariant?.images && Array.isArray(selectedVariant.images)) {
      return selectedVariant.images.map(img => {
        // Handle both string URLs and objects with url property
        const urlString = typeof img === 'string' ? img : img.url;
        return getValidImageUrl(urlString);
      });
    }
    return [FALLBACK_IMAGES[0]];
  };

  const images = getImages();
  const currentImage = images[currentImageIndex];

  const price = selectedVariant?.price || 0;
  const stock = selectedVariant?.stock || 0;

  // Find variant based on selected options
  const findVariantByOptions = (options) => {
    if (!product.variants || product.variants.length === 0) return null;
    
    if (Object.keys(options).length === 0) {
      return product.variants[0];
    }
    
    const optionIds = Object.values(options).map(id => parseInt(id)).sort((a, b) => a - b);
    const optionListString = optionIds.join(', ');
    
    const matchingVariant = product.variants.find(variant => {
      const variantOptionList = variant.optionList || '';
      const variantOptionIds = variantOptionList
        .split(/[, ]+/)
        .map(id => parseInt(id.trim()))
        .filter(id => !isNaN(id))
        .sort((a, b) => a - b);
      const variantOptionListString = variantOptionIds.join(', ');
      return variantOptionListString === optionListString;
    });
    
    return matchingVariant || product.variants[0];
  };

  const handleOptionChange = (optionName, optionId) => {
    const newSelectedOptions = { ...selectedOptions };
    const currentSelected = newSelectedOptions[optionName];
    
    if (currentSelected == optionId) {
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

  const handleQuantityDecrease = () => {
    if (quantity > 1) {
      setQuantity(quantity - 1);
    }
  };

  const handleQuantityIncrease = () => {
    if (quantity < stock) {
      setQuantity(quantity + 1);
    }
  };

  const handleAddToCart = async () => {
    if (!user) {
      navigate('/login');
      return;
    }

    if (!selectedVariant) {
      showError('Please select product options');
      return;
    }

    if (adding) return;

    setAdding(true);
    try {
      const res = await cartService.addItem(selectedVariant.id, quantity);
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

  const handleImageError = (e) => {
    e.target.onerror = null; // Prevent infinite loop
    const currentIndex = images.indexOf(e.target.src);
    if (currentIndex !== -1 && currentIndex < FALLBACK_IMAGES.length) {
      // If there's a fallback for this index, use it
      e.target.src = FALLBACK_IMAGES[currentIndex % FALLBACK_IMAGES.length];
    } else {
      // Otherwise use the first fallback
      e.target.src = FALLBACK_IMAGES[0];
    }
  };

  return (
    <div className="modal-overlay active" onClick={onClose}>
      <div className="modal-container" onClick={(e) => e.stopPropagation()}>
        <button className="btn-close" onClick={onClose} title="Close modal">
          <i className="fa-solid fa-xmark"></i>
        </button>

        <div className="product-modal-grid">
          {/* Gallery Column */}
          <div className="product-gallery">
            <div className="main-image-wrap">
              <img 
                id="modalMainImg" 
                src={currentImage} 
                alt="Product Preview" 
                onError={handleImageError}
              />
            </div>
            {images.length > 1 && (
              <div className="gallery-thumbnails">
                {images.map((imgUrl, idx) => (
                  <div
                    key={idx}
                    className={`thumb-item ${idx === currentImageIndex ? 'active' : ''}`}
                    onClick={() => setCurrentImageIndex(idx)}
                  >
                    <img 
                      src={imgUrl} 
                      alt={`Thumbnail ${idx + 1}`}
                      onError={handleImageError}
                    />
                  </div>
                ))}
              </div>
            )}
          </div>

          {/* Product Info Column */}
          <div className="product-details">
            <div className="header-meta">
              <span className="brand-badge">{product.brand?.name || 'Brand'}</span>
              <span className={`status-badge ${product.status?.toLowerCase() === 'published' ? 'published' : 'draft'}`}>
                <span className="dot"></span>
                <span>{product.status || 'DRAFT'}</span>
              </span>
            </div>

            <h2 className="product-title">{product.name}</h2>

            <div className="price-stock-row">
              <div className="product-price">${parseFloat(price).toFixed(2)}</div>
              <div className={`stock-tag ${stock > 0 ? 'in-stock' : 'out-of-stock'}`}>
                {stock > 0 ? `${stock} in stock` : 'Out of Stock'}
              </div>
            </div>

            {/* Options Section */}
            {Object.keys(groupedOptions).length > 0 && (
              <div className="options-wrapper">
                {Object.entries(groupedOptions).map(([optionName, options]) => (
                  <div key={optionName} className="option-group">
                    <div className="option-title">
                      <span>{optionName}</span>
                      <span className="option-selected-val">
                        {selectedOptions[optionName] 
                          ? options.find(o => o.id === selectedOptions[optionName])?.value || 'Select'
                          : 'Select'}
                      </span>
                    </div>
                    <div className="option-pills">
                      {options.map(option => (
                        <button
                          key={option.id}
                          className={`option-pill ${selectedOptions[optionName] === option.id ? 'active' : ''}`}
                          onClick={() => handleOptionChange(optionName, option.id)}
                        >
                          {optionName.toLowerCase() === 'color' && (
                            <span 
                              className="color-dot" 
                              style={{ backgroundColor: option.value.toLowerCase() }}
                            ></span>
                          )}
                          <span>{option.value}</span>
                        </button>
                      ))}
                    </div>
                  </div>
                ))}
              </div>
            )}

            {/* Quantity & Action CTAs */}
            <div className="actions-row">
              <div className="quantity-control">
                <button className="qty-btn" onClick={handleQuantityDecrease} disabled={quantity <= 1}>
                  <i className="fa-solid fa-minus"></i>
                </button>
                <input 
                  type="number" 
                  className="qty-input" 
                  value={quantity} 
                  min="1" 
                  max={stock}
                  readOnly
                />
                <button className="qty-btn" onClick={handleQuantityIncrease} disabled={quantity >= stock}>
                  <i className="fa-solid fa-plus"></i>
                </button>
              </div>
              <button 
                className="btn-cta btn-add-cart" 
                onClick={handleAddToCart}
                disabled={adding || !selectedVariant || stock === 0}
              >
                <i className="fa-solid fa-bag-shopping"></i>
                {adding ? 'Adding...' : 'Add to Cart'}
              </button>
              <button 
                className={`btn-cta btn-wishlist ${isWishlist ? 'active' : ''}`}
                onClick={() => setIsWishlist(!isWishlist)}
                title="Add to wishlist"
              >
                <i className={`${isWishlist ? 'fa-solid' : 'fa-regular'} fa-heart`}></i>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default ProductVariantPopup;
