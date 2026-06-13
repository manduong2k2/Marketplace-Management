import { useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import './ProductCard.css';
import defaultProductImage from '../../../assets/product.png';
import { AuthContext } from '../../../contexts/AuthContext';

function ProductCard({ product, onOpenVariantPopup }) {
  const navigate = useNavigate();
  const { user } = useContext(AuthContext);
  const { name, images, variants } = product;
  
  const imageUrl = variants && variants.length > 0 && variants[0].images && variants[0].images.length > 0
    ? variants[0].images[0]
    : (images && images.length > 0 ? images[0] : defaultProductImage);

  const price = variants && variants.length > 0 ? variants[0].price : 0;

  const handleClick = () => {
    navigate(`/product/${product.id}`);
  };

  const handleAddToCart = (e) => {
    e.stopPropagation();
    onOpenVariantPopup && onOpenVariantPopup(product);
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
          className="add-to-cart-btn"
          onClick={handleAddToCart}
          title="Add to cart"
        >
          <i className="fas fa-cart-plus"></i>
          <span>Add to cart</span>
        </button>
      </div>
    </div>
  );
}

export default ProductCard;
