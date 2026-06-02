// src/pages/admin/products/AdminProductDetailPage.jsx
import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { productService } from '../../../services/productService';
import { brandService } from '../../../services/brandService';
import { categoryService } from '../../../services/categoryService';
import { productService as productApi } from '../../../services/productService';
import defaultProductImage from '../../../assets/product.png';
import '../shared/AdminPage.css';
import './AdminProductDetailPage.css';

export default function AdminProductDetailPage() {
  useEffect(() => { document.title = 'Admin - Product Detail'; }, []);

  const { id } = useParams();
  const navigate = useNavigate();
  
  const [product, setProduct] = useState(null);
  const [brands, setBrands] = useState([]);
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        const [prodRes, brandRes, catRes] = await Promise.all([
          productService.getById(id),
          brandService.getAll(),
          categoryService.getAll(),
        ]);
        
        if (prodRes.ok && prodRes.data) {
          setProduct(prodRes.data);
        } else {
          setError('Product not found');
        }
        
        setBrands(brandRes.data?.data || []);
        setCategories(catRes.data?.data || []);
      } catch {
        setError('Failed to load data');
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [id]);

  const getBrandName = (product) => {
    if (product.brand?.name) return product.brand.name;
    const brand = brands.find((b) => b.id === product.brandId);
    return brand ? brand.name : '—';
  };

  const getCategoryNames = (product) => {
    if (product.categories && product.categories.length > 0) {
      return product.categories.map(cat => cat.name).join(', ');
    }
    if (product.categoryIds && product.categoryIds.length > 0) {
      return product.categoryIds
        .map(catId => categories.find(c => c.id === catId)?.name)
        .filter(Boolean)
        .join(', ');
    }
    return '—';
  };

  const getStatusBadge = (status) => {
    const isPublished = status === 'PUBLISHED';
    const label = status
      .split('_')
      .map((w) => w.charAt(0) + w.slice(1).toLowerCase())
      .join(' ');
    return (
      <span className={`admin-badge ${isPublished ? 'admin-badge-active' : 'admin-badge-inactive'}`}>
        {label}
      </span>
    );
  };

  const handleEdit = () => {
    navigate(`/admin/products/edit/${id}`);
  };

  const handleDelete = async () => {
    if (!window.confirm('Are you sure you want to delete this product?')) {
      return;
    }

    try {
      const res = await productApi.delete(id);
      if (res.ok) {
        window.showSuccess('Product deleted successfully');
        navigate('/admin/products');
      } else {
        window.showError(res.data?.message || 'Failed to delete product');
      }
    } catch {
      window.showError('Server connection error');
    }
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

  if (error || !product) {
    return (
      <div className="admin-page">
        <div className="admin-alert admin-alert-error">
          {error || 'Product not found'}
        </div>
        <button className="btn-admin-secondary" onClick={() => navigate('/admin/products')}>
          Back to Products
        </button>
      </div>
    );
  }

  const imageUrl = product.images && product.images.length > 0 ? product.images[0] : defaultProductImage;

  return (
    <div className="admin-page">
      <div className="admin-page-header">
        <h2 className="admin-page-title">📦 Product Detail</h2>
        <div className="admin-header-actions">
          <button className="btn-admin-secondary" onClick={() => navigate('/admin/products')}>
            Back to List
          </button>
          <button className="btn-admin-primary" onClick={handleEdit}>
            ✏️ Edit
          </button>
          <button className="btn-admin-danger" onClick={handleDelete}>
            🗑️ Delete
          </button>
        </div>
      </div>

      <div className="admin-product-detail-container">
        <div className="detail-section">
          <h3 className="detail-section-title">Product Information</h3>
          
          <div className="detail-grid">
            <div className="detail-item">
              <span className="detail-label">ID:</span>
              <span className="detail-value">{product.id}</span>
            </div>
            
            <div className="detail-item">
              <span className="detail-label">Name:</span>
              <span className="detail-value">{product.name}</span>
            </div>
            
            <div className="detail-item">
              <span className="detail-label">Code:</span>
              <span className="detail-value code">{product.code}</span>
            </div>
            
            <div className="detail-item">
              <span className="detail-label">Price:</span>
              <span className="detail-value price">${Number(product.price).toLocaleString('en-US')}</span>
            </div>
            
            <div className="detail-item">
              <span className="detail-label">Stock:</span>
              <span className="detail-value stock">{product.stock}</span>
            </div>
            
            <div className="detail-item">
              <span className="detail-label">Brand:</span>
              <span className="detail-value">{getBrandName(product)}</span>
            </div>
            
            <div className="detail-item full-width">
              <span className="detail-label">Categories:</span>
              <span className="detail-value">{getCategoryNames(product)}</span>
            </div>
            
            <div className="detail-item full-width">
              <span className="detail-label">Status:</span>
              <span className="detail-value">{getStatusBadge(product.status)}</span>
            </div>
            
            <div className="detail-item full-width">
              <span className="detail-label">Description:</span>
              <span className="detail-value description">{product.description || '—'}</span>
            </div>
          </div>
        </div>

        <div className="detail-section">
          <h3 className="detail-section-title">Product Images</h3>
          
          <div className="image-gallery">
            {product.images && product.images.length > 0 ? (
              product.images.map((img, idx) => (
                <div key={idx} className="image-item">
                  <img src={img} alt={`${product.name} ${idx + 1}`} />
                </div>
              ))
            ) : (
              <div className="no-images">
                <span className="admin-no-image">No images</span>
              </div>
            )}
          </div>
        </div>

        <div className="detail-section">
          <h3 className="detail-section-title">Timestamps</h3>
          
          <div className="detail-grid">
            <div className="detail-item">
              <span className="detail-label">Created At:</span>
              <span className="detail-value">
                {new Date(product.createdAt).toLocaleString()}
              </span>
            </div>
            
            <div className="detail-item">
              <span className="detail-label">Updated At:</span>
              <span className="detail-value">
                {new Date(product.updatedAt).toLocaleString()}
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
