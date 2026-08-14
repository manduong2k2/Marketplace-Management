import { useNavigate } from 'react-router-dom';
import VendorCard from '../../../../components/vendor/card/VendorCard';

export default function VendorTab({ vendor, loading }) {
  const navigate = useNavigate();

  return (
    <>
      <div className="my-vendor-tab-header">
        <h2>My Vendor Store</h2>
        {vendor && (
          <button
            className="btn-edit-vendor"
            onClick={() => navigate('/vendor-edit')}
          >
            ✏️ Edit Store
          </button>
        )}
      </div>

      {loading && (
        <div className="my-vendor-loading">
          <p>Loading your store...</p>
        </div>
      )}

      {!loading && !vendor && (
        <div className="my-vendor-empty">
          <span className="my-vendor-empty-icon">🏪</span>
          <h3>You haven't registered a store yet. Click the button below to create one! 🚀</h3>
          <button
            className="btn-create-vendor"
            onClick={() => navigate('/vendor-create')}
          >
            + Create Store
          </button>
        </div>
      )}

      {!loading && vendor && (
        <VendorCard vendor={vendor} />
      )}
    </>
  );
}
