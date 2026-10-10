// src/pages/auth/forgot/ForgotPasswordPage.jsx
// Step 1: enter the email → a reset link is mailed.
// Step 2: the link opens /forgot?email=…&token=… → choose a new password.
import { useEffect, useState } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import { authService } from '../../../services/authService';
import { Field, Notice, PasswordInput } from '../../../components/ui/Ui';
import '../../../components/ui/ui.css';
import './ForgotPasswordPage.css';

const EMAIL_PATTERN = /^\S+@\S+\.\S+$/;

function RequestLinkStep({ onSent }) {
  const [email, setEmail] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!EMAIL_PATTERN.test(email.trim())) {
      setError('Please enter a valid email address');
      return;
    }
    setLoading(true);
    try {
      const res = await authService.forgotPassword(email.trim());
      if (res.ok) onSent(email.trim());
      else setError(res.data?.message || 'Could not send the reset link');
    } catch {
      setError('Could not send the reset link. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <>
      <span className="forgot-icon"><i className="bi bi-key"></i></span>
      <h1>Forgot your password?</h1>
      <p className="forgot-lead">Enter the email of your account and we will send you a link to choose a new password.</p>
      <form className="ui-form" onSubmit={handleSubmit} noValidate>
        <Field label="Email" error={error}>
          <input
            type="email"
            value={email}
            onChange={(e) => { setEmail(e.target.value); setError(''); }}
            placeholder="you@example.com"
            autoComplete="email"
            autoFocus
          />
        </Field>
        <button type="submit" className="ui-btn ui-btn--primary ui-btn--lg ui-btn--block" disabled={loading}>
          {loading ? 'Sending…' : 'Send reset link'}
        </button>
      </form>
    </>
  );
}

function LinkSentStep({ email, onRetry }) {
  return (
    <>
      <span className="forgot-icon forgot-icon--success"><i className="bi bi-envelope-check"></i></span>
      <h1>Check your inbox</h1>
      <p className="forgot-lead">We sent a password reset link to <strong>{email}</strong>.</p>
      <Notice icon="bi-info-circle">
        <p>Not there after a few minutes? Check your spam folder, or send the link again.</p>
      </Notice>
      <button type="button" className="ui-btn ui-btn--ghost ui-btn--block forgot-retry" onClick={onRetry}>
        <i className="bi bi-arrow-repeat"></i> Send again
      </button>
    </>
  );
}

function NewPasswordStep({ email, token, onDone }) {
  const [form, setForm] = useState({ password: '', confirm: '' });
  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm(prev => ({ ...prev, [name]: value }));
    setErrors({});
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const next = {};
    if (form.password.length < 6) next.password = 'Use at least 6 characters';
    if (form.confirm !== form.password) next.confirm = 'Passwords do not match';
    setErrors(next);
    if (Object.keys(next).length) return;

    setLoading(true);
    try {
      const res = await authService.resetPassword(email, token, form.password);
      if (res.ok) onDone();
      else setErrors({ form: res.data?.message || 'This link is invalid or has expired' });
    } catch {
      setErrors({ form: 'Could not reset your password. Please try again.' });
    } finally {
      setLoading(false);
    }
  };

  return (
    <>
      <span className="forgot-icon"><i className="bi bi-shield-lock"></i></span>
      <h1>Choose a new password</h1>
      <p className="forgot-lead">For <strong>{email}</strong></p>
      <form className="ui-form" onSubmit={handleSubmit} noValidate>
        {errors.form && (
          <Notice tone="danger" icon="bi-exclamation-triangle">
            <p>{errors.form} <Link to="/forgot" className="ui-link">Request a new link</Link></p>
          </Notice>
        )}
        <Field label="New password" error={errors.password} hint="At least 6 characters.">
          <PasswordInput name="password" value={form.password} onChange={handleChange} autoComplete="new-password" autoFocus />
        </Field>
        <Field label="Confirm new password" error={errors.confirm}>
          <PasswordInput name="confirm" value={form.confirm} onChange={handleChange} autoComplete="new-password" />
        </Field>
        <button type="submit" className="ui-btn ui-btn--primary ui-btn--lg ui-btn--block" disabled={loading}>
          {loading ? 'Saving…' : 'Reset password'}
        </button>
      </form>
    </>
  );
}

function DoneStep() {
  return (
    <>
      <span className="forgot-icon forgot-icon--success"><i className="bi bi-check2-circle"></i></span>
      <h1>Password updated</h1>
      <p className="forgot-lead">You can now log in with your new password. You have been signed out on all devices.</p>
      <Link to="/auth?action=login" className="ui-btn ui-btn--primary ui-btn--lg ui-btn--block">Go to login</Link>
    </>
  );
}

export default function ForgotPasswordPage() {
  useEffect(() => {
    document.title = 'My Store - Forgot Password';
  }, []);

  const [searchParams, setSearchParams] = useSearchParams();
  const email = searchParams.get('email');
  const token = searchParams.get('token');
  const [sentTo, setSentTo] = useState(null);
  const [done, setDone] = useState(false);

  let step;
  if (done) step = <DoneStep />;
  else if (email && token) step = <NewPasswordStep email={email} token={token} onDone={() => { setDone(true); setSearchParams({}, { replace: true }); }} />;
  else if (sentTo) step = <LinkSentStep email={sentTo} onRetry={() => setSentTo(null)} />;
  else step = <RequestLinkStep onSent={setSentTo} />;

  return (
    <div className="ui-page forgot-page">
      <div className="ui-card forgot-card">
        {step}
        {!done && (
          <Link to="/auth?action=login" className="ui-back forgot-back">
            <i className="bi bi-arrow-left"></i> Back to login
          </Link>
        )}
      </div>
    </div>
  );
}
