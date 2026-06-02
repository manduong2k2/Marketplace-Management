// src/pages/product/ProductDetailPage.jsx
import { useEffect, useState, useContext } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { productService } from '../../services/productService';
import { cartService } from '../../services/cartService';
import { AuthContext } from '../../contexts/AuthContext';
import { CartContext } from '../../contexts/CartContext';
import { showSuccess, showError } from '../../components/master/popup';
import defaultProductImage from '../../assets/product.png';
import './ProductDetailPage.css';

export default function ProductDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { user } = useContext(AuthContext);
  const { setCart } = useContext(CartContext);

  const [product, setProduct] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [quantity, setQuantity] = useState(1);
  const [adding, setAdding] = useState(false);

  const amount = product ? product.price * quantity : 0;

  useEffect(() => {
    const fetchProduct = async () => {
      try {
        setLoading(true);
        const res = await productService.getById(id);
        if (res.ok && res.data && res.data.data) {
          setProduct(res.data.data);
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

  const handleAddToCart = async () => {
    if (!user) {
      navigate('/login');
      return;
    }

    if (adding) return;

    setAdding(true);
    try {
      const res = await cartService.addItem(product.id, quantity);
      if (res.ok) {
        const cartData = await cartService.getCart();
        if (cartData.data && cartData.data.cart) {
          setCart(cartData.data.cart);
        }
        showSuccess(`Added ${quantity} item${quantity > 1 ? 's' : ''} to cart!`);
      } else {
        showError('Failed to add to cart!');
      }
    } catch {
      showError('Failed to add to cart!');
    } finally {
      setAdding(false);
    }
  };

  const handleQuantityChange = (e) => {
    const value = parseInt(e.target.value);
    if (value >= 1 && value <= product.stock) {
      setQuantity(value);
    }
  };

  const handleQuantityDecrease = () => {
    if (quantity > 1) {
      setQuantity(quantity - 1);
    }
  };

  const handleQuantityIncrease = () => {
    if (quantity < product.stock) {
      setQuantity(quantity + 1);
    }
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

  const imageUrl = product.images && product.images.length > 0 ? product.images[0] : defaultProductImage;

  return (
    <div className="product-detail-page">
      <button className="btn-back" onClick={() => navigate(-1)}>
        ← Back
      </button>

      <div className="product-detail-container">
        <div className="product-image-section">
          <div className="main-image">
            <img
              src={imageUrl}
              alt={product.name}
              onError={(e) => { e.target.src = defaultProductImage; }}
            />
          </div>
          {product.images && product.images.length > 1 && (
            <div className="thumbnail-gallery">
              {product.images.map((img, idx) => (
                <div
                  key={idx}
                  className={`thumbnail ${img === imageUrl ? 'active' : ''}`}
                  onClick={() => {/* TODO: Implement image switching */ }}
                >
                  <img src={img} alt={`${product.name} ${idx + 1}`} />
                </div>
              ))}
            </div>
          )}
        </div>

        <div className="product-info-section">
          <h1 className="product-title">{product.name}</h1>
          <p className="product-code">Code: {product.code}</p>

          <div className="product-price-section">
            <p className="product-detail-price">${Number(product.price).toLocaleString('en-US')}</p>
            <p className={`product-detail-stock ${product.stock > 0 ? 'in-stock' : 'out-of-stock'}`}>
              {product.stock > 0 ? `${product.stock} in stock` : 'Out of stock'}
            </p>
          </div>



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

          <div className="product-actions">
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
                max={product.stock}
              />
              <button
                className="quantity-btn"
                onClick={handleQuantityIncrease}
                disabled={quantity >= product.stock}
              >
                +
              </button>
            </div>

            <div className="product-amount-section">
              <span className="amount-label">Total Amount:</span>
              <span className="amount-value">${Number(amount).toLocaleString('en-US')}</span>
            </div>

            <button
              className={`btn-add-to-cart ${adding ? 'loading' : ''}`}
              onClick={handleAddToCart}
              disabled={adding || product.stock === 0}
            >
              {adding ? (
                <>
                  <i className="fas fa-spinner fa-spin"></i>
                  Adding...
                </>
              ) : product.stock === 0 ? (
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
