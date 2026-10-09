// src/pages/admin/dashboard/AdminDashboardPage.jsx
import { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { orderService } from '../../../services/orderService';
import { productService } from '../../../services/productService';
import { brandService } from '../../../services/brandService';
import { categoryService } from '../../../services/categoryService';
import { vendorService } from '../../../services/vendorService';
import { PageHeader, Pill, EmptyState } from '../shared/AdminUi';
import { formatCurrency, formatDate } from '../shared/adminUiUtils';
import './AdminDashboardPage.css';

const QUICK_LINKS = [
  { label: 'Vendors', text: 'Manage marketplace vendors', icon: 'bi-shop', tone: 'warning', to: '/admin/vendors' },
  { label: 'Brands', text: 'Manage product brands', icon: 'bi-tags', tone: 'danger', to: '/admin/brands' },
  { label: 'Categories', text: 'Manage product categories', icon: 'bi-diagram-3', tone: 'info', to: '/admin/categories' },
  { label: 'Products', text: 'Browse & manage products', icon: 'bi-box-seam', tone: 'success', to: '/admin/products' },
  { label: 'Orders', text: 'Track customer orders', icon: 'bi-receipt', tone: 'accent', to: '/admin/orders' },
];

export default function AdminDashboardPage() {
  const navigate = useNavigate();
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
          setStats(s => ({ ...s, totalOrders: total }));
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
      } catch {
        // silently fail
      } finally {
        setLoading(false);
      }
    };

    fetchStats();
  }, []);

  const metrics = [
    { label: 'Total Orders', value: stats.totalOrders, icon: 'bi-receipt', tone: 'accent', link: '/admin/orders', hint: 'View all orders' },
    { label: 'Products', value: stats.totalProducts, icon: 'bi-box-seam', tone: 'success', link: '/admin/products', hint: 'Manage products' },
    { label: 'Vendors', value: stats.totalVendors, icon: 'bi-shop', tone: 'warning', link: '/admin/vendors', hint: 'Manage vendors' },
    { label: 'Brands', value: stats.totalBrands, icon: 'bi-tags', tone: 'danger', link: '/admin/brands', hint: 'Manage brands' },
    { label: 'Categories', value: stats.totalCategories, icon: 'bi-diagram-3', tone: 'info', link: '/admin/categories', hint: 'Manage categories' },
  ];

  return (
    <div className="admin-ui-page">
      <PageHeader
        eyebrow="Overview"
        eyebrowIcon="bi-speedometer2"
        title="Dashboard"
        description="Monitor orders, products, brands and categories from one place."
        actions={
          <>
            <Link className="admin-ui-btn admin-ui-btn--ghost" to="/admin/orders">
              <i className="bi bi-receipt" aria-hidden="true"></i> Orders
            </Link>
            <Link className="admin-ui-btn admin-ui-btn--primary" to="/admin/products">
              <i className="bi bi-plus-lg" aria-hidden="true"></i> Add Product
            </Link>
          </>
        }
      />

      {/* KPI stat cards */}
      <section className="admin-dashboard-stats" aria-label="Dashboard metrics">
        {metrics.map(m => (
          <article key={m.label} className={`admin-ui-card admin-dashboard-stat admin-dashboard-tone--${m.tone}`}>
            <div className="admin-dashboard-stat-top">
              <span className="admin-dashboard-stat-label">{m.label}</span>
              <span className="admin-dashboard-badge">
                <i className={`bi ${m.icon}`} aria-hidden="true"></i>
              </span>
            </div>
            <div className="admin-dashboard-stat-value">
              {loading ? <span className="admin-dashboard-stat-skeleton" aria-label="Loading"></span> : m.value.toLocaleString()}
            </div>
            <Link to={m.link} className="admin-dashboard-stat-link">
              {m.hint} <i className="bi bi-arrow-right" aria-hidden="true"></i>
            </Link>
          </article>
        ))}
      </section>

      {/* Recent orders */}
      <section className="admin-ui-card admin-dashboard-section">
        <div className="admin-dashboard-card-head">
          <div>
            <h2>
              <i className="bi bi-receipt" aria-hidden="true"></i>
              Recent Orders
            </h2>
            <p>Latest orders placed on the marketplace.</p>
          </div>
          <Link className="admin-ui-btn admin-ui-btn--ghost admin-ui-btn--sm" to="/admin/orders">
            View All
          </Link>
        </div>

        <div className="admin-ui-table-wrap">
          <table className="admin-ui-table">
            <thead>
              <tr>
                <th scope="col">Order #</th>
                <th scope="col">Customer</th>
                <th scope="col">Status</th>
                <th scope="col">Total</th>
                <th scope="col">Date</th>
                <th scope="col" className="admin-ui-col-actions"><span className="visually-hidden">Action</span></th>
              </tr>
            </thead>
            <tbody>
              {loading && [...Array(5)].map((_, i) => (
                <tr key={i} className="admin-ui-skeleton-row"><td colSpan={6}><span></span></td></tr>
              ))}
              {!loading && recentOrders.map(order => (
                <tr key={order.id} onClick={() => navigate(`/admin/orders/${order.id}`)}>
                  <td><strong className="admin-ui-mono">#{order.id}</strong></td>
                  <td>{order.name || '—'}</td>
                  <td>
                    <Pill tone={getStatusTone(order.status)}>{order.status || '—'}</Pill>
                  </td>
                  <td className="admin-dashboard-amount">
                    {formatCurrency(order.total)}
                  </td>
                  <td className="admin-ui-muted">
                    {formatDate(order.createdAt)}
                  </td>
                  <td className="admin-ui-col-actions" onClick={(e) => e.stopPropagation()}>
                    <Link className="admin-ui-btn admin-ui-btn--ghost admin-ui-btn--sm" to={`/admin/orders/${order.id}`}>
                      View
                    </Link>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
          {!loading && recentOrders.length === 0 && (
            <EmptyState icon="bi-receipt" title="No orders found" text="New orders will show up here." />
          )}
        </div>
      </section>

      {/* Quick navigation */}
      <section className="admin-dashboard-quick" aria-label="Quick navigation">
        {QUICK_LINKS.map(q => (
          <Link key={q.to} to={q.to} className={`admin-ui-card admin-dashboard-quick-card admin-dashboard-tone--${q.tone}`}>
            <span className="admin-dashboard-badge">
              <i className={`bi ${q.icon}`} aria-hidden="true"></i>
            </span>
            <span className="admin-dashboard-quick-copy">
              <strong>{q.label}</strong>
              <span>{q.text}</span>
            </span>
            <i className="bi bi-arrow-right admin-dashboard-quick-arrow" aria-hidden="true"></i>
          </Link>
        ))}
      </section>
    </div>
  );
}

function getStatusTone(status) {
  if (!status) return 'neutral';
  const s = status.toLowerCase();
  if (s === 'completed' || s === 'delivered') return 'success';
  if (s === 'pending') return 'warning';
  if (s === 'cancelled' || s === 'canceled') return 'danger';
  if (s === 'processing' || s === 'shipped') return 'info';
  return 'neutral';
}
