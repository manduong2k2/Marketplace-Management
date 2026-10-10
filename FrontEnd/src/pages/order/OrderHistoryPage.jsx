import { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { orderService } from '../../services/orderService';
import { Page, PageHeader, Card, EmptyState, Pagination, Pill, SkeletonRows, Notice } from '../../components/ui/Ui';
import { ORDER_STATUSES, formatDate, formatMoney, orderNumber, orderStatus } from '../../components/ui/uiUtils';
import './OrderHistoryPage.css';

const PAGE_SIZE = 10;

const SORTS = {
  newest: { sortBy: 'createdAt', sortOrder: 'desc', label: 'Newest first' },
  oldest: { sortBy: 'createdAt', sortOrder: 'asc', label: 'Oldest first' },
  highest: { sortBy: 'total', sortOrder: 'desc', label: 'Highest total' },
  lowest: { sortBy: 'total', sortOrder: 'asc', label: 'Lowest total' },
};

export default function OrderHistoryPage() {
  useEffect(() => {
    document.title = 'My Store - Orders';
  }, []);

  const navigate = useNavigate();
  const [status, setStatus] = useState('');
  const [sort, setSort] = useState('newest');
  const [page, setPage] = useState(0);
  const [result, setResult] = useState({ orders: [], pagination: null });
  const [loading, setLoading] = useState(true);
  const [failed, setFailed] = useState(false);

  useEffect(() => {
    let cancelled = false;
    (async () => {
      setLoading(true);
      setFailed(false);
      try {
        const { sortBy, sortOrder } = SORTS[sort];
        const params = { page, size: PAGE_SIZE, sortBy, sortOrder, ...(status && { status }) };
        const res = await orderService.getMyOrders(params);
        if (cancelled) return;
        if (res.ok) {
          const { data = [], ...pagination } = res.data || {};
          setResult({ orders: data, pagination });
        } else {
          setFailed(true);
        }
      } catch {
        if (!cancelled) setFailed(true);
      } finally {
        if (!cancelled) setLoading(false);
      }
    })();
    return () => { cancelled = true; };
  }, [status, sort, page]);

  const changeStatus = (value) => { setStatus(value); setPage(0); };
  const changeSort = (value) => { setSort(value); setPage(0); };

  const { orders, pagination } = result;
  const filtered = Boolean(status);

  return (
    <Page>
      <PageHeader
        eyebrow="Account"
        eyebrowIcon="bi-receipt"
        title="My orders"
        description="Track your orders and see what you bought."
        actions={<Link to="/home" className="ui-btn ui-btn--ghost"><i className="bi bi-shop"></i> Continue shopping</Link>}
      />

      <div className="ui-toolbar">
        <div className="ui-segmented" role="tablist" aria-label="Filter by status">
          <button type="button" className={!status ? 'active' : ''} onClick={() => changeStatus('')}>All</button>
          {Object.entries(ORDER_STATUSES).map(([value, { label }]) => (
            <button key={value} type="button" className={status === value ? 'active' : ''} onClick={() => changeStatus(value)}>
              {label}
            </button>
          ))}
        </div>
        <select className="ui-select" value={sort} onChange={(e) => changeSort(e.target.value)} aria-label="Sort orders">
          {Object.entries(SORTS).map(([value, { label }]) => <option key={value} value={value}>{label}</option>)}
        </select>
      </div>

      <Card bodyless>
        {loading ? (
          <SkeletonRows rows={4} />
        ) : failed ? (
          <div className="ui-card-body">
            <Notice tone="danger" icon="bi-exclamation-triangle"><p>We could not load your orders. Please try again later.</p></Notice>
          </div>
        ) : orders.length === 0 ? (
          <EmptyState
            icon="bi-receipt"
            title={filtered ? `No ${orderStatus(status).label.toLowerCase()} orders` : 'No orders yet'}
            text={filtered ? 'Try another status filter.' : 'When you place an order, it will show up here.'}
          >
            {!filtered && <Link to="/home" className="ui-btn ui-btn--primary"><i className="bi bi-shop"></i> Start shopping</Link>}
          </EmptyState>
        ) : (
          <>
            <ul className="ui-list">
              {orders.map(order => {
                const s = orderStatus(order.status);
                return (
                  <li
                    key={order.id}
                    className="ui-row ui-row--link"
                    onClick={() => navigate(`/orders/${order.id}`)}
                    onKeyDown={(e) => e.key === 'Enter' && navigate(`/orders/${order.id}`)}
                    tabIndex={0}
                    role="link"
                  >
                    <span className={`order-row-icon order-row-icon--${s.tone}`}><i className={`bi ${s.icon}`}></i></span>
                    <div className="ui-row-main">
                      <p className="ui-row-title">
                        <span className="ui-mono">{orderNumber(order.id)}</span>
                        <Pill tone={s.tone}>{s.label}</Pill>
                      </p>
                      <p className="ui-row-sub order-row-address">
                        <i className="bi bi-calendar3"></i> {formatDate(order.createdAt, true)}
                        <span aria-hidden="true">·</span>
                        <i className="bi bi-geo-alt"></i> {order.address}
                      </p>
                    </div>
                    <div className="ui-row-end">
                      <span className="ui-price">{formatMoney(order.total)}</span>
                      <i className="bi bi-chevron-right ui-muted"></i>
                    </div>
                  </li>
                );
              })}
            </ul>
            <Pagination pagination={pagination} onChange={setPage} />
          </>
        )}
      </Card>
    </Page>
  );
}
