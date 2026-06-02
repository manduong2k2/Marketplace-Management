import React, { useContext, useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { CartContext } from '../../contexts/CartContext';
import { cartService } from '../../services/cartService';
import { showSuccess, showError } from '../../components/master/popup';
import './CartPage.css';

function CartPage() {
  const navigate = useNavigate();
  const { cart, loading, setCart } = useContext(CartContext);
  const [quantities, setQuantities] = useState({});

  useEffect(() => {
    if (cart?.items) {
      const initialQuantities = {};
      cart.items.forEach(item => {
        initialQuantities[item.id] = item.quantity;
      });
      setQuantities(initialQuantities);
    }
  }, [cart]);

  const handleQuantityChange = async (itemId, newQuantity) => {
    setQuantities(prev => ({
      ...prev,
      [itemId]: Math.max(1, newQuantity)
    }));
    try {
      const res = await cartService.updateItem(itemId, newQuantity);
      if (res.ok) {
        // Refresh cart data
        const cartData = await cartService.getCart();
        if (cartData.data && cartData.data.cart) {
          setCart(cartData.data.cart);
        }
      }
    } catch (error) {
      console.error('Error updating quantity:', error);
    }
  };

  const handleDeleteItem = async (productId) => {
    try {
      const res = await cartService.removeItem(productId);
      if (res.ok) {
        // Refresh cart data
        const cartData = await cartService.getCart();
        if (cartData.data && cartData.data.cart) {
          setCart(cartData.data.cart);
        }
        showSuccess('Item deleted successfully');
      } else {
        showError(res.data.message || 'Failed to delete item');
      }
    } catch (error) {
      console.error('Error deleting item:', error);
      showError('Failed to delete item');
    }
  };

  const handleDeleteAll = async () => {
    try {
      const res = await cartService.clearCart();
      if (res.ok) {
        // Refresh cart data
        const cartData = await cartService.getCart();
        if (cartData.data && cartData.data.cart) {
          setCart(cartData.data.cart);
        }
        showSuccess('Cart cleared successfully');
      } else {
        showError(res.data.message || 'Failed to clear cart');
      }
    } catch (error) {
      console.error('Error clearing cart:', error);
      showError('Failed to clear cart');
    }
  };

  const handleCheckout = () => {
    navigate('/checkout');
  };

  if (loading) {
    return <div className="cart-page">Loading...</div>;
  }

  if (!cart.items || cart.items.length === 0) {
    return (
      <div className="cart-page">
        <div className="cart-empty">
          <h2>Your cart is empty</h2>
          <p>Add some products to your cart to see them here.</p>
        </div>
      </div>
    );
  }

  return (
    <div className="cart-page">
      <div className="cart-container">
        <div className="cart-header">
          <h1>Shopping Cart</h1>
          <button className="delete-all-btn" onClick={handleDeleteAll}>
            <i className="fas fa-trash"></i> Delete All
          </button>
        </div>

        <div className="cart-items">
          {cart.items.map(item => (
            <div key={item.id} className="cart-item">
              <div className="item-image">
                {item.productImage && item.productImage.length > 0 ? (
                  <img src={item.productImage[0]} alt={item.productName} />
                ) : (
                  <div className="no-image">No Image</div>
                )}
              </div>

              <div className="item-details">
                <h3 className="item-name">{item.productName}</h3>
                <p className="item-price">${item.productPrice.toFixed(2)}</p>
              </div>

              <div className="item-quantity">
                <label>Quantity:</label>
                <input
                  type="number"
                  min="1"
                  value={quantities[item.productId] || item.quantity}
                  onChange={(e) => handleQuantityChange(item.productId, parseInt(e.target.value))}
                  className="quantity-input"
                />
              </div>

              <div className="item-subtotal">
                <p>Subtotal:</p>
                <p className="subtotal-price">${item.subTotal.toFixed(2)}</p>
              </div>

              <button className="delete-item-btn" onClick={() => handleDeleteItem(item.productId)}>
                <i className="fas fa-trash"></i>
              </button>
            </div>
          ))}
        </div>

        <div className="cart-footer">
          <div className="cart-summary">
            <div className="summary-item">
              <span>Total Items:</span>
              <span>{cart.totalItemCount}</span>
            </div>
            <div className="summary-item total">
              <span>Total:</span>
              <span>${cart.total.toFixed(2)}</span>
            </div>
          </div>

          <button className="checkout-btn" onClick={handleCheckout}>
            <i className="fas fa-credit-card"></i> Checkout
          </button>
        </div>
      </div>
    </div>
  );
}

export default CartPage;
