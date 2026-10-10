import { useState } from 'react';
import { authService } from '../../../services/authService';
import { showSuccess, showError } from '../../../components/master/popup';
import { Card, Field } from '../../../components/ui/Ui';
import { formatDate } from '../../../components/ui/uiUtils';

const PHONE_PATTERN = /^\+?[0-9\s.-]{8,20}$/;

export default function PersonalInfoCard({ user, onSaved }) {
  const [editing, setEditing] = useState(false);
  const [form, setForm] = useState({ name: '', phone: '' });
  const [errors, setErrors] = useState({});
  const [saving, setSaving] = useState(false);

  const startEditing = () => {
    setForm({ name: user.name || '', phone: user.phone || '' });
    setErrors({});
    setEditing(true);
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm(prev => ({ ...prev, [name]: value }));
    if (errors[name]) setErrors(prev => ({ ...prev, [name]: '' }));
  };

  const validate = () => {
    const next = {};
    if (!form.name.trim()) next.name = 'Please enter your name';
    if (form.phone.trim() && !PHONE_PATTERN.test(form.phone.trim())) next.phone = 'Please enter a valid phone number';
    setErrors(next);
    return Object.keys(next).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validate()) return;

    setSaving(true);
    try {
      const fd = new FormData();
      fd.append('name', form.name.trim());
      fd.append('phone', form.phone.trim());
      const res = await authService.updateProfile(fd);
      if (res.ok) {
        await onSaved();
        showSuccess('Your information has been updated', 'Profile');
        setEditing(false);
      } else {
        showError(res.data?.message || 'Could not update your information', 'Profile');
      }
    } catch {
      showError('Could not update your information. Please try again.', 'Connection Error');
    } finally {
      setSaving(false);
    }
  };

  if (!editing) {
    return (
      <Card
        id="personal"
        icon="bi-person"
        title="Personal information"
        subtitle="Used for your orders and deliveries."
        actions={
          <button type="button" className="ui-btn ui-btn--ghost ui-btn--sm" onClick={startEditing}>
            <i className="bi bi-pencil"></i> Edit
          </button>
        }
      >
        <dl className="ui-kv">
          <div><dt>Full name</dt><dd>{user.name || <span className="ui-muted">Not provided</span>}</dd></div>
          <div><dt>Email</dt><dd>{user.email}</dd></div>
          <div><dt>Phone</dt><dd>{user.phone || <span className="ui-muted">Not provided</span>}</dd></div>
          <div><dt>Member since</dt><dd>{formatDate(user.createdAt)}</dd></div>
        </dl>
      </Card>
    );
  }

  return (
    <Card id="personal" icon="bi-person" title="Personal information" subtitle="Used for your orders and deliveries.">
      <form className="ui-form ui-form--grid" onSubmit={handleSubmit} noValidate>
        <Field label="Full name" required error={errors.name}>
          <input name="name" value={form.name} onChange={handleChange} autoComplete="name" maxLength={255} autoFocus />
        </Field>
        <Field label="Phone" error={errors.phone}>
          <input name="phone" type="tel" value={form.phone} onChange={handleChange} autoComplete="tel" maxLength={20} placeholder="e.g. 0901 234 567" />
        </Field>
        <Field label="Email" hint="Your email cannot be changed." wide>
          <input value={user.email} disabled />
        </Field>
        <div className="ui-form-actions ui-field--wide">
          <button type="button" className="ui-btn ui-btn--ghost" onClick={() => setEditing(false)} disabled={saving}>Cancel</button>
          <button type="submit" className="ui-btn ui-btn--primary" disabled={saving}>
            {saving ? 'Saving…' : 'Save changes'}
          </button>
        </div>
      </form>
    </Card>
  );
}
