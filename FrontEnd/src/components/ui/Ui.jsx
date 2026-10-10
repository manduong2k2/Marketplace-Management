// Shared UI building blocks for the store pages. Styles: ./ui.css (ui-*)
import { useEffect, useId, useMemo, useRef, useState } from 'react';
import { createPortal } from 'react-dom';
import { Link } from 'react-router-dom';
import { matchesSearch } from './uiUtils';
import './ui.css';

/** Renders overlays on document.body (outside the page's stacking context) with the ui tokens. */
export function Layer({ children }) {
  return createPortal(<div className="ui-layer">{children}</div>, document.body);
}

/** Page wrapper: provides the ui tokens and the page width. */
export function Page({ narrow, children }) {
  return <div className={`ui-page${narrow ? ' ui-page--narrow' : ''}`}>{children}</div>;
}

/** Title block: optional back link, eyebrow, title, description and actions on the right. */
export function PageHeader({ back, eyebrow, eyebrowIcon, title, description, actions }) {
  return (
    <>
      {back && (
        <Link to={back.to} className="ui-back">
          <i className="bi bi-arrow-left"></i> {back.label}
        </Link>
      )}
      <header className="ui-header">
        <div>
          {eyebrow && (
            <span className="ui-eyebrow">
              {eyebrowIcon && <i className={`bi ${eyebrowIcon}`}></i>} {eyebrow}
            </span>
          )}
          <h1>{title}</h1>
          {description && <p>{description}</p>}
        </div>
        {actions && <div className="ui-header-actions">{actions}</div>}
      </header>
    </>
  );
}

/** Card with an optional header (icon + title + subtitle + actions) and footer. */
export function Card({ icon, title, subtitle, actions, footer, children, bodyless, className = '', id }) {
  return (
    <section className={`ui-card ${className}`} id={id}>
      {(title || actions) && (
        <div className="ui-card-header">
          <div>
            {title && <h2>{icon && <i className={`bi ${icon}`}></i>}{title}</h2>}
            {subtitle && <p>{subtitle}</p>}
          </div>
          {actions}
        </div>
      )}
      {bodyless ? children : <div className="ui-card-body">{children}</div>}
      {footer && <div className="ui-card-footer">{footer}</div>}
    </section>
  );
}

/** Label + control + error/hint. Wraps any input/select/textarea/ComboBox. */
export function Field({ label, required, error, hint, wide, children, as = 'label' }) {
  const Tag = as;
  return (
    <Tag className={`ui-field${wide ? ' ui-field--wide' : ''}${error ? ' has-error' : ''}`}>
      {label && (
        <span className="ui-field-label">
          {label}{required && <span className="ui-required">*</span>}
        </span>
      )}
      {children}
      {error ? <span className="ui-field-error">{error}</span> : hint && <span className="ui-field-hint">{hint}</span>}
    </Tag>
  );
}

/** Password input with a show/hide toggle. */
export function PasswordInput({ value, onChange, name, autoComplete, placeholder, autoFocus }) {
  const [visible, setVisible] = useState(false);
  return (
    <span className="ui-input-wrap">
      <input
        type={visible ? 'text' : 'password'}
        name={name}
        value={value}
        onChange={onChange}
        autoComplete={autoComplete}
        placeholder={placeholder}
        autoFocus={autoFocus}
      />
      <button
        type="button"
        className="ui-icon-btn"
        onClick={() => setVisible(v => !v)}
        aria-label={visible ? 'Hide password' : 'Show password'}
        tabIndex={-1}
      >
        <i className={`bi ${visible ? 'bi-eye-slash' : 'bi-eye'}`}></i>
      </button>
    </span>
  );
}

/** Toggle switch with a title and description. */
export function Switch({ checked, onChange, title, description, disabled }) {
  return (
    <label className="ui-switch">
      <input type="checkbox" checked={checked} onChange={(e) => onChange(e.target.checked)} disabled={disabled} />
      <span className="ui-switch-track" aria-hidden="true"></span>
      <span className="ui-switch-text">
        <strong>{title}</strong>
        {description && <span>{description}</span>}
      </span>
    </label>
  );
}

