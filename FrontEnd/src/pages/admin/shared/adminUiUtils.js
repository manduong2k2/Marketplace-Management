// Non-component helpers shared by admin pages (kept apart from AdminUi.jsx for Fast Refresh)
import { useCallback, useEffect, useState } from 'react';

/**
 * Runs `fetcher` whenever `key` changes (key = serialized query params).
 * - `loading` is derived (no synchronous setState inside the effect)
 * - stale responses are ignored when the key changes mid-request (fast typing in search)
 * - the previous response stays visible while the next one loads
 * - `reload()` refetches with the same params (after a create/update/delete)
 */
export function useApiQuery(fetcher, key) {
  const [reloadToken, setReloadToken] = useState(0);
  const fullKey = `${key}#${reloadToken}`;
  const [state, setState] = useState({ key: null, response: null });

  useEffect(() => {
    let cancelled = false;
    fetcher()
      .then(response => { if (!cancelled) setState({ key: fullKey, response }); })
      .catch(() => { if (!cancelled) setState({ key: fullKey, response: null }); });
    return () => { cancelled = true; };
    // `fetcher` is a new function every render; `key` captures everything it depends on
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [fullKey]);

  const reload = useCallback(() => setReloadToken(n => n + 1), []);
  return { response: state.response, loading: state.key !== fullKey, reload };
}

export const PAGE_SIZE = 10;

/** Value that only updates after `delay` ms without changes (search inputs). */
export function useDebounce(value, delay = 350) {
  const [debounced, setDebounced] = useState(value);
  useEffect(() => {
    const timer = setTimeout(() => setDebounced(value), delay);
    return () => clearTimeout(timer);
  }, [value, delay]);
  return debounced;
}

export const formatDate = (value) => {
  if (!value) return '—';
  const date = new Date(value);
  return Number.isNaN(date.getTime())
    ? '—'
    : date.toLocaleDateString('en-GB', { day: '2-digit', month: 'short', year: 'numeric' });
};

export const formatDateTime = (value) => {
  if (!value) return '—';
  const date = new Date(value);
  return Number.isNaN(date.getTime())
    ? '—'
    : date.toLocaleString('en-GB', { day: '2-digit', month: 'short', year: 'numeric', hour: '2-digit', minute: '2-digit' });
};

export const formatCurrency = (value) =>
  value === null || value === undefined || Number.isNaN(Number(value))
    ? '—'
    : Number(value).toLocaleString('en-US', { style: 'currency', currency: 'USD' });

// Splits a failed save response into inline field errors + a general message
export const readErrors = (response) => ({
  fields: response?.data?.errors || {},
  message: response?.data?.errors ? null : (response?.data?.message || 'Something went wrong'),
});
