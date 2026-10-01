import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { orderService } from '../../../services/orderService';
import { showSuccess, showError } from '../../../components/master/popup';
import defaultProductImage from '../../../assets/product.png';
import './AdminOrderDetailPage.css';

function AdminOrderDetailPage() {
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
      <div className="admin-order-detail-page">
        <div className="admin-loading-container">
          <div className="admin-spinner"></div>
          <p>Loading order details...</p>
        </div>
      </div>
    );
  }

  if (error || !order) {
    return (
      <div className="admin-order-detail-page">
        <div className="admin-error-state">
          <p>{error || 'Order not found'}</p>
          <button className="admin-btn-back" onClick={() => navigate('/admin/orders')}>
            Back to Orders
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="admin-order-detail-page">
      <div className="admin-order-detail-container">
        <div className="admin-order-detail-header">
          <button className="admin-btn-back" onClick={() => navigate('/admin/orders')}>
            ← Back to Orders
          </button>
          <h1>Order Details</h1>
        </div>

        <div className="admin-order-detail-content">
          {/* Order Info */}
          <div className="admin-order-info-section">
            <div className="admin-info-header">
              <h2>Order Information</h2>
              <span
                className="admin-order-status-badge"
                style={{ backgroundColor: getStatusColor(order.status) }}
              >
                {order.status}
              </span>
            </div>

            <div className="admin-info-grid">
              <div className="admin-info-item">
                <label>Order ID:</label>
                <span>{order.id}</span>
              </div>
              <div className="admin-info-item">
                <label>User ID:</label>
                <span>{order.userId}</span>
              </div>
              <div className="admin-info-item">
                <label>Order Date:</label>
                <span>{new Date(order.createdAt).toLocaleString()}</span>
              </div>
              <div className="admin-info-item">
                <label>Last Updated:</label>
                <span>{new Date(order.updatedAt).toLocaleString()}</span>
              </div>
              <div className="admin-info-item">
                <label>Total:</label>
                <span className="admin-total-price">${order.total.toFixed(2)}</span>
              </div>
            </div>
          </div>

          {/* Shipping Info */}
          <div className="admin-shipping-info-section">
            <h2>Shipping Information</h2>
            <div className="admin-shipping-details">
              <div className="admin-shipping-item">
                <label>Name:</label>
                <span>{order.name}</span>
              </div>
              <div className="admin-shipping-item">
                <label>Phone:</label>
                <span>{order.phone}</span>
              </div>
              <div className="admin-shipping-item">
                <label>Address:</label>
                <span>{order.address}</span>
              </div>
              {order.note && (
                <div className="admin-shipping-item">
                  <label>Note:</label>
                  <span>{order.note}</span>
                </div>
              )}
            </div>
          </div>

          {/* Order Items */}
          <div className="admin-order-items-section">
            <h2>Order Items</h2>
            <div className="admin-items-list">
              {order.items && order.items.map((item) => (
                <div key={item.id} className="admin-order-item-card">
                  <div className="admin-item-image">
                    {item.snapShot?.productImages ? (
                      <img src={item.snapShot.productImages[0]} alt={item.snapShot.productName} />
                    ) : (
                      <img src={defaultProductImage} alt="Product" />
                    )}
                  </div>

                  <div className="admin-item-details">
                    <h3 className="admin-item-name">{item.snapShot?.productName || 'Product'}</h3>
                    <p className="admin-item-meta">
                      ${item.snapShot?.price?.toFixed(2) || '0.00'} × {item.quantity}
                    </p>
                    <p className="admin-item-product-id">Code: {item.snapShot.productCode}</p>
                  </div>

                  <div className="admin-item-total">
                    <span className="admin-item-total-price">${item.total.toFixed(2)}</span>
                  </div>
                </div>
              ))}
            </div>

            <div className="admin-order-summary">
              <div className="admin-summary-row">
                <span>Subtotal:</span>
                <span>${order.total.toFixed(2)}</span>
              </div>
              <div className="admin-summary-row admin-total">
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

export default AdminOrderDetailPage;
