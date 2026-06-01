import { useContext, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './ProductCard.css';
import defaultProductImage from '../../../assets/product.png';
import { cartService } from '../../../services/cartService';
import { AuthContext } from '../../../contexts/AuthContext';
import { CartContext } from '../../../contexts/CartContext';
import { showSuccess, showError } from '../../master/popup';

function ProductCard({ product, onCartUpdate }) {
  const navigate = useNavigate();
  const { user } = useContext(AuthContext);
  const { setCart } = useContext(CartContext);
  const { name, price, images } = product;
  const imageUrl = images && images.length > 0 ? images[0] : defaultProductImage;

  const [adding, setAdding] = useState(false);

  const handleClick = () => {
    navigate(`/product/${product.id}`);
  };

  const handleAddToCart = async (e) => {
    e.stopPropagation();

    if (!user) {
      navigate('/login');
      return;
    }

    if (adding) return;

    setAdding(true);
    try {
      const res = await cartService.addItem(product.id, 1);
      if (res.ok) {
        // Refresh cart data
        const cartData = await cartService.getCart();
        if (cartData.data && cartData.data.cart) {
          setCart(cartData.data.cart);
        }
        onCartUpdate && onCartUpdate();
        showSuccess('Added to cart!');
      } else {
        showError('Failed to add to cart!');
      }
    } catch (err) {
      showError('Failed to add to cart!');
    } finally {
      setAdding(false);
    }
  };

  return (
    <div className="product-card user-view" onClick={handleClick}>
      <div className="product-image-container">
        <img
          src={imageUrl}
          alt={name}
          className="product-image"
          onError={(e) => {
            e.target.src = defaultProductImage;
          }}
        />
      </div>

      <div className="product-info">
        <h3 className="product-name">{name}</h3>
        {price >= 0 && <p className="product-price">${price}</p>}

        <button
          className={`add-to-cart-btn${adding ? ' loading' : ''}`}
          onClick={handleAddToCart}
          disabled={adding}
          title="Add to cart"
        >
          {adding
            ? <i className="fas fa-spinner fa-spin"></i>
            : <i className="fas fa-cart-plus"></i>
          }
          <span>{adding ? 'Adding...' : 'Add to cart'}</span>
        </button>
      </div>
    </div>
  );
}

export default ProductCard;
