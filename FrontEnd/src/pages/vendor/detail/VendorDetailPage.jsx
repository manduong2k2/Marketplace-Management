import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import VendorCard from '../../../components/vendor/card/VendorCard';
import ProductList from '../../../components/product/list/ProductList';
import { vendorService } from '../../../services/vendorService';
import './VendorDetailPage.css';

export default function VendorDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [vendor, setVendor] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (!id) return;
    async function fetchVendor() {
      try {
        setLoading(true);
        const res = await vendorService.getById(id);
        if (res.ok) {
          setVendor(res.data?.data ?? res.data);
        } else {
          setError(res.data?.message || 'Vendor not found');
        }
      } catch {
        setError('Failed to load vendor');
      } finally {
        setLoading(false);
      }
    }
    fetchVendor();
  }, [id]);

  if (loading) {
    return (
      <div className="vendor-detail-page">
        <div className="vendor-detail-loading">
          <p>Loading vendor...</p>
        </div>
      </div>
    );
  }

  if (error || !vendor) {
    return (
      <div className="vendor-detail-page">
        <div className="vendor-detail-error">
          <p>{error || 'Vendor not found'}</p>
        </div>
      </div>
    );
  }

  return (
    <div className="vendor-detail-page">
      {/* Back button */}
      <button className="vendor-detail-back" onClick={() => navigate(-1)}>
        ← Back
      </button>

      {/* Shared VendorCard */}
      <VendorCard vendor={vendor} />

      {/* Products from this vendor */}
      <div className="vendor-detail-products">
        <h3>Products from {vendor.name}</h3>
        <ProductList vendorId={vendor.id} />
      </div>
    </div>
  );
}
