import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { addressService } from '../../../services/addressService';
import { showSuccess, showError } from '../../../components/master/popup';
import { Card, ConfirmDialog, EmptyState, Pill, SkeletonRows } from '../../../components/ui/Ui';

/** The user's addresses: default first; set default, edit, delete. Add/edit happen on the address form page. */
export default function AddressBookCard() {
  const [addresses, setAddresses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [busyId, setBusyId] = useState(null);
  const [toDelete, setToDelete] = useState(null);

  const load = async () => {
    try {
      const res = await addressService.getMyAddresses();
      if (res.ok) setAddresses(res.data.data || []);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load();
  }, []);

  const setDefault = async (address) => {
    setBusyId(address.id);
    try {
      const res = await addressService.setDefaultAddress(address.id);
      if (res.ok) {
        await load();
        showSuccess('Default address updated', 'Addresses');
      }
    } catch {
      showError('Could not update the default address', 'Connection Error');
    } finally {
      setBusyId(null);
    }
  };

  const confirmDelete = async () => {
    setBusyId(toDelete.id);
    try {
      const res = await addressService.deleteAddress(toDelete.id);
      if (res.ok) {
        await load();
        showSuccess('Address deleted', 'Addresses');
      }
    } catch {
      showError('Could not delete the address', 'Connection Error');
    } finally {
      setBusyId(null);
      setToDelete(null);
    }
  };

  const addButton = (
    <Link to="/profile/addresses/new" className="ui-btn ui-btn--primary ui-btn--sm">
      <i className="bi bi-plus-lg"></i> Add address
    </Link>
  );

  return (
    <Card
      id="addresses"
      icon="bi-geo-alt"
      title="Delivery addresses"
      subtitle="Your default address is pre-selected at checkout."
      actions={addresses.length > 0 && addButton}
      bodyless
    >
      {loading ? (
        <SkeletonRows rows={2} />
      ) : addresses.length === 0 ? (
        <EmptyState icon="bi-geo-alt" title="No address yet" text="Add a delivery address to check out faster.">
          {addButton}
        </EmptyState>
      ) : (
        <ul className="ui-list">
          {addresses.map(address => (
            <li key={address.id} className={`ui-row${address.isDefault ? ' ui-row--active' : ''}`}>
              <span className="profile-address-icon"><i className={`bi ${address.isDefault ? 'bi-house-check' : 'bi-geo-alt'}`}></i></span>
              <div className="ui-row-main">
                <p className="ui-row-title">
                  {address.detail}
                  {address.isDefault && <Pill tone="accent" icon="bi-star-fill">Default</Pill>}
                </p>
                <p className="ui-row-sub">
                  {[address.ward?.fullName, address.province?.fullName].filter(Boolean).join(', ')}
                </p>
              </div>
              <div className="ui-row-end">
                {!address.isDefault && (
                  <button
                    type="button"
                    className="ui-btn ui-btn--ghost ui-btn--sm"
                    onClick={() => setDefault(address)}
                    disabled={busyId === address.id}
                  >
                    Set as default
                  </button>
                )}
                <Link to={`/profile/addresses/${address.id}/edit`} className="ui-icon-btn" title="Edit" aria-label="Edit address">
                  <i className="bi bi-pencil"></i>
                </Link>
                <button
                  type="button"
                  className="ui-icon-btn ui-icon-btn--danger"
                  title="Delete"
                  aria-label="Delete address"
                  onClick={() => setToDelete(address)}
                  disabled={busyId === address.id}
                >
                  <i className="bi bi-trash"></i>
                </button>
              </div>
            </li>
          ))}
        </ul>
      )}

      {toDelete && (
        <ConfirmDialog
          title="Delete this address?"
          message={
            toDelete.isDefault && addresses.length > 1
              ? `"${toDelete.fullAddress}" is your default address. Your most recent other address will become the default.`
              : `"${toDelete.fullAddress}" will be removed from your address book.`
          }
          busy={busyId === toDelete.id}
          onConfirm={confirmDelete}
          onClose={() => setToDelete(null)}
        />
      )}
    </Card>
  );
}
