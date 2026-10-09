// Identity & Access specific components. Generic ones live in ../shared/AdminUi.jsx
import { Pill } from '../shared/AdminUi';

export function RoleChip({ role, onRemove, disabled }) {
  return (
    // admin-iam-chip--{code}: color of built-in roles (IdentityAccess.css)
    <span className={`admin-ui-chip admin-iam-chip--${(role.code || '').toLowerCase()}`} title={role.code}>
      {role.name}
      {onRemove && (
        <button type="button" onClick={onRemove} disabled={disabled} aria-label={`Remove ${role.name}`}>
          <i className="bi bi-x"></i>
        </button>
      )}
    </span>
  );
}

export function StatusPill({ status }) {
  const active = status === 'ACTIVE';
  return <Pill tone={active ? 'success' : 'neutral'}>{active ? 'Active' : 'Inactive'}</Pill>;
}
