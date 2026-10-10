// src/pages/auth/profile/ProfilePage.jsx
import { useContext, useEffect, useRef, useState } from 'react';
import { AuthContext } from '../../../contexts/AuthContext';
import { authService } from '../../../services/authService';
import { showSuccess, showError } from '../../../components/master/popup';
import { Page, PageHeader, Card } from '../../../components/ui/Ui';
import { formatDate } from '../../../components/ui/uiUtils';
import PersonalInfoCard from './PersonalInfoCard';
import AddressBookCard from './AddressBookCard';
import ChangePasswordCard from './ChangePasswordCard';
import './ProfilePage.css';

const SECTIONS = [
  { id: 'personal', icon: 'bi-person', label: 'Personal information' },
  { id: 'addresses', icon: 'bi-geo-alt', label: 'Delivery addresses' },
  { id: 'security', icon: 'bi-shield-lock', label: 'Password & security' },
];

function ProfileAvatar({ user, size = 96 }) {
  if (user?.avatar) {
    return <img className="profile-photo" src={user.avatar} alt="" style={{ width: size, height: size }} referrerPolicy="no-referrer" />;
  }
  const label = (user?.name || user?.email || '?').trim();
  const initials = label.split(/\s+/).slice(0, 2).map(w => w[0]).join('').toUpperCase();
  return (
    <span className="profile-photo profile-photo--initials" style={{ width: size, height: size, fontSize: size * 0.36 }} aria-hidden="true">
      {initials}
    </span>
  );
}

export default function ProfilePage() {
  useEffect(() => {
    document.title = 'My Store - Profile';
  }, []);

  const { user, setUser } = useContext(AuthContext);
  const fileInputRef = useRef(null);
  const [uploading, setUploading] = useState(false);

  const reloadProfile = async () => {
    const res = await authService.profile();
    if (res.ok) setUser(res.data.data);
  };

  const handleAvatarChange = async (e) => {
    const file = e.target.files?.[0];
    e.target.value = '';
    if (!file) return;
    if (!file.type.startsWith('image/')) {
      showError('Please choose an image file', 'Avatar');
      return;
    }

    setUploading(true);
    try {
      const fd = new FormData();
      fd.append('avatar', file);
      const res = await authService.updateProfile(fd);
      if (res.ok) {
        await reloadProfile();
        showSuccess('Your photo has been updated', 'Avatar');
      } else {
        showError(res.data?.message || 'Could not update your photo', 'Avatar');
      }
    } catch {
      showError('Could not update your photo. Please try again.', 'Connection Error');
    } finally {
      setUploading(false);
    }
  };

  if (!user) return null;

  return (
    <Page>
      <PageHeader
        eyebrow="Account"
        eyebrowIcon="bi-person-circle"
        title="My profile"
        description="Manage your personal information, delivery addresses and password."
      />

      <div className="ui-split ui-split--left">
        <aside className="ui-stack ui-sticky">
          <Card bodyless className="profile-hero">
            <div className="profile-hero-body">
              <div className="profile-avatar-wrap">
                <ProfileAvatar user={user} />
                <button
                  type="button"
                  className="profile-avatar-btn"
                  onClick={() => fileInputRef.current?.click()}
                  disabled={uploading}
                  aria-label="Change photo"
                  title="Change photo"
                >
                  <i className={`bi ${uploading ? 'bi-hourglass-split' : 'bi-camera'}`}></i>
                </button>
                <input ref={fileInputRef} type="file" accept="image/*" hidden onChange={handleAvatarChange} />
              </div>
              <h2>{user.name || 'No name yet'}</h2>
              <p className="ui-muted">{user.email}</p>
              {user.createdAt && (
                <span className="ui-chip"><i className="bi bi-calendar3 me-1"></i>Member since {formatDate(user.createdAt)}</span>
              )}
            </div>
            <nav className="profile-nav" aria-label="Profile sections">
              {SECTIONS.map(section => (
                <a key={section.id} href={`#${section.id}`}>
                  <i className={`bi ${section.icon}`}></i> {section.label}
                  <i className="bi bi-chevron-right profile-nav-arrow"></i>
                </a>
              ))}
            </nav>
          </Card>
        </aside>

        <div className="ui-stack">
          <PersonalInfoCard user={user} onSaved={reloadProfile} />
          <AddressBookCard />
          <ChangePasswordCard />
        </div>
      </div>
    </Page>
  );
}
