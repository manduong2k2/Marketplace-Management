import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { orderService } from '../../../services/orderService';
import { showSuccess, showError } from '../../../components/master/popup';
import './AdminOrderHistoryPage.css';

function AdminOrderHistoryPage() {
  const navigate = useNavigate();
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [pagination, setPagination] = useState({
    currentPage: 0,
    pageSize: 10,
    totalElements: 0,
    totalPages: 0,
  });
  const [filters, setFilters] = useState({
    status: '',
    search: '',
    sortBy: 'createdAt',
    sortOrder: 'desc',
  });

  useEffect(() => {
    fetchOrders();
  }, [filters.status, filters.search, filters.sortBy, filters.sortOrder, pagination.currentPage]);

  const fetchOrders = async () => {
    try {
      setLoading(true);
      const params = {
        page: pagination.currentPage,
        size: pagination.pageSize,
        sortBy: filters.sortBy,
        sortOrder: filters.sortOrder,
      };
      if (filters.status) {
        params.status = filters.status;
      }
      if (filters.search) {
        params.search = filters.search;
      }
      const response = await orderService.getAllOrders(params);
      if (response.ok && response.data) {
        const ordersData = Array.isArray(response.data.data) ? response.data.data : [];
        setOrders(ordersData);
        setPagination({
          currentPage: response.data.currentPage || 0,
          pageSize: response.data.pageSize || 10,
          totalElements: response.data.totalElements || 0,
          totalPages: response.data.totalPages || 0,
          hasNext: response.data.hasNext || false,
          hasPrevious: response.data.hasPrevious || false,
        });
      } else {
        setError('Failed to load orders');
      }
    } catch (err) {
      setError('Failed to load orders');
    } finally {
      setLoading(false);
    }
  };

  const handlePageChange = (newPage) => {
    setPagination(prev => ({ ...prev, currentPage: newPage }));
  };

  const handleSort = (field) => {
    setFilters(prev => ({
      ...prev,
      sortBy: field,
      sortOrder: prev.sortBy === field && prev.sortOrder === 'desc' ? 'asc' : 'desc',
    }));
    setPagination(prev => ({ ...prev, currentPage: 0 }));
  };

  const handleStatusFilter = (status) => {
    setFilters(prev => ({ ...prev, status }));
    setPagination(prev => ({ ...prev, currentPage: 0 }));
  };

  const handleSearchChange = (e) => {
    setFilters(prev => ({ ...prev, search: e.target.value }));
  };

  const handleSearchSubmit = (e) => {
    e.preventDefault();
    setPagination(prev => ({ ...prev, currentPage: 0 }));
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
      <div className="admin-order-history-page">
        <div className="loading-container">
          <div className="spinner"></div>
          <p>Loading orders...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="admin-order-history-page">
        <div className="error-state">
          <p>{error}</p>
        </div>
      </div>
    );
  }

  return (
    <div className="admin-order-history-page">
      <div className="admin-order-history-container">
        <div className="admin-order-history-header">
          <h1>Manage Orders</h1>
        </div>

        <div className="order-filters">
          <form className="search-form" onSubmit={handleSearchSubmit}>
            <input
              type="text"
              placeholder="Search by name, phone, or order ID..."
              value={filters.search}
              onChange={handleSearchChange}
              className="admin-order-search-input"
            />
            <button type="submit" className="search-btn">Search</button>
          </form>

          <div className="filter-group">
            <label>Status:</label>
            <select
              value={filters.status}
              onChange={(e) => handleStatusFilter(e.target.value)}
              className="filter-select"
            >
              <option value="">All</option>
              <option value="PENDING">Pending</option>
              <option value="CONFIRMED">Confirmed</option>
              <option value="SHIPPED">Shipped</option>
              <option value="DELIVERED">Delivered</option>
              <option value="CANCELLED">Cancelled</option>
            </select>
          </div>

          <div className="sort-buttons">
            <button
              className={`sort-btn ${filters.sortBy === 'createdAt' ? 'active' : ''}`}
              onClick={() => handleSort('createdAt')}
            >
              Date {filters.sortBy === 'createdAt' ? (filters.sortOrder === 'asc' ? '↑' : '↓') : ''}
            </button>
            <button
              className={`sort-btn ${filters.sortBy === 'total' ? 'active' : ''}`}
              onClick={() => handleSort('total')}
            >
              Total {filters.sortBy === 'total' ? (filters.sortOrder === 'asc' ? '↑' : '↓') : ''}
            </button>
          </div>
        </div>

        {orders.length === 0 ? (
          <div className="empty-state">
            <div className="empty-icon">📦</div>
            <h3>No orders found</h3>
            <p>There are no orders matching your criteria.</p>
          </div>
        ) : (
          <>
            <div className="orders-list">
              {orders.map(order => (
                <div key={order.id} className="order-card" onClick={() => navigate(`/admin/orders/${order.id}`)}>
                  <div className="order-header">
                    <div className="order-info">
                      <span className="order-id">Order #{order.id.slice(0, 8)}</span>
                      <span className="order-date">
                        {new Date(order.createdAt).toLocaleDateString()}
                      </span>
                      <span className="order-user">User: {order.userId?.slice(0, 8)}</span>
                    </div>
                    <span
                      className="order-status"
                      style={{ backgroundColor: getStatusColor(order.status) }}
                    >
                      {order.status}
                    </span>
                  </div>

                  <div className="order-shipping-info">
                    <span className="shipping-name">{order.name}</span>
                    <span className="shipping-phone">{order.phone}</span>
                  </div>

                  <div className="order-items-preview">
                    {order.items && order.items.slice(0, 3).map((item, index) => (
                      <div key={index} className="order-item-preview">
                        <span>{item.quantity}x {item.snapShot?.name || 'Product'}</span>
                      </div>
                    ))}
                    {order.items && order.items.length > 3 && (
                      <span className="more-items">+{order.items.length - 3} more</span>
                    )}
                  </div>

                  <div className="order-footer">
                    <span className="order-total">${order.total.toFixed(2)}</span>
                    <button className="btn-view-details">View Details →</button>
                  </div>
                </div>
              ))}
            </div>

            {pagination.totalPages > 1 && (
              <div className="pagination-controls">
                <button
                  className="pagination-btn"
                  disabled={pagination.currentPage === 0}
                  onClick={() => handlePageChange(pagination.currentPage - 1)}
                >
                  Previous
                </button>
                <span className="pagination-info">
                  Page {pagination.currentPage + 1} of {pagination.totalPages}
                </span>
                <button
                  className="pagination-btn"
                  disabled={pagination.currentPage >= pagination.totalPages - 1}
                  onClick={() => handlePageChange(pagination.currentPage + 1)}
                >
                  Next
                </button>
              </div>
            )}
          </>
        )}
      </div>
    </div>
  );
}

export default AdminOrderHistoryPage;
