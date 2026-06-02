import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { orderService } from '../../services/orderService';
import { showSuccess, showError } from '../../components/master/popup';
import defaultProductImage from '../../assets/product.png';
import './OrderDetailPage.css';

function OrderDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [order, setOrder] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchOrderDetail();
  }, [id]);

  const fetchOrderDetail = async () => {
    try {
      setLoading(true);
      const response = await orderService.getOrderById(id);
      if (response.ok && response.data) {
        setOrder(response.data);
      } else {
        setError('Order not found');
      }
    } catch (err) {
      setError('Failed to load order details');
    } finally {
      setLoading(false);
    }
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'PENDING': return '#ffc107';
      case 'CONFIRMED': return '#17a2b8';
      case 'SHIPPED': return '#007bff';
      case 'DELIVERED': return '#28a745';
      case 'CANCELLED': return '#dc3545';
      default: return '#6c757d';
    }
  };

  if (loading) {
    return (
      <div className="order-detail-page">
        <div className="loading-container">
          <div className="spinner"></div>
          <p>Loading order details...</p>
        </div>
      </div>
    );
  }

  if (error || !order) {
    return (
      <div className="order-detail-page">
        <div className="error-state">
          <p>{error || 'Order not found'}</p>
          <button className="btn-back" onClick={() => navigate('/orders')}>
            Back to Orders
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="order-detail-page">
      <div className="order-detail-container">
        <div className="order-detail-header">
          <button className="btn-back" onClick={() => navigate('/orders')}>
            ← Back to Orders
          </button>
          <h1>Order Details</h1>
        </div>

        <div className="order-detail-content">
          {/* Order Info */}
          <div className="order-info-section">
            <div className="info-header">
              <h2>Order Information</h2>
              <span
                className="order-status-badge"
                style={{ backgroundColor: getStatusColor(order.status) }}
              >
                {order.status}
              </span>
            </div>

            <div className="info-grid">
              <div className="info-item">
                <label>Order ID:</label>
                <span>{order.id}</span>
              </div>
              <div className="info-item">
                <label>Order Date:</label>
                <span>{new Date(order.createdAt).toLocaleString()}</span>
              </div>
              <div className="info-item">
                <label>Last Updated:</label>
                <span>{new Date(order.updatedAt).toLocaleString()}</span>
              </div>
              <div className="info-item">
                <label>Total:</label>
                <span className="total-price">${order.total.toFixed(2)}</span>
              </div>
            </div>
          </div>

          {/* Shipping Info */}
          <div className="shipping-info-section">
            <h2>Shipping Information</h2>
            <div className="shipping-details">
              <div className="shipping-item">
                <label>Name:</label>
                <span>{order.name}</span>
              </div>
              <div className="shipping-item">
                <label>Phone:</label>
                <span>{order.phone}</span>
              </div>
              <div className="shipping-item">
                <label>Address:</label>
                <span>{order.address}</span>
              </div>
              {order.note && (
                <div className="shipping-item">
                  <label>Note:</label>
                  <span>{order.note}</span>
                </div>
              )}
            </div>
          </div>

          {/* Order Items */}
          <div className="order-items-section">
            <h2>Order Items</h2>
            <div className="items-list">
              {order.items && order.items.map((item) => (
                <div key={item.id} className="order-item-card">
                  <div className="item-image">
                    {item.snapShot?.productImages && item.snapShot.productImages.length > 0 ? (
                      <img src={item.snapShot.productImages[0]} alt={item.snapShot.name} />
                    ) : (
                      <img src={defaultProductImage} alt="Product" />
                    )}
                  </div>

                  <div className="item-details">
                    <h3 className="item-name">{item.snapShot?.productName || 'Product'}</h3>
                    <p className="item-meta">
                      ${item.snapShot?.price?.toFixed(2) || '0.00'} × {item.quantity}
                    </p>
                  </div>

                  <div className="item-total">
                    <span className="item-total-price">${item.total.toFixed(2)}</span>
                  </div>
                </div>
              ))}
            </div>

            <div className="order-summary">
              <div className="summary-row">
                <span>Subtotal:</span>
                <span>${order.total.toFixed(2)}</span>
              </div>
              <div className="summary-row total">
                <span>Total:</span>
                <span>${order.total.toFixed(2)}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default OrderDetailPage;
