// Marketplace → Vendors
import { useEffect, useState } from 'react';
import { vendorService } from '../../../services/vendorService';
import VendorForm from '../../../components/vendor/form/VendorForm';
import { PageHeader, SearchBox, Avatar, Pill, EmptyState, Modal, ConfirmDialog, Field } from '../shared/AdminUi';
import { useApiQuery } from '../shared/adminUiUtils';
import './AdminVendorsPage.css';

const STATUS = {
  ACTIVE:   { label: 'Active',   tone: 'success' },
  PENDING:  { label: 'Pending',  tone: 'warning' },
  INACTIVE: { label: 'Inactive', tone: 'neutral' },
  BANNED:   { label: 'Banned',   tone: 'danger' },
};

const STATUS_FILTERS = [['', 'All'], ['PENDING', 'Pending'], ['ACTIVE', 'Active'], ['BANNED', 'Banned']];

function VendorStatus({ status }) {
  if (!status) return <span className="admin-ui-muted">—</span>;
  const cfg = STATUS[status.toUpperCase()] ?? { label: status, tone: 'neutral' };
  return <Pill tone={cfg.tone}>{cfg.label}</Pill>;
}

const toFormData = (formData) => {
  const fd = new FormData();
  if (formData.userId) fd.append('userId', formData.userId);   // create only: the owner
  fd.append('name', formData.name);
  fd.append('email', formData.email || '');
  fd.append('description', formData.description || '');
  fd.append('phone', formData.phone || '');
  fd.append('taxCode', formData.taxCode || '');
  if (formData.logoFile) fd.append('logo', formData.logoFile);
  if (formData.bannerFile) fd.append('banner', formData.bannerFile);
  return fd;
};

