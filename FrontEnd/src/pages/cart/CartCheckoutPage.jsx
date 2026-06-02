import React, { useContext, useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { CartContext } from '../../contexts/CartContext';
import { AuthContext } from '../../contexts/AuthContext';
import { showSuccess, showError } from '../../components/master/popup';
import { orderService } from '../../services/orderService';
import { cartService } from '../../services/cartService';
import './CartCheckoutPage.css';

export default function CartCheckoutPage() {
  const navigate = useNavigate();
  const { cart, loading: cartLoading, setCart } = useContext(CartContext);
  const { user } = useContext(AuthContext);

  const [shippingInfo, setShippingInfo] = useState({
    name: '',
    phone: '',
    address: '',
    note: '',
  });
  const [errors, setErrors] = useState({});
  const [submitting, setSubmitting] = useState(false);

  // Pre-fill from user context
  useEffect(() => {
    if (user) {
      setShippingInfo(prev => ({
        ...prev,
        name: user.name || '',
        phone: user.phone || '',
        address: user.address || '',
      }));
    }
  }, [user]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setShippingInfo(prev => ({ ...prev, [name]: value }));
    // Clear error on change
    if (errors[name]) setErrors(prev => ({ ...prev, [name]: '' }));
  };

  const validate = () => {
    const newErrors = {};
    if (!shippingInfo.name.trim()) newErrors.name = 'Full name is required.';
    if (!shippingInfo.phone.trim()) newErrors.phone = 'Phone number is required.';
    if (!shippingInfo.address.trim()) newErrors.address = 'Delivery address is required.';
    return newErrors;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    const newErrors = validate();
    if (Object.keys(newErrors).length > 0) {
      setErrors(newErrors);
      return;
    }

    setSubmitting(true);
    try {
      const res = await orderService.create(shippingInfo);
      if (res.ok) {
        // Clear cart after successful order
        const clearRes = await cartService.clearCart();
        if (clearRes.ok) {
          const cartData = await cartService.getCart();
          if (cartData.data && cartData.data.cart) {
            setCart(cartData.data.cart);
          }
        }
        showSuccess('Order placed successfully!', 'Thank you');
        navigate('/home');
      } else {
        showError(res.data.message || 'Failed to place order. Please try again.', 'Error');
      }
    } catch (err) {
      showError('Failed to place order. Please try again.', 'Error');
    } finally {
      setSubmitting(false);
    }
  };

  if (cartLoading) {
    return (
      <div className="checkout-page">
        <div className="checkout-loading">
          <div className="spinner"></div>
          <p>Loading...</p>
        </div>
      </div>
    );
  }

  if (!cart?.items || cart.items.length === 0) {
    return (
      <div className="checkout-page">
        <div className="checkout-empty">
          <span className="empty-icon">🛒</span>
          <h2>Your cart is empty</h2>
          <p>Add some products before checking out.</p>
          <button className="btn-back" onClick={() => navigate('/home')}>
            Back to Shop
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="checkout-page">
      <div className="checkout-header">
        <button className="btn-back-link" onClick={() => navigate('/cart')}>
          ← Back to Cart
        </button>
        <h1>Checkout</h1>
      </div>

      <div className="checkout-body">

        {/* ── Left: Order summary (read-only) ── */}
        <section className="checkout-summary">
          <h2>Order Summary</h2>

          <div className="summary-items">
            {cart.items.map(item => (
              <div key={item.id} className="summary-item-row">
                <div className="summary-item-image">
                  {item.productImage && item.productImage.length > 0 ? (
                    <img src={item.productImage[0]} alt={item.productName} />
                  ) : (
                    <div className="no-image">No Image</div>
                  )}
                </div>

                <div className="summary-item-info">
                  <p className="summary-item-name">{item.productName}</p>
                  <p className="summary-item-meta">
                    ${item.productPrice.toFixed(2)} × {item.quantity}
                  </p>
                </div>

                <p className="summary-item-subtotal">
                  ${item.subTotal.toFixed(2)}
                </p>
              </div>
            ))}
          </div>

          <div className="summary-totals">
            <div className="totals-row">
              <span>Items</span>
              <span>{cart.totalItemCount}</span>
            </div>
            <div className="totals-row totals-grand">
              <span>Total</span>
              <span>${cart.total.toFixed(2)}</span>
            </div>
          </div>
        </section>

        {/* ── Right: Shipping form ── */}
        <section className="checkout-form-section">
          <h2>Shipping Information</h2>

          <form className="checkout-form" onSubmit={handleSubmit} noValidate>

            <div className="form-group">
              <label htmlFor="co-name">
                Full Name <span className="required">*</span>
              </label>
              <input
                id="co-name"
                type="text"
                name="name"
                value={shippingInfo.name}
                onChange={handleChange}
                placeholder="Your full name"
                className={errors.name ? 'input-error' : ''}
              />
              {errors.name && <span className="field-error">{errors.name}</span>}
            </div>

            <div className="form-group">
              <label htmlFor="co-phone">
                Phone <span className="required">*</span>
              </label>
              <input
                id="co-phone"
                type="tel"
                name="phone"
                value={shippingInfo.phone}
                onChange={handleChange}
                placeholder="e.g. 0901 234 567"
                className={errors.phone ? 'input-error' : ''}
              />
              {errors.phone && <span className="field-error">{errors.phone}</span>}
            </div>

            <div className="form-group">
              <label htmlFor="co-address">
                Delivery Address <span className="required">*</span>
              </label>
              <textarea
                id="co-address"
                name="address"
                value={shippingInfo.address}
                onChange={handleChange}
                placeholder="Street, district, city..."
                rows={3}
                className={errors.address ? 'input-error' : ''}
              />
              {errors.address && <span className="field-error">{errors.address}</span>}
            </div>

            <div className="form-group">
              <label htmlFor="co-note">Order Note (optional)</label>
              <textarea
                id="co-note"
                name="note"
                value={shippingInfo.note}
                onChange={handleChange}
                placeholder="Any special instructions..."
                rows={2}
              />
            </div>

            <button
              type="submit"
              className="btn-place-order"
              disabled={submitting}
            >
              {submitting ? 'Placing Order...' : `Place Order · $${cart.total.toFixed(2)}`}
            </button>

          </form>
        </section>

      </div>
    </div>
  );
}
