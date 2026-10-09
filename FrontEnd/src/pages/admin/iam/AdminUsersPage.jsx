// Identity & Access → Users
import { useContext, useEffect, useState } from 'react';
import { AdminContext } from '../../../contexts/AdminContext';
import { userAdminService, roleAdminService } from '../../../services/identityService';
import { Avatar, Pagination, EmptyState, Modal, ConfirmDialog, Field, Layer } from '../shared/AdminUi';
import { RoleChip, StatusPill } from './IamShared';
import { PAGE_SIZE, useDebounce, useApiQuery, formatDate, readErrors } from '../shared/adminUiUtils';
import './IdentityAccess.css';

const SORTABLE = { name: 'Name', email: 'Email', createdAt: 'Created' };

export default function AdminUsersPage() {
  const { admin } = useContext(AdminContext);
  const [roles, setRoles] = useState([]);

  const [page, setPage] = useState(0);
  const [search, setSearch] = useState('');
  const [roleFilter, setRoleFilter] = useState('');
  const [statusFilter, setStatusFilter] = useState('');
  const [sort, setSort] = useState({ by: 'createdAt', order: 'desc' });
  const debouncedSearch = useDebounce(search);

  const [selected, setSelected] = useState(null);    // user shown in the drawer
  const [creating, setCreating] = useState(false);
  const [deleting, setDeleting] = useState(null);
  const [busy, setBusy] = useState(false);

  useEffect(() => {
    document.title = 'Users - Identity & Access';
    roleAdminService.list({ size: 100, sortBy: 'name' }).then(res => res.ok && setRoles(res.data.data || []));
  }, []);

  const query = {
    page, size: PAGE_SIZE, search: debouncedSearch, roleId: roleFilter, status: statusFilter,
    sortBy: sort.by, sortOrder: sort.order,
  };
  const { response, loading, reload: loadUsers } = useApiQuery(() => userAdminService.list(query), JSON.stringify(query));
  const users = response?.ok ? response.data.data || [] : [];
  const pagination = response?.ok ? response.data.pagination : null;

  // Back to the first page whenever the filters change
  const updateFilter = (setter) => (value) => { setter(value); setPage(0); };

  const toggleSort = (by) => {
    setSort(prev => ({ by, order: prev.by === by && prev.order === 'asc' ? 'desc' : 'asc' }));
    setPage(0);
  };

  const refreshSelected = async (id) => {
    const res = await userAdminService.get(id);
    if (res.ok) setSelected(res.data.data);
  };

  const handleDelete = async () => {
    setBusy(true);
    const res = await userAdminService.remove(deleting.id);
    setBusy(false);
    if (res.ok) {
      window.showSuccess?.('User deleted');
      setDeleting(null);
      if (selected?.id === deleting.id) setSelected(null);
      loadUsers();
    }
  };

  const isSelf = (user) => admin?.id && user?.id === admin.id;

  return (
    <div className="admin-ui-page">
      <header className="admin-ui-header">
        <div>
          <span className="admin-ui-eyebrow"><i className="bi bi-shield-lock"></i> Identity &amp; Access</span>
          <h1>Users</h1>
          <p>Manage accounts, their status and the roles that control what they can access.</p>
        </div>
        <button type="button" className="admin-ui-btn admin-ui-btn--primary" onClick={() => setCreating(true)}>
          <i className="bi bi-person-plus"></i> New user
        </button>
      </header>

      <div className="admin-ui-toolbar">
        <label className="admin-ui-search">
          <i className="bi bi-search"></i>
          <input
            type="search"
            placeholder="Search by name or email…"
            value={search}
            onChange={(e) => updateFilter(setSearch)(e.target.value)}
          />
        </label>

        <select className="admin-ui-select" value={roleFilter} onChange={(e) => updateFilter(setRoleFilter)(e.target.value)}>
          <option value="">All roles</option>
          {roles.map(role => <option key={role.id} value={role.id}>{role.name}</option>)}
        </select>

        <div className="admin-ui-segmented" role="group" aria-label="Status filter">
          {[['', 'All'], ['ACTIVE', 'Active'], ['INACTIVE', 'Inactive']].map(([value, label]) => (
            <button
              key={label}
              type="button"
              className={statusFilter === value ? 'active' : ''}
              onClick={() => updateFilter(setStatusFilter)(value)}
            >
              {label}
            </button>
          ))}
        </div>
      </div>

      <div className="admin-ui-card">
        <div className="admin-ui-table-wrap">
          <table className="admin-ui-table">
            <thead>
              <tr>
                <SortHeader by="name" sort={sort} onSort={toggleSort} />
                <th>Roles</th>
                <th>Status</th>
                <th>Sign-in</th>
                <SortHeader by="createdAt" sort={sort} onSort={toggleSort} />
                <th className="admin-ui-col-actions"><span className="visually-hidden">Actions</span></th>
              </tr>
            </thead>
            <tbody>
              {loading && users.length === 0 && [...Array(5)].map((_, i) => (
                <tr key={i} className="admin-ui-skeleton-row"><td colSpan={6}><span></span></td></tr>
              ))}
              {users.map(user => (
                <tr
                  key={user.id}
                  className={selected?.id === user.id ? 'is-selected' : ''}
                  onClick={() => setSelected(user)}
                >
                  <td>
                    <div className="admin-ui-user">
                      <Avatar user={user} />
                      <div>
                        <strong>{user.name || 'Unnamed'}{isSelf(user) && <span className="admin-ui-you">You</span>}</strong>
                        <span>{user.email}</span>
                      </div>
                    </div>
                  </td>
                  <td><div className="admin-ui-chips">{user.roles.map(role => <RoleChip key={role.id} role={role} />)}</div></td>
                  <td><StatusPill status={user.status} /></td>
                  <td>
                    <span className="admin-iam-signin">
                      <SignInMethods providers={user.oauthProviders} />
                    </span>
                  </td>
                  <td className="admin-ui-muted">{formatDate(user.createdAt)}</td>
                  <td className="admin-ui-col-actions" onClick={(e) => e.stopPropagation()}>
                    <button type="button" className="admin-ui-icon-btn" title="Edit" onClick={() => setSelected(user)}>
                      <i className="bi bi-pencil"></i>
                    </button>
                    <button
                      type="button"
                      className="admin-ui-icon-btn admin-ui-icon-btn--danger"
                      title={isSelf(user) ? 'You cannot delete your own account' : 'Delete'}
                      disabled={isSelf(user)}
                      onClick={() => setDeleting(user)}
                    >
                      <i className="bi bi-trash3"></i>
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
          {!loading && users.length === 0 && (
            <EmptyState icon="bi-people" title="No users found" text="Try another search or filter." />
          )}
        </div>
        <Pagination pagination={pagination} onChange={setPage} />
      </div>

      {selected && (
        <UserDrawer
          key={selected.id}
          user={selected}
          roles={roles}
          isSelf={isSelf(selected)}
          onClose={() => setSelected(null)}
          onChanged={() => { refreshSelected(selected.id); loadUsers(); }}
        />
      )}

      {creating && (
        <CreateUserModal
          roles={roles}
          onClose={() => setCreating(false)}
          onCreated={() => { setCreating(false); setPage(0); loadUsers(); }}
        />
      )}

      {deleting && (
        <ConfirmDialog
          title="Delete user"
          message={<>Delete <strong>{deleting.email}</strong>? They will no longer be able to sign in.</>}
          busy={busy}
          onConfirm={handleDelete}
          onClose={() => setDeleting(null)}
        />
      )}
    </div>
  );
}

const PROVIDERS = {
  GOOGLE: { icon: 'bi-google', label: 'Google' },
  FACEBOOK: { icon: 'bi-facebook', label: 'Facebook' },
};

// Linked OAuth providers (e.g. ["FACEBOOK", "GOOGLE"]), or "Password" when there are none
function SignInMethods({ providers = [], linked = false }) {
  if (!providers.length) return <><i className="bi bi-key"></i> Password</>;
  const labels = providers.map((code) => PROVIDERS[code]?.label ?? code).join(', ');
  return (
    <span title={`${labels}${linked ? ' linked' : ''}`}>
      {providers.map((code) => <i key={code} className={`bi ${PROVIDERS[code]?.icon ?? 'bi-person-badge'}`}></i>)}
      {' '}{labels}{linked ? ' linked' : ''}
    </span>
  );
}

function SortHeader({ by, sort, onSort }) {
  const active = sort.by === by;
  return (
    <th aria-sort={active ? (sort.order === 'asc' ? 'ascending' : 'descending') : 'none'}>
      <button type="button" className={`admin-ui-sort${active ? ' active' : ''}`} onClick={() => onSort(by)}>
        {by === 'name' ? 'User' : SORTABLE[by]}
        <i className={`bi ${active ? (sort.order === 'asc' ? 'bi-arrow-up' : 'bi-arrow-down') : 'bi-arrow-down-up'}`}></i>
      </button>
    </th>
  );
}

// ── Side drawer: edit details + toggle roles ─────────────────────────────────
function UserDrawer({ user, roles, isSelf, onClose, onChanged }) {
  const [form, setForm] = useState({ name: user.name || '', phone: user.phone || '', status: user.status, password: '' });
  const [errors, setErrors] = useState({});
  const [saving, setSaving] = useState(false);
  const [pendingRole, setPendingRole] = useState(null);

  useEffect(() => {
    const onKey = (e) => e.key === 'Escape' && onClose();
    window.addEventListener('keydown', onKey);
    return () => window.removeEventListener('keydown', onKey);
  }, [onClose]);

  const set = (field) => (e) => setForm(prev => ({ ...prev, [field]: e.target.value }));
  const hasRole = (roleId) => user.roles.some(r => r.id === roleId);

  const save = async (e) => {
    e.preventDefault();
    setSaving(true);
    const res = await userAdminService.update(user.id, form);
    setSaving(false);
    if (res.ok) {
      setErrors({});
      setForm(prev => ({ ...prev, password: '' }));
      window.showSuccess?.('User updated');
      onChanged();
    } else {
      const { fields, message } = readErrors(res);
      setErrors(fields);
      if (message) window.showError?.(message);
    }
  };

  const toggleRole = async (role) => {
    setPendingRole(role.id);
    const action = hasRole(role.id) ? userAdminService.revokeRole : userAdminService.grantRole;
    const res = await action(role.id, [user.id]);
    setPendingRole(null);
    if (res.ok) onChanged();
  };

  return (
    <Layer>
    <div className="admin-ui-drawer-layer" onMouseDown={(e) => e.target === e.currentTarget && onClose()}>
      <aside className="admin-ui-drawer" role="dialog" aria-modal="true" aria-label={`Edit ${user.email}`}>
        <div className="admin-ui-drawer-hero">
          <button type="button" className="admin-ui-icon-btn admin-ui-drawer-close" onClick={onClose} aria-label="Close">
            <i className="bi bi-x-lg"></i>
          </button>
          <Avatar user={user} size={64} />
          <h2>{user.name || 'Unnamed'}</h2>
          <span className="admin-ui-muted">{user.email}</span>
          <div className="admin-ui-drawer-meta">
            <StatusPill status={user.status} />
            <span className="admin-iam-signin">
              <SignInMethods providers={user.oauthProviders} linked />
            </span>
            <span className="admin-ui-muted"><i className="bi bi-calendar3"></i> {formatDate(user.createdAt)}</span>
          </div>
        </div>

        <section className="admin-ui-drawer-section">
          <h3>Roles</h3>
          <p className="admin-ui-hint">Changes apply on the user's next sign-in or token refresh.</p>
          <div className="admin-iam-role-toggles">
            {roles.map(role => {
              const checked = hasRole(role.id);
              const locked = isSelf && role.code === 'ADMIN' && checked;
              return (
                <label key={role.id} className={`admin-iam-toggle${checked ? ' is-on' : ''}${locked ? ' is-locked' : ''}`}>
                  <span>
                    <strong>{role.name}</strong>
                    <code>{role.code}</code>
                  </span>
                  <input
                    type="checkbox"
                    role="switch"
                    checked={checked}
                    disabled={locked || pendingRole !== null}
                    onChange={() => toggleRole(role)}
                    title={locked ? 'You cannot revoke your own ADMIN role' : undefined}
                  />
                  <span className="admin-iam-switch" aria-hidden="true"></span>
                </label>
              );
            })}
          </div>
        </section>

        <form className="admin-ui-drawer-section admin-ui-form" onSubmit={save}>
          <h3>Details</h3>
          <Field label="Name" error={errors.name}>
            <input value={form.name} onChange={set('name')} maxLength={255} />
          </Field>
          <Field label="Phone" error={errors.phone}>
            <input value={form.phone} onChange={set('phone')} maxLength={20} placeholder="Optional" />
          </Field>
          <Field label="Status" error={errors.status}>
            <select value={form.status} onChange={set('status')}>
              <option value="ACTIVE">Active</option>
              <option value="INACTIVE">Inactive</option>
            </select>
          </Field>
          <Field label="New password" error={errors.password} hint="Leave empty to keep the current password">
            <input type="password" value={form.password} onChange={set('password')} autoComplete="new-password" />
          </Field>
          <div className="admin-ui-form-actions">
            <button type="button" className="admin-ui-btn admin-ui-btn--ghost" onClick={onClose}>Close</button>
            <button type="submit" className="admin-ui-btn admin-ui-btn--primary" disabled={saving}>
              {saving ? 'Saving…' : 'Save changes'}
            </button>
          </div>
        </form>
      </aside>
    </div>
    </Layer>
  );
}

// ── Create user ─────────────────────────────────────────────────────────────
function CreateUserModal({ roles, onClose, onCreated }) {
  const defaultRole = roles.find(r => r.code === 'USER');
  const [form, setForm] = useState({ email: '', name: '', password: '', phone: '', status: 'ACTIVE' });
  const [roleIds, setRoleIds] = useState(defaultRole ? [defaultRole.id] : []);
  const [errors, setErrors] = useState({});
  const [saving, setSaving] = useState(false);

  const set = (field) => (e) => setForm(prev => ({ ...prev, [field]: e.target.value }));
  const toggleRoleId = (id) => setRoleIds(prev => prev.includes(id) ? prev.filter(x => x !== id) : [...prev, id]);

  const submit = async (e) => {
    e.preventDefault();
    setSaving(true);
    const res = await userAdminService.create({ ...form, roleIds });
    setSaving(false);
    if (res.ok) {
      window.showSuccess?.('User created');
      onCreated();
    } else {
      const { fields, message } = readErrors(res);
      setErrors(fields);
      if (message) window.showError?.(message);
    }
  };

  return (
    <Modal
      title="New user"
      subtitle="Accounts created here are active right away unless you choose Inactive."
      onClose={onClose}
      footer={
        <>
          <button type="button" className="admin-ui-btn admin-ui-btn--ghost" onClick={onClose}>Cancel</button>
          <button type="submit" form="admin-ui-create-user" className="admin-ui-btn admin-ui-btn--primary" disabled={saving}>
            {saving ? 'Creating…' : 'Create user'}
          </button>
        </>
      }
    >
      <form id="admin-ui-create-user" className="admin-ui-form admin-ui-form--grid" onSubmit={submit}>
        <Field label="Email" error={errors.email} wide>
          <input type="email" value={form.email} onChange={set('email')} required maxLength={255} autoFocus />
        </Field>
        <Field label="Name" error={errors.name}>
          <input value={form.name} onChange={set('name')} maxLength={255} />
        </Field>
        <Field label="Phone" error={errors.phone}>
          <input value={form.phone} onChange={set('phone')} maxLength={20} placeholder="Optional" />
        </Field>
        <Field label="Password" error={errors.password}>
          <input type="password" value={form.password} onChange={set('password')} required minLength={6} autoComplete="new-password" />
        </Field>
        <Field label="Status" error={errors.status}>
          <select value={form.status} onChange={set('status')}>
            <option value="ACTIVE">Active</option>
            <option value="INACTIVE">Inactive (sends activation email)</option>
          </select>
        </Field>
        <Field label="Roles" wide hint="Defaults to User when none is selected">
          <div className="admin-ui-chip-picker">
            {roles.map(role => (
              <button
                key={role.id}
                type="button"
                className={roleIds.includes(role.id) ? 'is-picked' : ''}
                onClick={() => toggleRoleId(role.id)}
                aria-pressed={roleIds.includes(role.id)}
              >
                {roleIds.includes(role.id) && <i className="bi bi-check2"></i>}
                {role.name}
              </button>
            ))}
          </div>
        </Field>
      </form>
    </Modal>
  );
}