/**
 * Searchable select. options: [{ value, label }]. Search ignores accents ("da nang" finds "Đà Nẵng").
 * Keyboard: ↑/↓ to move, Enter to pick, Esc to close.
 */
export function ComboBox({ options, value, onChange, placeholder = 'Select…', searchPlaceholder = 'Search…', disabled, emptyText = 'No results', loading }) {
  const [open, setOpen] = useState(false);
  const [query, setQuery] = useState('');
  const [active, setActive] = useState(0);
  const rootRef = useRef(null);
  const listRef = useRef(null);
  const listId = useId();

  const selected = options.find(o => o.value === value);
  const filtered = useMemo(
    () => options.filter(o => matchesSearch(o.label, query)),
    [options, query]
  );

  useEffect(() => {
    if (!open) return undefined;
    const onDown = (e) => { if (!rootRef.current?.contains(e.target)) setOpen(false); };
    document.addEventListener('mousedown', onDown);
    return () => document.removeEventListener('mousedown', onDown);
  }, [open]);

  // Keep the highlighted option visible while moving with the keyboard
  useEffect(() => {
    listRef.current?.children[active]?.scrollIntoView({ block: 'nearest' });
  }, [active]);

  const toggle = () => {
    if (disabled) return;
    setQuery('');
    setActive(Math.max(0, options.findIndex(o => o.value === value)));
    setOpen(o => !o);
  };

  const pick = (option) => {
    onChange(option.value);
    setOpen(false);
  };

  const onKeyDown = (e) => {
    if (e.key === 'ArrowDown') { e.preventDefault(); setActive(i => Math.min(i + 1, filtered.length - 1)); }
    else if (e.key === 'ArrowUp') { e.preventDefault(); setActive(i => Math.max(i - 1, 0)); }
    else if (e.key === 'Enter') { e.preventDefault(); if (filtered[active]) pick(filtered[active]); }
    else if (e.key === 'Escape') { setOpen(false); }
  };

  return (
    <div className={`ui-combo${open ? ' is-open' : ''}`} ref={rootRef}>
      <button
        type="button"
        className="ui-combo-trigger"
        onClick={toggle}
        disabled={disabled}
        aria-haspopup="listbox"
        aria-expanded={open}
        aria-controls={listId}
      >
        <span className={selected ? '' : 'ui-combo-placeholder'}>
          {loading ? 'Loading…' : selected ? selected.label : placeholder}
        </span>
        <i className={`bi ${open ? 'bi-chevron-up' : 'bi-chevron-down'}`}></i>
      </button>

      {open && (
        <div className="ui-combo-panel">
          <div className="ui-combo-search ui-field">
            <input
              type="search"
              value={query}
              placeholder={searchPlaceholder}
              onChange={(e) => { setQuery(e.target.value); setActive(0); }}
              onKeyDown={onKeyDown}
              autoFocus
            />
          </div>
          {filtered.length === 0 ? (
            <div className="ui-combo-empty">{emptyText}</div>
          ) : (
            <ul className="ui-combo-options" role="listbox" id={listId} ref={listRef}>
              {filtered.map((option, index) => (
                <li
                  key={option.value}
                  role="option"
                  aria-selected={option.value === value}
                  className={`ui-combo-option${index === active ? ' is-active' : ''}${option.value === value ? ' is-selected' : ''}`}
                  onMouseEnter={() => setActive(index)}
                  onMouseDown={(e) => { e.preventDefault(); pick(option); }}
                >
                  {option.label}
                  {option.value === value && <i className="bi bi-check2"></i>}
                </li>
              ))}
            </ul>
          )}
        </div>
      )}
    </div>
  );
}

/** Square product image, or a placeholder icon when there is none. */
export function Thumb({ src, small }) {
  const size = small ? ' ui-thumb--sm' : '';
  return src
    ? <img className={`ui-thumb${size}`} src={src} alt="" loading="lazy" />
    : <span className={`ui-thumb ui-thumb--empty${size}`} aria-hidden="true"><i className="bi bi-image"></i></span>;
}

/** Status pill. tone: success | warning | danger | info | accent | neutral */
export function Pill({ tone = 'neutral', dot = true, icon, children }) {
  return (
    <span className={`ui-pill ui-pill--${tone}`}>
      {icon ? <i className={`bi ${icon}`}></i> : dot && <span className="ui-pill-dot"></span>}
      {children}
    </span>
  );
}

