import { useContext, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './ProductCard.css';
import defaultProductImage from '../../../assets/product.png';
import { cartService } from '../../../services/cartService';
import { AuthContext } from '../../../contexts/AuthContext';

function ProductCard({ product, onCartUpdate }) {
  const navigate = useNavigate();
  const { user } = useContext(AuthContext);
  const { name, price, files, variants = [] } = product;
  const imageUrl = files && files.length > 0 ? files[0].url : defaultProductImage;

  const [selectedVariantId, setSelectedVariantId] = useState(
    variants.length > 0 ? variants[0].id : null
  );
  const [adding, setAdding] = useState(false);

  const handleClick = () => {
    navigate(`/product/${product.id}`);
  };

  const handleVariantChange = (e) => {
    e.stopPropagation();
    setSelectedVariantId(e.target.value);
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
      const res = await cartService.addItem(selectedVariantId, 1);
      if (res.ok) {
        onCartUpdate && onCartUpdate();
      } else {
        alert('Thêm vào giỏ hàng thất bại!');
      }
    } catch (err) {
      console.error(err);
      alert('Có lỗi xảy ra, vui lòng thử lại!');
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
          title="Thêm vào giỏ hàng"
        >
          {adding
            ? <i className="fas fa-spinner fa-spin"></i>
            : <i className="fas fa-cart-plus"></i>
          }
          <span>{adding ? 'Đang thêm...' : 'Thêm vào giỏ'}</span>
        </button>
      </div>
    </div>
  );
}

export default ProductCard;