export default function AdminVendorsPage() {
  useEffect(() => { document.title = 'Admin - Vendors'; }, []);

  const { response, loading, reload: fetchVendors } = useApiQuery(() => vendorService.getAll(), 'vendors');
  const vendors = response?.ok ? response.data?.data || [] : [];
  const error = !loading && !response?.ok ? 'Failed to load vendors.' : null;

  const [search, setSearch]             = useState('');
  const [statusFilter, setStatusFilter] = useState('');
  const [submitting, setSubmitting]     = useState(false);
  const [modal, setModal]               = useState(null);   // 'create' | { mode: 'edit', vendor }
  const [deleteTarget, setDeleteTarget] = useState(null);
  const [deleting, setDeleting]         = useState(false);
  const [reasonAction, setReasonAction] = useState(null);   // { type: 'reject' | 'suspend', vendor }

  // Client-side filtering (the list endpoint returns every vendor)
  const term = search.trim().toLowerCase();
  const visibleVendors = vendors.filter(v =>
    (!statusFilter || (v.status || '').toUpperCase() === statusFilter) &&
    (!term || [v.name, v.email, v.phone, v.taxCode].some(f => (f || '').toLowerCase().includes(term)))
  );

  const saveVendor = async (formData, url, method, successMessage, failMessage) => {
    setSubmitting(true);
    try {
      const res  = await fetch(url, { method, credentials: 'include', body: toFormData(formData) });
      const data = await res.json();
      if (res.ok && data.success) { window.showSuccess(successMessage); setModal(null); fetchVendors(); }
      else return data.errors || { _: data.message || failMessage };
    } catch { window.showError('Server connection error'); }
    finally   { setSubmitting(false); }
  };

  const handleCreate = (formData) => saveVendor(
    formData, `${import.meta.env.VITE_API_URL}/api/vendors`, 'POST',
    'Vendor created successfully', 'Failed to create vendor',
  );

  const handleUpdate = (formData) => saveVendor(
    formData, `${import.meta.env.VITE_API_URL}/api/vendors/${modal.vendor.id}`, 'PUT',
    'Vendor updated successfully', 'Failed to update vendor',
  );

  const handleDelete = async () => {
    if (!deleteTarget) return;
    setDeleting(true);
    try {
      const res = await vendorService.delete(deleteTarget.id);
      if (res.ok) { window.showSuccess('Vendor deleted successfully'); fetchVendors(); }
      else window.showError(res.data?.message || 'Failed to delete vendor');
    } catch { window.showError('Server connection error'); }
    finally  { setDeleting(false); setDeleteTarget(null); }
  };

  const handleApprove = async (vendor) => {
    try {
      const res = await vendorService.approve(vendor.id);
      if (res.ok) { window.showSuccess('Vendor approved successfully'); fetchVendors(); }
      else window.showError(res.data?.message || 'Failed to approve vendor');
    } catch { window.showError('Server connection error'); }
  };

  // Reject / suspend need a reason (entered in ReasonModal)
  const handleReasonSubmit = async (reason) => {
    const { type, vendor } = reasonAction;
    const action = type === 'reject' ? vendorService.reject : vendorService.suspend;
    const verb = type === 'reject' ? 'rejected' : 'suspended';
    try {
      const res = await action(vendor.id, reason);
      if (res.ok) { window.showSuccess(`Vendor ${verb} successfully`); fetchVendors(); }
      else window.showError(res.data?.message || `Failed to ${type} vendor`);
    } catch { window.showError('Server connection error'); }
    finally  { setReasonAction(null); }
  };

  return (
    <div className="admin-ui-page">
      <PageHeader
        eyebrow="Marketplace"
        eyebrowIcon="bi-shop"
        title="Vendors"
        description="Review vendor applications, manage their storefront details and suspend shops when needed."
        actions={
          <button type="button" className="admin-ui-btn admin-ui-btn--primary" onClick={() => setModal('create')}>
            <i className="bi bi-plus-lg"></i> New vendor
          </button>
        }
      />

      <div className="admin-ui-toolbar">
        <SearchBox value={search} onChange={setSearch} placeholder="Search by name, email, phone or tax code…" />
        <div className="admin-ui-segmented" role="group" aria-label="Status filter">
          {STATUS_FILTERS.map(([value, label]) => (
            <button
              key={label}
              type="button"
              className={statusFilter === value ? 'active' : ''}
              onClick={() => setStatusFilter(value)}
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
                <th>Vendor</th>
                <th>Phone</th>
                <th>Tax code</th>
                <th>Status</th>
                <th className="admin-ui-col-actions"><span className="visually-hidden">Actions</span></th>
              </tr>
            </thead>
            <tbody>
              {loading && vendors.length === 0 && [...Array(5)].map((_, i) => (
                <tr key={i} className="admin-ui-skeleton-row"><td colSpan={5}><span></span></td></tr>
              ))}
              {visibleVendors.map(vendor => (
                <tr key={vendor.id} onClick={() => setModal({ mode: 'edit', vendor })}>
                  <td>
                    <div className="admin-ui-user">
                      {vendor.logo
                        ? <img className="admin-vendors-logo" src={vendor.logo} alt="" />
                        : <Avatar user={{ name: vendor.name, email: vendor.email }} />}
                      <div>
                        <strong>{vendor.name}</strong>
                        <span>{vendor.email || 'No email'}</span>
                      </div>
                    </div>
                  </td>
                  <td className={vendor.phone ? '' : 'admin-ui-muted'}>{vendor.phone || '—'}</td>
                  <td>{vendor.taxCode ? <span className="admin-ui-mono admin-vendors-tax">{vendor.taxCode}</span> : <span className="admin-ui-muted">—</span>}</td>
                  <td><VendorStatus status={vendor.status} /></td>
                  <td className="admin-ui-col-actions" onClick={(e) => e.stopPropagation()}>
                    {vendor.status === 'PENDING' && (
                      <>
                        <button type="button" className="admin-ui-icon-btn admin-vendors-approve" title="Approve" onClick={() => handleApprove(vendor)}>
                          <i className="bi bi-check-circle"></i>
                        </button>
                        <button type="button" className="admin-ui-icon-btn admin-ui-icon-btn--danger" title="Reject" onClick={() => setReasonAction({ type: 'reject', vendor })}>
                          <i className="bi bi-x-circle"></i>
                        </button>
                      </>
                    )}
                    {vendor.status === 'ACTIVE' && (
                      <button type="button" className="admin-ui-icon-btn admin-vendors-suspend" title="Suspend" onClick={() => setReasonAction({ type: 'suspend', vendor })}>
                        <i className="bi bi-pause-circle"></i>
                      </button>
                    )}
                    <button type="button" className="admin-ui-icon-btn" title="Edit" onClick={() => setModal({ mode: 'edit', vendor })}>
                      <i className="bi bi-pencil"></i>
                    </button>
                    <button type="button" className="admin-ui-icon-btn admin-ui-icon-btn--danger" title="Delete" onClick={() => setDeleteTarget(vendor)}>
                      <i className="bi bi-trash3"></i>
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
          {error && <EmptyState icon="bi-exclamation-octagon" title={error} text="Check your connection and reload the page." />}
          {!loading && !error && visibleVendors.length === 0 && (
            <EmptyState
              icon="bi-shop"
              title="No vendors found"
              text={vendors.length ? 'Try another search or filter.' : 'Create the first vendor to get started.'}
            />
          )}
        </div>
      </div>

      {modal && (
        <Modal
          title={modal === 'create' ? 'New vendor' : 'Edit vendor'}
          subtitle={modal === 'create' ? 'Logo, name and email are required.' : modal.vendor.name}
          onClose={() => setModal(null)}
          width={880}
        >
          <div className="admin-vendors-form-scope">
            {modal === 'create'
              ? <VendorForm onSubmit={handleCreate} onCancel={() => setModal(null)} loading={submitting} />
              : <VendorForm key={modal.vendor.id} vendor={modal.vendor} onSubmit={handleUpdate} onCancel={() => setModal(null)} loading={submitting} />}
          </div>
        </Modal>
      )}

      {deleteTarget && (
        <ConfirmDialog
          title="Delete vendor"
          message={<>Are you sure you want to delete vendor <strong>{deleteTarget.name}</strong>?</>}
          busy={deleting}
          onConfirm={handleDelete}
          onClose={() => setDeleteTarget(null)}
        />
      )}

      {reasonAction && (
        <ReasonModal
          type={reasonAction.type}
          vendor={reasonAction.vendor}
          onSubmit={handleReasonSubmit}
          onClose={() => setReasonAction(null)}
        />
      )}
    </div>
  );
}

// ── Reject / suspend: asks for the reason (replaces window.prompt) ───────────
function ReasonModal({ type, vendor, onSubmit, onClose }) {
  const [reason, setReason] = useState('');
  const [busy, setBusy] = useState(false);
  const label = type === 'reject' ? 'Reject' : 'Suspend';

  const submit = async (e) => {
    e.preventDefault();
    if (!reason.trim()) return;
    setBusy(true);
    await onSubmit(reason);
  };

  return (
    <Modal
      title={`${label} vendor`}
      subtitle={vendor.name}
      onClose={onClose}
      width={460}
      footer={
        <>
          <button type="button" className="admin-ui-btn admin-ui-btn--ghost" onClick={onClose} disabled={busy}>Cancel</button>
          <button type="submit" form="admin-vendors-reason" className="admin-ui-btn admin-ui-btn--danger" disabled={busy || !reason.trim()}>
            {busy ? 'Working…' : label}
          </button>
        </>
      }
    >
      <form id="admin-vendors-reason" className="admin-ui-form admin-vendors-reason" onSubmit={submit}>
        <Field label={type === 'reject' ? 'Rejection reason' : 'Suspension reason'}>
          <textarea value={reason} onChange={(e) => setReason(e.target.value)} rows={4} required autoFocus />
        </Field>
      </form>
    </Modal>
  );
}
