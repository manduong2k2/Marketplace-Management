// src/components/sidebar/Sidebar.jsx
import React from 'react';
import './Sidebar.css';

export default function Sidebar({ expanded }) {
  return (
    <aside className={`sidebar ${expanded ? 'expanded' : ''}`}>
      <ul>
        <li>Dashboard</li>
        <li>Orders</li>
        <li>Products</li>
        <li>Settings</li>
      </ul>
    </aside>
  );
}