import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { orderService } from '../../services/orderService';
import { Page, PageHeader, Card, EmptyState, Pill, SkeletonRows, Thumb } from '../../components/ui/Ui';
import { formatDate, formatMoney, orderNumber, orderStatus } from '../../components/ui/uiUtils';
import './OrderHistoryPage.css';
import './OrderDetailPage.css';

export default function OrderDetailPage() {
  const { id } = useParams();
  const [order, setOrder] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    document.title = 'My Store - Order details';
    let cancelled = false;
    (async () => {
      try {
        const res = await orderService.getOrderById(id);
        if (!cancelled) setOrder(res.ok ? res.data : null);
      } catch {
        if (!cancelled) setOrder(null);
      } finally {
        if (!cancelled) setLoading(false);
      }
    })();
    return () => { cancelled = true; };
  }, [id]);

  const back = { to: '/orders', label: 'Back to orders' };

  if (loading) {
    return (
      <Page>
        <PageHeader back={back} eyebrow="Order" eyebrowIcon="bi-receipt" title="Order details" />
        <Card bodyless><SkeletonRows rows={3} thumb /></Card>
      </Page>
    );
  }

  if (!order) {
    return (
      <Page>
        <PageHeader back={back} eyebrow="Order" eyebrowIcon="bi-receipt" title="Order details" />
        <Card bodyless>
          <EmptyState icon="bi-search" title="Order not found" text="This order does not exist or does not belong to your account.">
            <Link to="/orders" className="ui-btn ui-btn--primary">See my orders</Link>
          </EmptyState>
        </Card>
      </Page>
    );
  }

  const status = orderStatus(order.status);
  const items = order.items || [];
  const unitCount = items.reduce((sum, item) => sum + item.quantity, 0);

  return (
    <Page>
      <PageHeader
        back={back}
        eyebrow="Order"
        eyebrowIcon="bi-receipt"
        title={<>Order <span className="ui-mono">{orderNumber(order.id)}</span></>}
        description={`Placed on ${formatDate(order.createdAt, true)}`}
        actions={<Pill tone={status.tone} icon={status.icon}>{status.label}</Pill>}
      />

      <div className="ui-split">
        <Card icon="bi-box-seam" title="Items" subtitle={`${unitCount} item${unitCount > 1 ? 's' : ''}`} bodyless>
          <ul className="ui-list">
            {items.map(item => (
              <li key={item.id} className="ui-row">
                <Thumb src={item.productImages?.[0]} />
                <div className="ui-row-main">
                  <p className="ui-row-title">{item.productName || 'Product'}</p>
                  <p className="ui-row-sub">
                    {formatMoney(item.price)} × {item.quantity}
                    {item.productSku && <> · <span className="ui-mono">{item.productSku}</span></>}
                  </p>
                </div>
                <span className="ui-price">{formatMoney(item.total)}</span>
              </li>
            ))}
          </ul>
        </Card>

        <aside className="ui-stack ui-sticky">
          <Card icon="bi-receipt" title="Payment summary">
            <dl className="ui-kv">
              <div><dt>Subtotal</dt><dd>{formatMoney(order.total)}</dd></div>
              <div><dt>Shipping</dt><dd className="ui-muted">Free</dd></div>
              <div className="ui-kv-total"><dt>Total</dt><dd>{formatMoney(order.total)}</dd></div>
            </dl>
          </Card>

          <Card icon="bi-truck" title="Delivery">
            <dl className="ui-kv ui-kv--stacked">
              <div><dt>Recipient</dt><dd>{order.name}</dd></div>
              <div><dt>Phone</dt><dd>{order.phone}</dd></div>
              <div><dt>Address</dt><dd>{order.address}</dd></div>
              {order.note && <div><dt>Note</dt><dd className="order-note">{order.note}</dd></div>}
            </dl>
          </Card>

          <Card icon="bi-clock-history" title="Timeline">
            <dl className="ui-kv">
              <div><dt>Placed</dt><dd>{formatDate(order.createdAt, true)}</dd></div>
              <div><dt>Last update</dt><dd>{formatDate(order.updatedAt, true)}</dd></div>
            </dl>
          </Card>
        </aside>
      </div>
    </Page>
  );
}
