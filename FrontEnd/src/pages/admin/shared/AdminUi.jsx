// Shared UI building blocks for admin pages. Styles: ./admin-index.css (admin-ui-*)
import { useEffect } from 'react';
import { createPortal } from 'react-dom';

// Deterministic hue per user so avatars keep the same color
const hueOf = (text = '') => [...text].reduce((h, c) => (h * 31 + c.charCodeAt(0)) % 360, 7);

/**
 * Renders overlays (modal, drawer) on document.body. The admin page container creates its own
 * stacking context (z-index: 0), so an overlay rendered inside it would stay under the sticky navbar.
 * The wrapper re-applies the admin-ui tokens outside .admin-shell.
 */
export function Layer({ children }) {
  return createPortal(<div className="admin-ui-layer">{children}</div>, document.body);
}

/** Page title block: eyebrow (section), title, description and actions on the right. */
export function PageHeader({ eyebrow, eyebrowIcon, title, description, actions }) {
  return (
    <header className="admin-ui-header">
      <div>
        {eyebrow && (
          <span className="admin-ui-eyebrow">
            {eyebrowIcon && <i className={`bi ${eyebrowIcon}`}></i>} {eyebrow}
          </span>
        )}
        <h1>{title}</h1>
        {description && <p>{description}</p>}
      </div>
      {actions && <div className="admin-ui-header-actions">{actions}</div>}
    </header>
  );
}

export function SearchBox({ value, onChange, placeholder = 'Search…', compact, autoFocus }) {
  return (
    <label className={`admin-ui-search${compact ? ' admin-ui-search--compact' : ''}`}>
      <i className="bi bi-search"></i>
      <input type="search" placeholder={placeholder} value={value} onChange={(e) => onChange(e.target.value)} autoFocus={autoFocus} />
    </label>
  );
}

export function Avatar({ user, size = 36 }) {
  const label = (user?.name || user?.email || '?').trim();
  const initials = label.split(/\s+/).slice(0, 2).map(w => w[0]).join('').toUpperCase();
  const hue = hueOf(user?.email || label);

  if (user?.avatar) {
    return <img className="admin-ui-avatar" src={user.avatar} alt="" style={{ width: size, height: size }} referrerPolicy="no-referrer" />;
  }
  return (
    <span
      className="admin-ui-avatar admin-ui-avatar--initials"
      style={{ width: size, height: size, fontSize: size * 0.38, '--hue': hue }}
      aria-hidden="true"
    >
      {initials}
    </span>
  );
}

/** Status pill. tone: success | warning | danger | info | neutral */
export function Pill({ tone = 'neutral', dot = true, children }) {
  return (
    <span className={`admin-ui-status admin-ui-status--${tone}`}>
      {dot && <span className="admin-ui-status-dot"></span>}
      {children}
    </span>
  );
}

export function Pagination({ pagination, onChange }) {
  if (!pagination || pagination.totalPages <= 1) return null;
  const { currentPage, totalPages, totalElements, pageSize } = pagination;
  const from = currentPage * pageSize + 1;
  const to = Math.min(totalElements, (currentPage + 1) * pageSize);

  return (
    <div className="admin-ui-pagination">
      <span>{from}–{to} of {totalElements}</span>
      <div className="admin-ui-pagination-buttons">
        <button type="button" disabled={!pagination.hasPrevious} onClick={() => onChange(currentPage - 1)} aria-label="Previous page">
          <i className="bi bi-chevron-left"></i>
        </button>
        <span className="admin-ui-pagination-page">{currentPage + 1} / {totalPages}</span>
        <button type="button" disabled={!pagination.hasNext} onClick={() => onChange(currentPage + 1)} aria-label="Next page">
          <i className="bi bi-chevron-right"></i>
        </button>
      </div>
    </div>
  );
}

export function EmptyState({ icon, title, text, children }) {
  return (
    <div className="admin-ui-empty">
      <i className={`bi ${icon}`}></i>
      <strong>{title}</strong>
      {text && <span>{text}</span>}
      {children}
    </div>
  );
}

export function Modal({ title, subtitle, onClose, children, footer, width = 520 }) {
  useEffect(() => {
    const onKey = (e) => e.key === 'Escape' && onClose();
    window.addEventListener('keydown', onKey);
    return () => window.removeEventListener('keydown', onKey);
  }, [onClose]);

  return (
    <Layer>
      <div className="admin-ui-overlay" onMouseDown={(e) => e.target === e.currentTarget && onClose()}>
        <div className="admin-ui-modal" style={{ maxWidth: width }} role="dialog" aria-modal="true" aria-label={title}>
          <div className="admin-ui-modal-header">
            <div>
              <h3>{title}</h3>
              {subtitle && <p>{subtitle}</p>}
            </div>
            <button type="button" className="admin-ui-icon-btn" onClick={onClose} aria-label="Close">
              <i className="bi bi-x-lg"></i>
            </button>
          </div>
          <div className="admin-ui-modal-body">{children}</div>
          {footer && <div className="admin-ui-modal-footer">{footer}</div>}
        </div>
      </div>
    </Layer>
  );
}

/** Right-side drawer with a hero area on top. */
export function Drawer({ label, onClose, children, width = 440 }) {
  useEffect(() => {
    const onKey = (e) => e.key === 'Escape' && onClose();
    window.addEventListener('keydown', onKey);
    return () => window.removeEventListener('keydown', onKey);
  }, [onClose]);

  return (
    <Layer>
      <div className="admin-ui-drawer-layer" onMouseDown={(e) => e.target === e.currentTarget && onClose()}>
        <aside className="admin-ui-drawer" style={{ width: `min(${width}px, 100vw)` }} role="dialog" aria-modal="true" aria-label={label}>
          {children}
        </aside>
      </div>
    </Layer>
  );
}

export function ConfirmDialog({ title, message, confirmLabel = 'Delete', busy, onConfirm, onClose }) {
  return (
    <Modal
      title={title}
      onClose={onClose}
      width={420}
      footer={
        <>
          <button type="button" className="admin-ui-btn admin-ui-btn--ghost" onClick={onClose} disabled={busy}>Cancel</button>
          <button type="button" className="admin-ui-btn admin-ui-btn--danger" onClick={onConfirm} disabled={busy}>
            {busy ? 'Working…' : confirmLabel}
          </button>
        </>
      }
    >
      <div className="admin-ui-confirm">
        <span className="admin-ui-confirm-icon"><i className="bi bi-exclamation-triangle"></i></span>
        <p>{message}</p>
      </div>
    </Modal>
  );
}

export function Field({ label, error, hint, wide, children }) {
  return (
    <label className={`admin-ui-field${wide ? ' admin-ui-field--wide' : ''}${error ? ' has-error' : ''}`}>
      <span className="admin-ui-field-label">{label}</span>
      {children}
      {error ? <span className="admin-ui-field-error">{error}</span> : hint && <span className="admin-ui-field-hint">{hint}</span>}
    </label>
  );
}
