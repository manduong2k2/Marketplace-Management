import { useContext, useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { CartContext } from '../../contexts/CartContext';
import { AuthContext } from '../../contexts/AuthContext';
import { showSuccess, showError } from '../../components/master/popup';
import { orderService } from '../../services/orderService';
import { cartService } from '../../services/cartService';
import { addressService } from '../../services/addressService';
import { Page, PageHeader, Card, Field, EmptyState, Pill, SkeletonRows, Thumb } from '../../components/ui/Ui';
import { formatMoney } from '../../components/ui/uiUtils';
import './CartCheckoutPage.css';

const OTHER = 'other';
const PHONE_PATTERN = /^\+?[0-9\s.-]{8,20}$/;

export default function CartCheckoutPage() {
  useEffect(() => {
    document.title = 'My Store - Checkout';
  }, []);

  const navigate = useNavigate();
  const { cart, loading: cartLoading, setCart } = useContext(CartContext);
  const { user } = useContext(AuthContext);

  const [contact, setContact] = useState({ name: user?.name || '', phone: user?.phone || '' });
  const [note, setNote] = useState('');
  const [addresses, setAddresses] = useState([]);
  const [addressesLoading, setAddressesLoading] = useState(true);
  // id of the chosen saved address, or OTHER for a typed one
  const [selected, setSelected] = useState(OTHER);
  const [otherAddress, setOtherAddress] = useState('');
  const [errors, setErrors] = useState({});
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    (async () => {
      try {
        const res = await addressService.getMyAddresses();
        if (res.ok) {
          const list = res.data.data || [];
          setAddresses(list);
          // The list comes default-first
          if (list.length) setSelected(list[0].id);
        }
      } finally {
        setAddressesLoading(false);
      }
    })();
  }, []);

  const deliveryAddress = selected === OTHER
    ? otherAddress.trim()
    : addresses.find(a => a.id === selected)?.fullAddress || '';

  const handleContactChange = (e) => {
    const { name, value } = e.target;
    setContact(prev => ({ ...prev, [name]: value }));
    if (errors[name]) setErrors(prev => ({ ...prev, [name]: '' }));
  };

  const validate = () => {
    const next = {};
    if (!contact.name.trim()) next.name = 'Please enter the recipient name';
    if (!contact.phone.trim()) next.phone = 'Please enter a phone number';
    else if (!PHONE_PATTERN.test(contact.phone.trim())) next.phone = 'Please enter a valid phone number';
    if (!deliveryAddress) next.address = 'Please choose or enter a delivery address';
    setErrors(next);
    return Object.keys(next).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validate()) return;

    setSubmitting(true);
    try {
      const res = await orderService.create({
        name: contact.name.trim(),
        phone: contact.phone.trim(),
        address: deliveryAddress,
        note: note.trim(),
      });
      if (res.ok) {
        await cartService.clearCart();
        const cartRes = await cartService.getCart();
        setCart(cartRes.ok ? cartRes.data?.cart ?? null : null);
        showSuccess('Thank you! Your order has been placed.', 'Order placed');
        const orderId = res.data?.data?.id;
        navigate(orderId ? `/orders/${orderId}` : '/orders');
      } else {
        showError(res.data?.message || 'Could not place your order. Please try again.', 'Checkout');
      }
    } catch {
      showError('Could not place your order. Please try again.', 'Connection Error');
    } finally {
      setSubmitting(false);
    }
  };

  const items = cart?.items || [];

  if (!cartLoading && items.length === 0) {
    return (
      <Page>
        <PageHeader back={{ to: '/cart', label: 'Back to cart' }} eyebrow="Shopping" eyebrowIcon="bi-bag" title="Checkout" />
        <Card bodyless>
          <EmptyState icon="bi-bag" title="Your cart is empty" text="Add some products before checking out.">
            <Link to="/home" className="ui-btn ui-btn--primary"><i className="bi bi-shop"></i> Continue shopping</Link>
          </EmptyState>
        </Card>
      </Page>
    );
  }

  return (
    <Page>
      <PageHeader
        back={{ to: '/cart', label: 'Back to cart' }}
        eyebrow="Shopping"
        eyebrowIcon="bi-bag"
        title="Checkout"
        description="Check your delivery details, then place your order."
      />

      <form className="ui-split" onSubmit={handleSubmit} noValidate>
        <div className="ui-stack">
          <Card icon="bi-person" title="Recipient">
            <div className="ui-form ui-form--grid">
              <Field label="Full name" required error={errors.name}>
                <input name="name" value={contact.name} onChange={handleContactChange} autoComplete="name" />
              </Field>
              <Field label="Phone" required error={errors.phone}>
                <input name="phone" type="tel" value={contact.phone} onChange={handleContactChange} autoComplete="tel" placeholder="e.g. 0901 234 567" />
              </Field>
            </div>
          </Card>

          <Card
            icon="bi-geo-alt"
            title="Delivery address"
            actions={
              <Link to="/profile/addresses/new?returnTo=/checkout" className="ui-btn ui-btn--ghost ui-btn--sm">
                <i className="bi bi-plus-lg"></i> New address
              </Link>
            }
            bodyless
          >
            {addressesLoading ? (
              <SkeletonRows rows={2} />
            ) : (
              <div className="checkout-addresses" role="radiogroup" aria-label="Delivery address">
                {addresses.map(address => (
                  <label key={address.id} className={`checkout-address${selected === address.id ? ' is-selected' : ''}`}>
                    <input
                      type="radio"
                      name="address"
                      checked={selected === address.id}
                      onChange={() => { setSelected(address.id); setErrors(prev => ({ ...prev, address: '' })); }}
                    />
                    <span className="checkout-address-text">
                      <strong>
                        {address.detail}
                        {address.isDefault && <Pill tone="accent" icon="bi-star-fill">Default</Pill>}
                      </strong>
                      <span>{[address.ward?.fullName, address.province?.fullName].filter(Boolean).join(', ')}</span>
                    </span>
                  </label>
                ))}

                <label className={`checkout-address${selected === OTHER ? ' is-selected' : ''}`}>
                  <input
                    type="radio"
                    name="address"
                    checked={selected === OTHER}
                    onChange={() => setSelected(OTHER)}
                  />
                  <span className="checkout-address-text">
                    <strong>{addresses.length ? 'Deliver to another address' : 'Enter a delivery address'}</strong>
                    <span>Typed for this order only — not saved to your address book.</span>
                  </span>
                </label>

                {selected === OTHER && (
                  <div className="checkout-other">
                    <Field label="Address" required error={errors.address}>
                      <textarea
                        value={otherAddress}
                        onChange={(e) => { setOtherAddress(e.target.value); setErrors(prev => ({ ...prev, address: '' })); }}
                        placeholder="House number, street, ward, province / city"
                        rows={3}
                        autoComplete="street-address"
                      />
                    </Field>
                  </div>
                )}
                {selected !== OTHER && errors.address && <p className="ui-field-error checkout-other">{errors.address}</p>}
              </div>
            )}
          </Card>

          <Card icon="bi-chat-left-text" title="Order note" subtitle="Optional — delivery instructions, preferred time…">
            <Field>
              <textarea value={note} onChange={(e) => setNote(e.target.value)} rows={3} maxLength={500} placeholder="e.g. Please call before delivering" />
            </Field>
          </Card>
        </div>

        <aside className="ui-sticky">
          <Card icon="bi-receipt" title="Order summary" bodyless>
            {cartLoading ? (
              <SkeletonRows rows={2} thumb />
            ) : (
              <ul className="ui-list checkout-items">
                {items.map(item => (
                  <li key={item.id} className="ui-row">
                    <Thumb src={item.productImages?.[0]} small />
                    <div className="ui-row-main">
                      <p className="ui-row-title checkout-item-name">{item.productName}</p>
                      <p className="ui-row-sub">{formatMoney(item.productPrice)} × {item.quantity}</p>
                    </div>
                    <span className="ui-price">{formatMoney(item.subTotal)}</span>
                  </li>
                ))}
              </ul>
            )}
            <div className="ui-card-body checkout-totals">
              <dl className="ui-kv">
                <div><dt>Subtotal</dt><dd>{formatMoney(cart?.total)}</dd></div>
                <div><dt>Shipping</dt><dd className="ui-muted">Free</dd></div>
                <div className="ui-kv-total"><dt>Total</dt><dd>{formatMoney(cart?.total)}</dd></div>
              </dl>
              <button type="submit" className="ui-btn ui-btn--primary ui-btn--lg ui-btn--block checkout-submit" disabled={submitting || cartLoading}>
                <i className="bi bi-bag-check"></i> {submitting ? 'Placing order…' : `Place order · ${formatMoney(cart?.total)}`}
              </button>
            </div>
          </Card>
        </aside>
      </form>
    </Page>
  );
}
