// Identity & Access → Roles
import { useContext, useEffect, useState } from 'react';
import { AdminContext } from '../../../contexts/AdminContext';
import { roleAdminService, userAdminService } from '../../../services/identityService';
import { Avatar, Pagination, EmptyState, Modal, ConfirmDialog, Field } from '../shared/AdminUi';
import { RoleChip, StatusPill } from './IamShared';
import { PAGE_SIZE, useDebounce, useApiQuery, formatDate, readErrors } from '../shared/adminUiUtils';
import './IdentityAccess.css';

export default function AdminRolesPage() {
  const [roleSearch, setRoleSearch] = useState('');
  const debouncedRoleSearch = useDebounce(roleSearch);
  const [selectedId, setSelectedId] = useState(null);

  const [editing, setEditing] = useState(null);   // null | 'new' | role
  const [deleting, setDeleting] = useState(null);
  const [busy, setBusy] = useState(false);

  useEffect(() => { document.title = 'Roles - Identity & Access'; }, []);

  const rolesQuery = { size: 100, search: debouncedRoleSearch, sortBy: 'name' };
  const { response, loading: loadingRoles, reload: loadRoles } =
    useApiQuery(() => roleAdminService.list(rolesQuery), JSON.stringify(rolesQuery));
  const roles = response?.ok ? response.data.data || [] : [];

  // Selected role, falling back to the first one (e.g. after the selected role is deleted or filtered out)
  const selected = roles.find(r => r.id === selectedId) || roles[0] || null;

  const handleDelete = async () => {
    setBusy(true);
    const res = await roleAdminService.remove(deleting.id);
    setBusy(false);
    if (res.ok) {
      window.showSuccess?.('Role deleted');
      setDeleting(null);
      loadRoles();
    }
  };

  return (
    <div className="admin-ui-page">
      <header className="admin-ui-header">
        <div>
          <span className="admin-ui-eyebrow"><i className="bi bi-shield-lock"></i> Identity &amp; Access</span>
          <h1>Roles</h1>
          <p>Roles group permissions. Their code is checked by the API (e.g. <code>hasAuthority('ADMIN')</code>).</p>
        </div>
        <button type="button" className="admin-ui-btn admin-ui-btn--primary" onClick={() => setEditing('new')}>
          <i className="bi bi-plus-lg"></i> New role
        </button>
      </header>

      <div className="admin-ui-split">
        {/* ── Role list ── */}
        <aside className="admin-ui-card admin-iam-role-list">
          <label className="admin-ui-search admin-ui-search--compact">
            <i className="bi bi-search"></i>
            <input type="search" placeholder="Filter roles…" value={roleSearch} onChange={(e) => setRoleSearch(e.target.value)} />
          </label>

          <div className="admin-iam-role-cards">
            {loadingRoles && roles.length === 0 && [...Array(3)].map((_, i) => <div key={i} className="admin-iam-role-card is-skeleton"></div>)}
            {roles.map(role => (
              <button
                key={role.id}
                type="button"
                className={`admin-iam-role-card${role.id === selectedId ? ' is-active' : ''}`}
                onClick={() => setSelectedId(role.id)}
              >
                <span className={`admin-iam-role-badge admin-iam-chip--${role.code.toLowerCase()}`}>
                  <i className={`bi ${role.system ? 'bi-lock-fill' : 'bi-person-badge'}`}></i>
                </span>
                <span className="admin-iam-role-card-body">
                  <strong>{role.name}</strong>
                  <code>{role.code}</code>
                </span>
                <span className="admin-iam-role-count" title="Users with this role">
                  <i className="bi bi-people"></i> {role.usersCount ?? 0}
                </span>
              </button>
            ))}
            {!loadingRoles && roles.length === 0 && <EmptyState icon="bi-person-badge" title="No roles found" />}
          </div>
        </aside>

        {/* ── Selected role ── */}
        <section className="admin-ui-card admin-iam-role-detail">
          {selected
            ? <RoleDetail
                key={selected.id}
                role={selected}
                onEdit={() => setEditing(selected)}
                onDelete={() => setDeleting(selected)}
                onMembersChanged={loadRoles}
              />
            : <EmptyState icon="bi-hand-index" title="Select a role" text="Pick a role on the left to see its members." />}
        </section>
      </div>

      {editing && (
        <RoleFormModal
          role={editing === 'new' ? null : editing}
          onClose={() => setEditing(null)}
          onSaved={(saved) => { setEditing(null); setSelectedId(saved.id); loadRoles(); }}
        />
      )}

      {deleting && (
        <ConfirmDialog
          title="Delete role"
          message={<>Delete the role <strong>{deleting.name}</strong> (<code>{deleting.code}</code>)?</>}
          busy={busy}
          onConfirm={handleDelete}
          onClose={() => setDeleting(null)}
        />
      )}
    </div>
  );
}

