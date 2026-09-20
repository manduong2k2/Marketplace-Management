// src/pages/admin/products/AdminProductEditPage.jsx
import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { productService } from '../../../services/productService';
import { brandService } from '../../../services/brandService';
import { categoryService } from '../../../services/categoryService';
import ProductForm from '../../../components/product/form/ProductForm';
import '../shared/AdminPage.css';

const API_URL = import.meta.env.VITE_API_URL;

export default function AdminProductEditPage() {
  const { id } = useParams();
  const navigate = useNavigate();

  useEffect(() => {
    document.title = 'Admin - Edit Product';
  }, []);

  const [product, setProduct] = useState(null);
  const [brands, setBrands] = useState([]);
  const [categories, setCategories] = useState([]);
  const [statuses, setStatuses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState(null);

  // Fetch product detail and metadata
  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        setError(null);

        const [prodRes, brandRes, catRes, statusRes] = await Promise.all([
          productService.getById(id),
          brandService.getAll(),
          categoryService.getAll(),
          productService.getStatuses(),
        ]);

        if (!prodRes.ok || !prodRes.data) {
          setError('Product not found');
          return;
        }

        // Extract actual product data from nested response
        const productData = prodRes.data.data || prodRes.data;
        setProduct(productData);
        setBrands(brandRes.data?.data || []);
        setCategories(catRes.data?.data || []);
        setStatuses(statusRes.data?.data || []);
      } catch (err) {
        setError('Failed to load data');
        console.error(err);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [id]);

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

      // Handle variant images
      // Keep existing image URLs
      var_.images?.forEach((img, imgIdx) => {
        if (img.type === 'existing' && img.url) {
          fd.append(`variants[${idx}].imageUrls[${imgIdx}]`, img.url);
        }
      });

      // Add new image files
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
      const res = await fetch(`${API_URL}/api/products/${id}`, {
        method: 'PUT',
        credentials: 'include',
        body: buildFd(formData),
      });

      const data = await res.json();

      if (res.ok && data.success) {
        window.showSuccess('Product updated successfully');
        navigate('/admin/products');
      } else {
        // Return the full error response with message and errors
        return {
          message: data.message || 'Failed to update product',
          errors: data.errors || {}
        };
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

  if (error) {
    return (
      <div className="admin-page">
        <div className="admin-alert admin-alert-error">{error}</div>
        <button
          className="btn-admin-secondary"
          onClick={() => navigate('/admin/products')}
        >
          Back to Products
        </button>
      </div>
    );
  }

  return (
    <div className="admin-page">
      <div className="admin-page-header">
        <h2 className="admin-page-title">✏️ Edit Product</h2>
      </div>

      <div style={{margin: '0 auto' }}>
        {product && (
          <ProductForm
            product={product}
            brands={brands}
            categories={categories}
            statuses={statuses}
            onSubmit={handleSubmit}
            onCancel={handleCancel}
            loading={submitting}
          />
        )}
      </div>
    </div>
  );
}
