// src/pages/admin/products/AdminProductCreatePage.jsx
import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { brandService } from '../../../services/brandService';
import { categoryService } from '../../../services/categoryService';
import { productService } from '../../../services/productService';
import ProductForm from '../../../components/product/form/ProductForm';
import '../shared/AdminPage.css';

const API_URL = import.meta.env.VITE_API_URL;

export default function AdminProductCreatePage() {
  useEffect(() => {
    document.title = 'Admin - Create Product';
  }, []);

  const navigate = useNavigate();
  const [brands, setBrands] = useState([]);
  const [categories, setCategories] = useState([]);
  const [statuses, setStatuses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);

  // Fetch brands, categories, statuses
  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        const [brandRes, catRes, statusRes] = await Promise.all([
          brandService.getAll(),
          categoryService.getAll(),
          productService.getStatuses(),
        ]);

        setBrands(brandRes.data?.data || []);
        setCategories(catRes.data?.data || []);
        setStatuses(statusRes.data?.data || []);
      } catch (err) {
        console.error('Failed to load data:', err);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  const buildFd = (formData) => {
    const fd = new FormData();
    ['name', 'description', 'brandId', 'status'].forEach(k =>
      fd.append(k, formData[k] ?? '')
    );
    formData.categoryIds?.forEach(id => fd.append('categoryIds', id));

    // Add options
    formData.options?.forEach((opt, idx) => {
      fd.append(`options[${idx}].tempId`, opt.tempId);
      fd.append(`options[${idx}].name`, opt.name);
      fd.append(`options[${idx}].value`, opt.value);
    });

    // Add variants with images
    formData.variants?.forEach((var_, idx) => {
      fd.append(`variants[${idx}].name`, var_.name);
      fd.append(`variants[${idx}].price`, var_.price);
      fd.append(`variants[${idx}].stock`, var_.stock);
      fd.append(`variants[${idx}].sku`, var_.sku);
      var_.optionIds?.forEach((optId, optIdx) => {
        fd.append(`variants[${idx}].optionIds[${optIdx}]`, optId);
      });

      // Add image files
      var_.images?.forEach((img) => {
        if (img.type === 'new' && img.file) {
          fd.append(`variants[${idx}].images`, img.file);
        }
      });
    });

    return fd;
  };

  const handleSubmit = async (formData) => {
    setSubmitting(true);
    try {
      const res = await fetch(`${API_URL}/api/products`, {
        method: 'POST',
        credentials: 'include',
        body: buildFd(formData),
      });

      const data = await res.json();

      if (res.ok && data.success) {
        window.showSuccess('Product created successfully');
        navigate('/admin/products');
      } else {
        return data.errors || { _: data.message || 'Failed to create product' };
      }
    } catch (err) {
      window.showError('Server connection error');
      console.error(err);
    } finally {
      setSubmitting(false);
    }
  };

  const handleCancel = () => {
    navigate('/admin/products');
  };

  if (loading) {
    return (
      <div className="admin-page">
        <div className="admin-loading">
          <div className="admin-spinner" />
          <span>Loading...</span>
        </div>
      </div>
    );
  }

  return (
    <div className="admin-page">
      <div className="admin-page-header">
        <h2 className="admin-page-title">➕ Create Product</h2>
      </div>

      <div style={{ maxWidth: '900px', margin: '0 auto' }}>
        <ProductForm
          brands={brands}
          categories={categories}
          statuses={statuses}
          onSubmit={handleSubmit}
          onCancel={handleCancel}
          loading={submitting}
        />
      </div>
    </div>
  );
}