// ── Role detail + members ───────────────────────────────────────────────────
function RoleDetail({ role, onEdit, onDelete, onMembersChanged }) {
  const { admin } = useContext(AdminContext);
  const [page, setPage] = useState(0);
  const [search, setSearch] = useState('');
  const debouncedSearch = useDebounce(search);
  const [adding, setAdding] = useState(false);
  const [revoking, setRevoking] = useState(null);
  const [busy, setBusy] = useState(false);

  const membersQuery = { page, size: PAGE_SIZE, search: debouncedSearch, sortBy: 'name', sortOrder: 'asc' };
  const { response, loading, reload: loadMembers } = useApiQuery(
    () => roleAdminService.users(role.id, membersQuery),
    `${role.id}:${JSON.stringify(membersQuery)}`
  );
  const members = response?.ok ? response.data.data || [] : [];
  const pagination = response?.ok ? response.data.pagination : null;

  const afterChange = () => { loadMembers(); onMembersChanged(); };

  const handleRevoke = async () => {
    setBusy(true);
    const res = await userAdminService.revokeRole(role.id, [revoking.id]);
    setBusy(false);
    if (res.ok) {
      window.showSuccess?.(`Removed ${revoking.email} from ${role.name}`);
      setRevoking(null);
      afterChange();
    }
  };

  const cannotRevoke = (user) => role.code === 'ADMIN' && admin?.id === user.id;
  const deleteBlockedReason = role.system
    ? 'System roles cannot be deleted'
    : role.usersCount > 0 ? 'Remove all members before deleting' : null;

  return (
    <>
      <div className="admin-ui-detail-head">
        <div className="admin-ui-detail-title">
          <span className={`admin-iam-role-badge admin-iam-role-badge--lg admin-iam-chip--${role.code.toLowerCase()}`}>
            <i className={`bi ${role.system ? 'bi-lock-fill' : 'bi-person-badge'}`}></i>
          </span>
          <div>
            <h2>{role.name} {role.system && <span className="admin-ui-tag">System</span>}</h2>
            <span className="admin-ui-muted">
              <code>{role.code}</code> · {role.usersCount ?? 0} member{role.usersCount === 1 ? '' : 's'} · created {formatDate(role.createdAt)}
            </span>
          </div>
        </div>
        <div className="admin-ui-detail-actions">
          <button
            type="button"
            className="admin-ui-btn admin-ui-btn--ghost"
            onClick={onEdit}
            disabled={role.system}
            title={role.system ? 'System roles are read-only' : 'Edit role'}
          >
            <i className="bi bi-pencil"></i> Edit
          </button>
          <button
            type="button"
            className="admin-ui-btn admin-ui-btn--ghost admin-ui-btn--danger-text"
            onClick={onDelete}
            disabled={!!deleteBlockedReason}
            title={deleteBlockedReason || 'Delete role'}
          >
            <i className="bi bi-trash3"></i> Delete
          </button>
        </div>
      </div>

      <div className="admin-iam-members-bar">
        <label className="admin-ui-search admin-ui-search--compact">
          <i className="bi bi-search"></i>
          <input type="search" placeholder="Search members…" value={search} onChange={(e) => { setSearch(e.target.value); setPage(0); }} />
        </label>
        <button type="button" className="admin-ui-btn admin-ui-btn--primary admin-ui-btn--sm" onClick={() => setAdding(true)}>
          <i className="bi bi-person-plus"></i> Add members
        </button>
      </div>

      <ul className="admin-iam-member-list">
        {loading && members.length === 0 && [...Array(3)].map((_, i) => <li key={i} className="admin-iam-member is-skeleton"></li>)}
        {members.map(user => (
          <li key={user.id} className="admin-iam-member">
            <div className="admin-ui-user">
              <Avatar user={user} />
              <div>
                <strong>{user.name || 'Unnamed'}</strong>
                <span>{user.email}</span>
              </div>
            </div>
            <div className="admin-ui-chips admin-iam-member-roles">
              {user.roles.filter(r => r.id !== role.id).map(r => <RoleChip key={r.id} role={r} />)}
            </div>
            <StatusPill status={user.status} />
            <button
              type="button"
              className="admin-ui-icon-btn admin-ui-icon-btn--danger"
              title={cannotRevoke(user) ? 'You cannot revoke your own ADMIN role' : `Remove from ${role.name}`}
              disabled={cannotRevoke(user)}
              onClick={() => setRevoking(user)}
            >
              <i className="bi bi-person-dash"></i>
            </button>
          </li>
        ))}
      </ul>
      {!loading && members.length === 0 && (
        <EmptyState icon="bi-people" title={search ? 'No matching members' : 'No members yet'} text={search ? null : 'Use "Add members" to grant this role.'} />
      )}
      <Pagination pagination={pagination} onChange={setPage} />

      {adding && <AddMembersModal role={role} onClose={() => setAdding(false)} onAdded={() => { setAdding(false); afterChange(); }} />}

      {revoking && (
        <ConfirmDialog
          title="Remove member"
          message={<>Remove <strong>{revoking.email}</strong> from <strong>{role.name}</strong>?</>}
          confirmLabel="Remove"
          busy={busy}
          onConfirm={handleRevoke}
          onClose={() => setRevoking(null)}
        />
      )}
    </>
  );
}

