import { useState } from 'react';
import { Link } from 'react-router-dom';
import { authService } from '../../../services/authService';
import { showSuccess, showError } from '../../../components/master/popup';
import { Card, Field, Notice, PasswordInput } from '../../../components/ui/Ui';

const EMPTY = { currentPassword: '', newPassword: '', confirmPassword: '' };

export default function ChangePasswordCard() {
  const [form, setForm] = useState(EMPTY);
  const [errors, setErrors] = useState({});
  const [saving, setSaving] = useState(false);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm(prev => ({ ...prev, [name]: value }));
    if (errors[name]) setErrors(prev => ({ ...prev, [name]: '' }));
  };

  const validate = () => {
    const next = {};
    if (!form.currentPassword) next.currentPassword = 'Please enter your current password';
    if (form.newPassword.length < 6) next.newPassword = 'Use at least 6 characters';
    else if (form.newPassword === form.currentPassword) next.newPassword = 'Choose a password different from the current one';
    if (form.confirmPassword !== form.newPassword) next.confirmPassword = 'Passwords do not match';
    setErrors(next);
    return Object.keys(next).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validate()) return;

    setSaving(true);
    try {
      const res = await authService.changePassword(form.currentPassword, form.newPassword);
      if (res.ok) {
        setForm(EMPTY);
        showSuccess('Your password has been changed. Other devices have been signed out.', 'Password');
      } else if (res.status === 400) {
        // Wrong current password / same password: show it on the matching field
        const message = res.data?.message || 'Could not change your password';
        if (/current/i.test(message)) setErrors({ currentPassword: message });
        else setErrors({ newPassword: message });
      } else {
        showError(res.data?.message || 'Could not change your password', 'Password');
      }
    } catch {
      showError('Could not change your password. Please try again.', 'Connection Error');
    } finally {
      setSaving(false);
    }
  };

  return (
    <Card id="security" icon="bi-shield-lock" title="Password & security" subtitle="Changing your password signs you out on every other device.">
      <form className="ui-form" onSubmit={handleSubmit} noValidate>
        <Field label="Current password" required error={errors.currentPassword}>
          <PasswordInput name="currentPassword" value={form.currentPassword} onChange={handleChange} autoComplete="current-password" />
        </Field>
        <div className="ui-form ui-form--grid">
          <Field label="New password" required error={errors.newPassword}>
            <PasswordInput name="newPassword" value={form.newPassword} onChange={handleChange} autoComplete="new-password" />
          </Field>
          <Field label="Confirm new password" required error={errors.confirmPassword}>
            <PasswordInput name="confirmPassword" value={form.confirmPassword} onChange={handleChange} autoComplete="new-password" />
          </Field>
        </div>

        <Notice icon="bi-google">
          <p>
            Signed up with Google or Facebook? Your account has no password you know yet —{' '}
            <Link to="/forgot" className="ui-link">set one with “Forgot password”</Link>.
          </p>
        </Notice>

        <div className="ui-form-actions">
          <button type="submit" className="ui-btn ui-btn--primary" disabled={saving}>
            <i className="bi bi-key"></i> {saving ? 'Updating…' : 'Update password'}
          </button>
        </div>
      </form>
    </Card>
  );
}