/** Pagination bar. pagination: { currentPage (0-based), totalPages, totalElements, pageSize, hasNext, hasPrevious } */
export function Pagination({ pagination, onChange }) {
  if (!pagination || pagination.totalPages <= 1) return null;
  const { currentPage, totalPages, totalElements, pageSize } = pagination;
  const from = currentPage * pageSize + 1;
  const to = Math.min(totalElements, (currentPage + 1) * pageSize);

  return (
    <div className="ui-pagination">
      <span>{from}–{to} of {totalElements}</span>
      <div className="ui-pagination-buttons">
        <button type="button" disabled={!pagination.hasPrevious} onClick={() => onChange(currentPage - 1)} aria-label="Previous page">
          <i className="bi bi-chevron-left"></i>
        </button>
        <span className="ui-pagination-page">{currentPage + 1} / {totalPages}</span>
        <button type="button" disabled={!pagination.hasNext} onClick={() => onChange(currentPage + 1)} aria-label="Next page">
          <i className="bi bi-chevron-right"></i>
        </button>
      </div>
    </div>
  );
}

export function EmptyState({ icon, title, text, children }) {
  return (
    <div className="ui-empty">
      <i className={`bi ${icon}`}></i>
      <strong>{title}</strong>
      {text && <span>{text}</span>}
      {children}
    </div>
  );
}

/** Placeholder rows while loading. */
export function SkeletonRows({ rows = 3, thumb = false }) {
  return (
    <ul className="ui-list" aria-busy="true">
      {Array.from({ length: rows }, (_, i) => (
        <li key={i} className="ui-row">
          {thumb && <span className="ui-skeleton ui-thumb" style={{ border: 0 }}></span>}
          <div className="ui-row-main">
            <span className="ui-skeleton" style={{ width: '45%' }}></span>
            <span className="ui-skeleton" style={{ width: '70%', height: 12 }}></span>
          </div>
        </li>
      ))}
    </ul>
  );
}

/** Inline message. tone: info | success | warning | danger */
export function Notice({ tone = 'info', icon = 'bi-info-circle', children }) {
  return (
    <div className={`ui-notice ui-notice--${tone}`} role={tone === 'danger' ? 'alert' : undefined}>
      <i className={`bi ${icon}`}></i>
      <div>{children}</div>
    </div>
  );
}

export function QuantityStepper({ value, onChange, min = 1, max = 99, disabled }) {
  return (
    <div className="ui-stepper" role="group" aria-label="Quantity">
      <button type="button" onClick={() => onChange(value - 1)} disabled={disabled || value <= min} aria-label="Decrease quantity">
        <i className="bi bi-dash"></i>
      </button>
      <span aria-live="polite">{value}</span>
      <button type="button" onClick={() => onChange(value + 1)} disabled={disabled || value >= max} aria-label="Increase quantity">
        <i className="bi bi-plus"></i>
      </button>
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
      <div className="ui-overlay" onMouseDown={(e) => e.target === e.currentTarget && onClose()}>
        <div className="ui-modal" style={{ maxWidth: width }} role="dialog" aria-modal="true" aria-label={title}>
          <div className="ui-modal-header">
            <div>
              <h3>{title}</h3>
              {subtitle && <p>{subtitle}</p>}
            </div>
            <button type="button" className="ui-icon-btn" onClick={onClose} aria-label="Close">
              <i className="bi bi-x-lg"></i>
            </button>
          </div>
          <div className="ui-modal-body">{children}</div>
          {footer && <div className="ui-modal-footer">{footer}</div>}
        </div>
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
          <button type="button" className="ui-btn ui-btn--ghost" onClick={onClose} disabled={busy}>Cancel</button>
          <button type="button" className="ui-btn ui-btn--danger" onClick={onConfirm} disabled={busy}>
            {busy ? 'Working…' : confirmLabel}
          </button>
        </>
      }
    >
      <div className="ui-confirm">
        <span className="ui-confirm-icon"><i className="bi bi-exclamation-triangle"></i></span>
        <p>{message}</p>
      </div>
    </Modal>
  );
}
