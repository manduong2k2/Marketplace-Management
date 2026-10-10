import { useCallback } from 'react';
import SearchableSelect from '../../master/input/select/SearchableSelect';
import { userAdminService } from '../../../services/identityService';
import './SearchableUserSelect.css';

const PAGE_SIZE = 20;

/**
 * User picker (admin): searches GET /api/users by email (the API also matches the name).
 * onChange receives { target: { name, value: userId } } like a native input.
 */
export default function SearchableUserSelect({ name = 'userId', value, onChange, error, disabled = false, placeholder = '-- Select owner (search by email) --' }) {
  // Stable identity: SearchableSelect re-runs its search effect when onSearch changes
  const handleSearch = useCallback(async (searchTerm) => {
    try {
      const res = await userAdminService.list({
        search: searchTerm.trim(),
        size: PAGE_SIZE,
        sortBy: 'email',
        sortOrder: 'asc',
        status: 'ACTIVE',
      });
      // Shown as "name — email" so two users with the same name can be told apart
      return (res.ok ? res.data?.data || [] : []).map(user => ({
        ...user,
        label: user.name ? `${user.name} — ${user.email}` : user.email,
      }));
    } catch {
      return [];
    }
  }, []);

  const handleChange = (e) => onChange({ target: { name, value: e.target.value } });

  const renderUser = (user) => (
    <div className="user-select-item">
      <span className="user-select-email">{user.email}</span>
      {user.name && <span className="user-select-name">{user.name}</span>}
    </div>
  );

  return (
    <SearchableSelect
      value={value}
      onChange={handleChange}
      onSearch={handleSearch}
      itemValueKey="id"
      itemLabelKey="label"
      renderItem={renderUser}
      placeholder={placeholder}
      error={error}
      disabled={disabled}
      debounceMs={400}
      clearable
    />
  );
}