// ── Pick users to grant the role to ─────────────────────────────────────────
function AddMembersModal({ role, onClose, onAdded }) {
  const [search, setSearch] = useState('');
  const debouncedSearch = useDebounce(search);
  const [picked, setPicked] = useState({});   // id -> user
  const [saving, setSaving] = useState(false);

  const pickerQuery = { size: 20, search: debouncedSearch, sortBy: 'name', sortOrder: 'asc' };
  const { response, loading } = useApiQuery(() => userAdminService.list(pickerQuery), JSON.stringify(pickerQuery));
  const results = response?.ok ? response.data.data || [] : [];

  const togglePick = (user) => setPicked(prev => {
    const next = { ...prev };
    if (next[user.id]) delete next[user.id]; else next[user.id] = user;
    return next;
  });

  const pickedUsers = Object.values(picked);

  const submit = async () => {
    setSaving(true);
    const res = await userAdminService.grantRole(role.id, pickedUsers.map(u => u.id));
    setSaving(false);
    if (res.ok) {
      window.showSuccess?.(`Added ${res.data.data.affected} member(s) to ${role.name}`);
      onAdded();
    }
  };

  return (
    <Modal
      title={`Add members to ${role.name}`}
      subtitle="Users who already have this role are shown as members."
      onClose={onClose}
      width={560}
      footer={
        <>
          <span className="admin-ui-muted admin-ui-footer-note">{pickedUsers.length} selected</span>
          <button type="button" className="admin-ui-btn admin-ui-btn--ghost" onClick={onClose}>Cancel</button>
          <button type="button" className="admin-ui-btn admin-ui-btn--primary" disabled={!pickedUsers.length || saving} onClick={submit}>
            {saving ? 'Adding…' : `Add ${pickedUsers.length || ''}`.trim()}
          </button>
        </>
      }
    >
      <label className="admin-ui-search">
        <i className="bi bi-search"></i>
        <input type="search" placeholder="Search users by name or email…" value={search} onChange={(e) => setSearch(e.target.value)} autoFocus />
      </label>

      {pickedUsers.length > 0 && (
        <div className="admin-ui-chips admin-iam-picked">
          {pickedUsers.map(u => (
            <RoleChip key={u.id} role={{ name: u.name || u.email, code: '' }} onRemove={() => togglePick(u)} />
          ))}
        </div>
      )}

      <ul className="admin-iam-pick-list">
        {loading && results.length === 0 && <li className="admin-ui-muted admin-iam-pick-loading">Loading…</li>}
        {results.map(user => {
          const isMember = user.roles.some(r => r.id === role.id);
          const isPicked = !!picked[user.id];
          return (
            <li key={user.id}>
              <button
                type="button"
                className={`admin-iam-pick${isPicked ? ' is-picked' : ''}`}
                disabled={isMember}
                onClick={() => togglePick(user)}
                aria-pressed={isPicked}
              >
                <Avatar user={user} size={32} />
                <span className="admin-iam-pick-text">
                  <strong>{user.name || 'Unnamed'}</strong>
                  <span>{user.email}</span>
                </span>
                {isMember
                  ? <span className="admin-ui-tag">Member</span>
                  : <span className="admin-iam-checkbox">{isPicked && <i className="bi bi-check2"></i>}</span>}
              </button>
            </li>
          );
        })}
        {!loading && results.length === 0 && <li className="admin-ui-muted admin-iam-pick-loading">No users found</li>}
      </ul>
    </Modal>
  );
}

