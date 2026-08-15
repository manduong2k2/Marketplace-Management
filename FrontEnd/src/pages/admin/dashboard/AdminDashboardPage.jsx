// src/pages/admin/dashboard/AdminDashboardPage.jsx
import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { orderService } from '../../../services/orderService';
import { productService } from '../../../services/productService';
import { brandService } from '../../../services/brandService';
import { categoryService } from '../../../services/categoryService';
import { vendorService } from '../../../services/vendorService';
import './AdminDashboardPage.css';

export default function AdminDashboardPage() {
  const [stats, setStats] = useState({
    totalOrders: 0,
    totalProducts: 0,
    totalBrands: 0,
    totalCategories: 0,
    totalVendors: 0,
  });
  const [recentOrders, setRecentOrders] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchStats = async () => {
      try {
        const [ordersRes, productsRes, brandsRes, categoriesRes, vendorsRes] = await Promise.allSettled([
          orderService.getAllOrders({ page: 0, size: 5 }),
          productService.getAll({ page: 0, size: 1 }),
          brandService.getAll(),
          categoryService.getAll(),
          vendorService.getAll(),
        ]);

        // Orders
        if (ordersRes.status === 'fulfilled' && ordersRes.value.ok) {
          const total = ordersRes.value?.data?.totalElements ?? 0;
          const d = ordersRes.value?.data?.data;
          setStats(s => ({ ...s, totalOrders: total}));
          setRecentOrders(d?.slice(0, 5) ?? []);
        }

        // Products
        if (productsRes.status === 'fulfilled' && productsRes.value.ok) {
          const total = productsRes.value?.data.pagination?.totalElements ?? 0;
          setStats(s => ({ ...s, totalProducts: total }));
        }

        // Brands
        if (brandsRes.status === 'fulfilled' && brandsRes.value.ok) {
          const d = brandsRes.value.data?.data;
          const count = Array.isArray(d) ? d.length : (d?.totalElements ?? d?.content?.length ?? 0);
          setStats(s => ({ ...s, totalBrands: count }));
        }

        // Categories
        if (categoriesRes.status === 'fulfilled' && categoriesRes.value.ok) {
          const d = categoriesRes.value.data?.data;
          const count = Array.isArray(d) ? d.length : (d?.totalElements ?? d?.content?.length ?? 0);
          setStats(s => ({ ...s, totalCategories: count }));
        }

        // Vendors
        if (vendorsRes.status === 'fulfilled' && vendorsRes.value.ok) {
          const d = vendorsRes.value.data?.data;
          const count = Array.isArray(d) ? d.length : (d?.totalElements ?? d?.content?.length ?? 0);
          setStats(s => ({ ...s, totalVendors: count }));
        }
      } catch (_) {
        // silently fail
      } finally {
        setLoading(false);
      }
    };

    fetchStats();
  }, []);

  const metrics = [
    {
      label: 'Total Orders',
      value: loading ? '—' : stats.totalOrders.toLocaleString(),
      icon: 'bi-receipt',
      variant: 'metric-primary',
      link: '/admin/orders',
      hint: 'View all orders',
    },
    {
      label: 'Products',
      value: loading ? '—' : stats.totalProducts.toLocaleString(),
      icon: 'bi-box-seam',
      variant: 'metric-success',
      link: '/admin/products',
      hint: 'Manage products',
    },
    {
      label: 'Vendors',
      value: loading ? '—' : stats.totalVendors.toLocaleString(),
      icon: 'bi-shop',
      variant: 'metric-warning',
      link: '/admin/vendors',
      hint: 'Manage vendors',
    },
    {
      label: 'Brands',
      value: loading ? '—' : stats.totalBrands.toLocaleString(),
      icon: 'bi-tags',
      variant: 'metric-danger',
      link: '/admin/brands',
      hint: 'Manage brands',
    },
    {
      label: 'Categories',
      value: loading ? '—' : stats.totalCategories.toLocaleString(),
      icon: 'bi-diagram-3',
      variant: 'metric-primary',
      link: '/admin/categories',
      hint: 'Manage categories',
    },
  ];

  return (
    <div className="container-fluid px-0">
      {/* Page heading */}
      <div className="page-heading">
        <div className="page-heading-copy">
          <span className="page-icon">
            <i className="bi bi-speedometer2" aria-hidden="true"></i>
          </span>
          <div>
            <p className="eyebrow mb-1">Overview</p>
            <h1 className="h3 mb-1">Dashboard</h1>
            <p className="text-muted mb-0">
              Monitor orders, products, brands and categories from one place.
            </p>
          </div>
        </div>
        <div className="heading-actions">
          <Link className="btn btn-outline-secondary btn-sm" to="/admin/orders">
            <i className="bi bi-receipt" aria-hidden="true"></i> Orders
          </Link>
          <Link className="btn btn-primary btn-sm" to="/admin/products">
            <i className="bi bi-plus-lg" aria-hidden="true"></i> Add Product
          </Link>
        </div>
      </div>

      {/* Metric cards */}
      <section className="row g-3 mt-1" aria-label="Dashboard metrics">
        {metrics.map(m => (
          <div key={m.label} className="col-12 col-sm-6 col-xl-3">
            <article className={`metric-card ${m.variant}`}>
              <div className="metric-top">
                <span className="metric-label">{m.label}</span>
                <span className="metric-icon">
                  <i className={`bi ${m.icon}`} aria-hidden="true"></i>
                </span>
              </div>
              <div className="metric-value">{m.value}</div>
              <div className="metric-meta">
                <Link to={m.link} className="text-muted small">{m.hint} →</Link>
              </div>
            </article>
          </div>
        ))}
      </section>

      {/* Recent orders */}
      <section className="panel mt-3">
        <div className="panel-header">
          <div>
            <h2 className="h5 mb-1 section-title">
              <i className="bi bi-receipt" aria-hidden="true"></i>
              <span>Recent Orders</span>
            </h2>
            <p className="text-muted mb-0">Latest orders placed on the marketplace.</p>
          </div>
          <Link className="btn btn-outline-secondary btn-sm" to="/admin/orders">
            View All
          </Link>
        </div>

        {loading ? (
          <div className="dashboard-empty">
            <span className="spinner-border spinner-border-sm text-primary me-2" role="status"></span>
            Loading orders…
          </div>
        ) : recentOrders.length === 0 ? (
          <div className="dashboard-empty text-muted">No orders found.</div>
        ) : (
          <div className="table-responsive">
            <table className="table align-middle mb-0">
              <thead>
                <tr>
                  <th scope="col">Order #</th>
                  <th scope="col">Customer</th>
                  <th scope="col">Status</th>
                  <th scope="col">Total</th>
                  <th scope="col">Date</th>
                  <th scope="col" className="text-end">Action</th>
                </tr>
              </thead>
              <tbody>
                {recentOrders.map(order => (
                  <tr key={order.id}>
                    <td className="fw-semibold">#{order.id}</td>
                    <td>{order.name}</td>
                    <td>
                      <span className={`badge ${getStatusBadge(order.status)}`}>
                        {order.status || '—'}
                      </span>
                    </td>
                    <td>
                      {order.total != null
                        ? `$${Number(order.total).toLocaleString('vi-VN')}`
                        : '—'}
                    </td>
                    <td className="text-muted small">
                      {order.createdAt ? new Date(order.createdAt).toLocaleDateString('vi-VN') : '—'}
                    </td>
                    <td className="text-end">
                      <Link className="btn btn-light btn-sm" to={`/admin/orders/${order.id}`}>
                        View
                      </Link>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </section>

      {/* Quick navigation */}
      <section className="row g-3 mt-1">
        <div className="col-12 col-md-6 col-xl-2">
          <Link to="/admin/vendors" className="quick-nav-card panel d-flex align-items-center gap-3 text-decoration-none">
            <span className="page-icon flex-shrink-0">
              <i className="bi bi-shop" aria-hidden="true"></i>
            </span>
            <div>
              <div className="fw-bold" style={{ color: 'var(--admin-text)' }}>Vendors</div>
              <div className="text-muted small">Manage marketplace vendors</div>
            </div>
            <i className="bi bi-arrow-right ms-auto text-muted"></i>
          </Link>
        </div>
        <div className="col-12 col-md-6 col-xl-2">
          <Link to="/admin/brands" className="quick-nav-card panel d-flex align-items-center gap-3 text-decoration-none">
            <span className="page-icon flex-shrink-0">
              <i className="bi bi-tags" aria-hidden="true"></i>
            </span>
            <div>
              <div className="fw-bold" style={{ color: 'var(--admin-text)' }}>Brands</div>
              <div className="text-muted small">Manage product brands</div>
            </div>
            <i className="bi bi-arrow-right ms-auto text-muted"></i>
          </Link>
        </div>
        <div className="col-12 col-md-6 col-xl-2">
          <Link to="/admin/categories" className="quick-nav-card panel d-flex align-items-center gap-3 text-decoration-none">
            <span className="page-icon flex-shrink-0">
              <i className="bi bi-diagram-3" aria-hidden="true"></i>
            </span>
            <div>
              <div className="fw-bold" style={{ color: 'var(--admin-text)' }}>Categories</div>
              <div className="text-muted small">Manage product categories</div>
            </div>
            <i className="bi bi-arrow-right ms-auto text-muted"></i>
          </Link>
        </div>
        <div className="col-12 col-md-6 col-xl-2">
          <Link to="/admin/products" className="quick-nav-card panel d-flex align-items-center gap-3 text-decoration-none">
            <span className="page-icon flex-shrink-0">
              <i className="bi bi-box-seam" aria-hidden="true"></i>
            </span>
            <div>
              <div className="fw-bold" style={{ color: 'var(--admin-text)' }}>Products</div>
              <div className="text-muted small">Browse & manage products</div>
            </div>
            <i className="bi bi-arrow-right ms-auto text-muted"></i>
          </Link>
        </div>
        <div className="col-12 col-md-6 col-xl-2">
          <Link to="/admin/orders" className="quick-nav-card panel d-flex align-items-center gap-3 text-decoration-none">
            <span className="page-icon flex-shrink-0">
              <i className="bi bi-receipt" aria-hidden="true"></i>
            </span>
            <div>
              <div className="fw-bold" style={{ color: 'var(--admin-text)' }}>Orders</div>
              <div className="text-muted small">Track customer orders</div>
            </div>
            <i className="bi bi-arrow-right ms-auto text-muted"></i>
          </Link>
        </div>
      </section>
    </div>
  );
}

function getStatusBadge(status) {
  if (!status) return 'text-bg-secondary';
  const s = status.toLowerCase();
  if (s === 'completed' || s === 'delivered') return 'text-bg-success';
  if (s === 'pending') return 'text-bg-warning';
  if (s === 'cancelled' || s === 'canceled') return 'text-bg-danger';
  if (s === 'processing' || s === 'shipped') return 'text-bg-info';
  return 'text-bg-secondary';
}
