// src/pages/auth/address/AddressFormPage.jsx
// Add / edit an address: province → ward → detail → default. Routes:
//   /profile/addresses/new            (?returnTo=/checkout to come back after saving)
//   /profile/addresses/:id/edit
import { useEffect, useMemo, useState } from 'react';
import { useNavigate, useParams, useSearchParams } from 'react-router-dom';
import { addressService } from '../../../services/addressService';
import { showSuccess, showError } from '../../../components/master/popup';
import { Page, PageHeader, Card, Field, ComboBox, Switch, Notice } from '../../../components/ui/Ui';
import './AddressFormPage.css';

const toOptions = (regions) => regions.map(r => ({ value: r.id, label: r.fullName || r.name }));

// Only same-origin paths are accepted as a return target (no open redirect)
const safeReturnTo = (value) => (value && value.startsWith('/') && !value.startsWith('//') ? value : '/profile#addresses');

export default function AddressFormPage() {
  const { id } = useParams();
  const isEdit = Boolean(id);
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const returnTo = safeReturnTo(searchParams.get('returnTo'));

  const [provinces, setProvinces] = useState([]);
  const [wards, setWards] = useState([]);
  const [wardsLoading, setWardsLoading] = useState(false);

  const [provinceId, setProvinceId] = useState('');
  const [wardId, setWardId] = useState('');
  const [detail, setDetail] = useState('');
  const [isDefault, setIsDefault] = useState(false);
  // The default address stays the default until another one replaces it
  const [wasDefault, setWasDefault] = useState(false);
  const [isFirstAddress, setIsFirstAddress] = useState(false);

  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    document.title = `My Store - ${isEdit ? 'Edit' : 'Add'} address`;
  }, [isEdit]);

  // Provinces + (edit) the address itself, or (add) whether this is the first address
  useEffect(() => {
    let cancelled = false;
    (async () => {
      try {
        const [provinceRes, addressRes] = await Promise.all([
          addressService.getProvinces(),
          isEdit ? addressService.getAddressById(id) : addressService.getMyAddresses(),
        ]);
        if (cancelled) return;
        if (provinceRes.ok) setProvinces(toOptions(provinceRes.data.data || []));

        if (isEdit) {
          if (!addressRes.ok) {
            navigate(returnTo, { replace: true });
            return;
          }
          const address = addressRes.data.data;
          setProvinceId(address.province?.id || '');
          setWardId(address.ward?.id || '');
          setDetail(address.detail || '');
          setIsDefault(Boolean(address.isDefault));
          setWasDefault(Boolean(address.isDefault));
        } else if (addressRes.ok && (addressRes.data.data || []).length === 0) {
          setIsFirstAddress(true);
          setIsDefault(true);
        }
      } catch {
        if (!cancelled) showError('Could not load the address form. Please try again.', 'Connection Error');
      } finally {
        if (!cancelled) setLoading(false);
      }
    })();
    return () => { cancelled = true; };
  }, [id, isEdit, navigate, returnTo]);

  // Wards of the selected province
  useEffect(() => {
    if (!provinceId) return undefined;
    let cancelled = false;
    (async () => {
      setWardsLoading(true);
      try {
        const res = await addressService.getWards(provinceId);
        if (!cancelled && res.ok) setWards(toOptions(res.data.data || []));
      } finally {
        if (!cancelled) setWardsLoading(false);
      }
    })();
    return () => { cancelled = true; };
  }, [provinceId]);

  const handleProvinceChange = (value) => {
    if (value === provinceId) return;
    setProvinceId(value);
    setWards([]);
    setWardId('');
    setErrors(prev => ({ ...prev, provinceId: '', wardId: '' }));
  };

  const handleWardChange = (value) => {
    setWardId(value);
    setErrors(prev => ({ ...prev, wardId: '' }));
  };

  const preview = useMemo(() => {
    const ward = wards.find(w => w.value === wardId)?.label;
    const province = provinces.find(p => p.value === provinceId)?.label;
    return [detail.trim(), ward, province].filter(Boolean).join(', ');
  }, [detail, wardId, wards, provinceId, provinces]);

  const validate = () => {
    const next = {};
    if (!provinceId) next.provinceId = 'Please choose a province / city';
    if (!wardId) next.wardId = 'Please choose a ward';
    if (!detail.trim()) next.detail = 'Please enter the house number and street';
    setErrors(next);
    return Object.keys(next).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validate()) return;

    setSaving(true);
    try {
      const body = { wardId, detail: detail.trim(), isDefault };
      const res = isEdit ? await addressService.updateAddress(id, body) : await addressService.createAddress(body);
      if (res.ok) {
        showSuccess(isEdit ? 'Address updated' : 'Address added', 'Addresses');
        navigate(returnTo);
      }
    } catch {
      showError('Could not save the address. Please try again.', 'Connection Error');
    } finally {
      setSaving(false);
    }
  };

  const lockedDefault = wasDefault || isFirstAddress;

  return (
    <Page narrow>
      <PageHeader
        back={{ to: returnTo, label: returnTo.startsWith('/checkout') ? 'Back to checkout' : 'Back to profile' }}
        eyebrow="Delivery addresses"
        eyebrowIcon="bi-geo-alt"
        title={isEdit ? 'Edit address' : 'Add a new address'}
        description="Choose the province / city and the ward, then enter the house number and street."
      />

      <Card bodyless>
        <form onSubmit={handleSubmit} noValidate>
          <div className="ui-card-body ui-form">
            <ol className="address-steps">
              <li className="address-step">
                <span className="address-step-index">1</span>
                <Field as="div" label="Province / City" required error={errors.provinceId}>
                  <ComboBox
                    options={provinces}
                    value={provinceId}
                    onChange={handleProvinceChange}
                    placeholder="Choose a province / city"
                    searchPlaceholder="Search provinces…"
                    loading={loading}
                    disabled={loading}
                  />
                </Field>
              </li>

              <li className="address-step">
                <span className="address-step-index">2</span>
                <Field
                  as="div"
                  label="Ward"
                  required
                  error={errors.wardId}
                  hint={!provinceId ? 'Choose a province / city first.' : undefined}
                >
                  <ComboBox
                    options={wards}
                    value={wardId}
                    onChange={handleWardChange}
                    placeholder={provinceId ? 'Choose a ward' : '—'}
                    searchPlaceholder="Search wards…"
                    loading={wardsLoading}
                    disabled={!provinceId || wardsLoading}
                  />
                </Field>
              </li>

              <li className="address-step">
                <span className="address-step-index">3</span>
                <Field label="Address detail" required error={errors.detail} hint="House number, street, building, floor…">
                  <input
                    value={detail}
                    onChange={(e) => { setDetail(e.target.value); if (errors.detail) setErrors(prev => ({ ...prev, detail: '' })); }}
                    placeholder="e.g. 12 Nguyễn Trãi, Floor 3"
                    maxLength={255}
                    autoComplete="street-address"
                  />
                </Field>
              </li>

              <li className="address-step">
                <span className="address-step-index">4</span>
                <div className="address-step-content">
                  <Switch
                    checked={isDefault}
                    onChange={setIsDefault}
                    disabled={lockedDefault}
                    title="Set as default address"
                    description={
                      isFirstAddress
                        ? 'Your first address is always the default.'
                        : wasDefault
                          ? 'This is your default address. To change it, set another address as default.'
                          : 'Pre-selected at checkout.'
                    }
                  />
                </div>
              </li>
            </ol>

            {preview && (
              <Notice icon="bi-pin-map">
                <p><strong>Delivered to:</strong> {preview}</p>
              </Notice>
            )}
          </div>

          <div className="ui-card-footer">
            <button type="button" className="ui-btn ui-btn--ghost" onClick={() => navigate(returnTo)} disabled={saving}>
              Cancel
            </button>
            <button type="submit" className="ui-btn ui-btn--primary" disabled={saving || loading}>
              <i className="bi bi-check2"></i> {saving ? 'Saving…' : isEdit ? 'Save changes' : 'Save address'}
            </button>
          </div>
        </form>
      </Card>
    </Page>
  );
}
