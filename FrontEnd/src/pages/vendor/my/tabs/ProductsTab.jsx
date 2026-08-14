import { useState, useEffect } from 'react';
import ProductTable from '../../../../components/product/table/ProductTable';
import ProductForm from '../../../../components/product/form/ProductForm';
import { brandService } from '../../../../services/brandService';
import { categoryService } from '../../../../services/categoryService';
import { productService } from '../../../../services/productService';

const API_URL = import.meta.env.VITE_API_URL;

export default function ProductsTab({ vendor }) {
  const [modal, setModal] = useState(null);
  const [brands, setBrands] = useState([]);
  const [categories, setCategories] = useState([]);
  const [statuses, setStatuses] = useState([]);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    const fetchFormData = async () => {
      try {
        const [brandRes, catRes, statusRes] = await Promise.all([
          brandService.getAll(),
          categoryService.getAll(),
          productService.getStatuses(),
        ]);
        setBrands(brandRes.data?.data || []);
        setCategories(catRes.data?.data || []);
        setStatuses(statusRes.data?.data || []);
      } catch {
        console.error('Failed to load form data');
      }
    };
    fetchFormData();
  }, []);

  const buildCreateFormData = (formData) => {
    const fd = new FormData();
    fd.append('name', formData.name);
    fd.append('code', formData.code);
    fd.append('description', formData.description || '');
    fd.append('price', formData.price);
    fd.append('stock', formData.stock);
    fd.append('brandId', formData.brandId);
    fd.append('status', formData.status);
    formData.categoryIds.forEach((id) => fd.append('categoryIds', id));
    if (formData.imageFiles && formData.imageFiles.length > 0) {
      formData.imageFiles.forEach((file) => fd.append('images', file));
    }
    return fd;
  };

  const handleCreate = async (formData) => {
    setSubmitting(true);
    try {
      const fd = buildCreateFormData(formData);
      const res = await fetch(`${API_URL}/api/products`, {
        method: 'POST',
        credentials: 'include',
        body: fd,
      });
      const data = await res.json();
      if (res.ok && data.success) {
        window.showSuccess('Product created successfully');
        setModal(null);
        // Refresh product list by forcing remount
        window.location.reload();
      } else {
        return data.errors || { _: data.message || 'Failed to create product' };
      }
    } catch {
      window.showError('Server connection error');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <>
      <div className="my-products-tab-header">
        <h2>My Products</h2>
        {vendor && (
          <button
            className="btn-add-product"
            onClick={() => setModal('create')}
          >
            + Add Product
          </button>
        )}
      </div>
      {vendor ? (
        <ProductTable vendorId={vendor.id} />
      ) : (
        <div className="my-vendor-empty">
          <span className="my-vendor-empty-icon">🏪</span>
          <h3>You haven't registered a store yet. Create one to add, manage and sell your products! 🚀</h3>
          <button
            className="btn-create-vendor"
            onClick={() => window.location.href = '/vendor-create'}
          >
            + Create Store
          </button>
        </div>
      )}

      {/* Create modal */}
      {modal === 'create' && (
        <div className="admin-modal-overlay" onClick={() => setModal(null)}>
          <div className="admin-modal" onClick={(e) => e.stopPropagation()}>
            <ProductForm
              brands={brands}
              categories={categories}
              statuses={statuses}
              onSubmit={handleCreate}
              onCancel={() => setModal(null)}
              loading={submitting}
            />
          </div>
        </div>
      )}
    </>
  );
}
