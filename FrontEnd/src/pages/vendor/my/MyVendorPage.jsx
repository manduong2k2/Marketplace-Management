import { useState, useEffect } from 'react';
import VendorTab from './tabs/VendorTab';
import ProductsTab from './tabs/ProductsTab';
import RevenueTab from './tabs/RevenueTab';
import { vendorService } from '../../../services/vendorService';
import './MyVendorPage.css';

const TABS = [
  { key: 'vendor',   label: 'Vendor',    icon: '🏪' },
  { key: 'products', label: 'Products',  icon: '📦' },
  { key: 'revenue',  label: 'Revenue',   icon: '💰' },
];

export default function MyVendorPage() {
  const [activeTab, setActiveTab] = useState('vendor');
  const [vendor, setVendor] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function fetchMyVendor() {
      try {
        const res = await vendorService.getMyVendor();
        if (res.ok) {
          setVendor(res.data?.data ?? res.data);
        } else {
          setVendor(null);
        }
      } catch {
        setVendor(null);
      } finally {
        setLoading(false);
      }
    }
    fetchMyVendor();
  }, []);

  return (
    <div className="my-vendor-page">

      {/* ── Sidebar ── */}
      <aside className="my-vendor-sidebar">
        <p className="my-vendor-sidebar-title">My Store</p>
        {TABS.map((tab) => (
          <button
            key={tab.key}
            className={`my-vendor-nav-item ${activeTab === tab.key ? 'active' : ''}`}
            onClick={() => setActiveTab(tab.key)}
          >
            <span className="my-vendor-nav-icon">{tab.icon}</span>
            {tab.label}
          </button>
        ))}
      </aside>

      {/* ── Content ── */}
      <div className="my-vendor-content">
        {activeTab === 'vendor' && <VendorTab vendor={vendor} loading={loading} />}
        {activeTab === 'products' && <ProductsTab vendor={vendor} />}
        {activeTab === 'revenue' && <RevenueTab />}
      </div>
    </div>
  );
}