// ── Create / edit role ──────────────────────────────────────────────────────
function RoleFormModal({ role, onClose, onSaved }) {
  const [form, setForm] = useState({ name: role?.name || '', code: role?.code || '' });
  const [codeTouched, setCodeTouched] = useState(!!role);
  const [errors, setErrors] = useState({});
  const [saving, setSaving] = useState(false);

  // Suggest a code from the name until the code is edited by hand: "Content Manager" -> CONTENT_MANAGER
  const toCode = (name) => name.normalize('NFD').replace(/[̀-ͯ]/g, '').toUpperCase()
    .replace(/[^A-Z0-9]+/g, '_').replace(/^_+|_+$/g, '').replace(/^(\d)/, 'R_$1');

  const onName = (e) => {
    const name = e.target.value;
    setForm(prev => ({ name, code: codeTouched ? prev.code : toCode(name) }));
  };

  const onCode = (e) => {
    setCodeTouched(true);
    setForm(prev => ({ ...prev, code: e.target.value.toUpperCase().replace(/[^A-Z0-9_]/g, '') }));
  };

  const submit = async (e) => {
    e.preventDefault();
    setSaving(true);
    const res = role ? await roleAdminService.update(role.id, form) : await roleAdminService.create(form);
    setSaving(false);
    if (res.ok) {
      window.showSuccess?.(role ? 'Role updated' : 'Role created');
      onSaved(res.data.data);
    } else {
      const { fields, message } = readErrors(res);
      setErrors(fields);
      if (message) window.showError?.(message);
    }
  };

  return (
    <Modal
      title={role ? 'Edit role' : 'New role'}
      subtitle={role ? null : 'Users get the new role through "Add members" or the user drawer.'}
      onClose={onClose}
      width={460}
      footer={
        <>
          <button type="button" className="admin-ui-btn admin-ui-btn--ghost" onClick={onClose}>Cancel</button>
          <button type="submit" form="admin-ui-role-form" className="admin-ui-btn admin-ui-btn--primary" disabled={saving}>
            {saving ? 'Saving…' : role ? 'Save changes' : 'Create role'}
          </button>
        </>
      }
    >
      <form id="admin-ui-role-form" className="admin-ui-form" onSubmit={submit}>
        <Field label="Name" error={errors.name}>
          <input value={form.name} onChange={onName} required maxLength={100} autoFocus placeholder="e.g. Content Manager" />
        </Field>
        <Field label="Code" error={errors.code} hint="UPPER_SNAKE_CASE. Used in the JWT and in hasAuthority(...) checks.">
          <input className="admin-ui-mono" value={form.code} onChange={onCode} required maxLength={50} placeholder="CONTENT_MANAGER" />
        </Field>
      </form>
    </Modal>
  );
}
