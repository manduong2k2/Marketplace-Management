import './VendorCard.css';

const STATUS_CONFIG = {
  ACTIVE:   { label: 'Active',   color: '#22c55e', bg: '#f0fdf4', icon: '✅' },
  PENDING:  { label: 'Pending',  color: '#f59e0b', bg: '#fffbeb', icon: '⏳' },
  INACTIVE: { label: 'Inactive', color: '#6b7280', bg: '#f3f4f6', icon: '⏸️' },
  BANNED:   { label: 'Banned',   color: '#ef4444', bg: '#fef2f2', icon: '🚫' },
};

function StatusBadge({ status }) {
  if (!status) return null;
  const cfg = STATUS_CONFIG[status.toUpperCase()] ?? {
    label: status, color: '#6b7280', bg: '#f3f4f6', icon: '❓',
  };
  return (
    <span
      className="vendor-card-status"
      style={{ color: cfg.color, background: cfg.bg, borderColor: cfg.color + '33' }}
    >
      {cfg.icon} {cfg.label}
    </span>
  );
}

/**
 * Shared VendorCard — used in both MyVendorPage and VendorDetailPage.
 *
 * Props:
 *   vendor  — vendor object from API
 *   onClick — optional click handler (e.g. navigate to detail)
 */
export default function VendorCard({ vendor, onClick }) {
  if (!vendor) return null;

  const { name, email, description, phone, taxCode, logoUrl, bannerUrl, status } = vendor;

  return (
    <div className="vendor-card" onClick={onClick} style={onClick ? { cursor: 'pointer' } : {}}>
      {/* Banner */}
      {bannerUrl
        ? <img src={bannerUrl} alt={`${name} banner`} className="vendor-card-banner" />
        : <div className="vendor-card-banner-placeholder" />
      }

      {/* Logo + name */}
      <div className="vendor-card-identity">
        <div className="vendor-card-logo-wrap">
          {logoUrl
            ? <img src={logoUrl} alt={`${name} logo`} className="vendor-card-logo" />
            : <div className="vendor-card-logo-placeholder">🏪</div>
          }
        </div>
        <div className="vendor-card-name-row">
          <div className="vendor-card-name-line">
            <h3 className="vendor-card-name">{name}</h3>
            <StatusBadge status={status} />
          </div>
          {email && <p className="vendor-card-email">{email}</p>}
        </div>
      </div>

      {/* Body */}
      <div className="vendor-card-body">
        {description && <p className="vendor-card-desc">{description}</p>}
        <div className="vendor-card-meta">
          {phone   && <span className="vendor-card-tag"><span>📞</span>{phone}</span>}
          {taxCode && <span className="vendor-card-tag"><span>🧾</span>{taxCode}</span>}
        </div>
      </div>
    </div>
  );
}
