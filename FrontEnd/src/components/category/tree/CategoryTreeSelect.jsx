// src/components/category/tree/CategoryTreeSelect.jsx
import { useState, useMemo } from 'react';
import './CategoryTreeSelect.css';

// Build tree from flat list (uses `children` if already present, else builds from parentId)
function buildTree(flat) {
  // If items already have children arrays (from API), use them directly for roots
  const hasChildren = flat.some((c) => Array.isArray(c.children));
  if (hasChildren) {
    return flat.filter((c) => !c.parentId);
  }
  const map = {};
  flat.forEach((c) => { map[c.id] = { ...c, children: [] }; });
  const roots = [];
  flat.forEach((c) => {
    if (c.parentId && map[c.parentId]) {
      map[c.parentId].children.push(map[c.id]);
    } else if (!c.parentId) {
      roots.push(map[c.id]);
    }
  });
  return roots;
}

// Recursive node
function CategoryNode({ node, selectedIds, onToggle, depth = 0 }) {
  const [expanded, setExpanded] = useState(false);
  const hasChildren = node.children && node.children.length > 0;
  const isChecked = selectedIds.includes(node.id);

  // Count how many descendants are selected
  const countSelected = (n) => {
    let count = selectedIds.includes(n.id) ? 1 : 0;
    (n.children || []).forEach((c) => { count += countSelected(c); });
    return count;
  };
  const selectedBelow = hasChildren ? countSelected(node) - (isChecked ? 1 : 0) : 0;

  return (
    <div className="cat-tree-node" style={{ '--depth': depth }}>
      <div className={`cat-tree-row ${isChecked ? 'selected' : ''}`}>
        {/* Indent */}
        {depth > 0 && (
          <span className="cat-tree-indent" style={{ width: depth * 16 + 'px' }} aria-hidden="true" />
        )}

        {/* Checkbox */}
        <label className="cat-tree-checkbox-label" onClick={(e) => e.stopPropagation()}>
          <input
            type="checkbox"
            checked={isChecked}
            onChange={() => onToggle(node.id)}
          />
        </label>

        {/* Name + expand toggle */}
        <span
          className="cat-tree-name"
          onClick={() => hasChildren && setExpanded((p) => !p)}
          role={hasChildren ? 'button' : undefined}
          tabIndex={hasChildren ? 0 : undefined}
          onKeyDown={(e) => hasChildren && e.key === 'Enter' && setExpanded((p) => !p)}
        >
          {node.name}
          {hasChildren && selectedBelow > 0 && (
            <span className="cat-tree-selected-badge">{selectedBelow}</span>
          )}
        </span>

        {/* Chevron */}
        {hasChildren && (
          <button
            type="button"
            className="cat-tree-chevron"
            onClick={() => setExpanded((p) => !p)}
            aria-label={expanded ? 'Collapse' : 'Expand'}
          >
            {expanded ? '▲' : '▼'}
          </button>
        )}
      </div>

      {/* Children */}
      {hasChildren && expanded && (
        <div className="cat-tree-children">
          {node.children.map((child) => (
            <CategoryNode
              key={child.id}
              node={child}
              selectedIds={selectedIds}
              onToggle={onToggle}
              depth={depth + 1}
            />
          ))}
        </div>
      )}
    </div>
  );
}

export default function CategoryTreeSelect({ categories = [], selectedIds = [], onChange }) {
  const tree = useMemo(() => buildTree(categories), [categories]);

  const handleToggle = (id) => {
    const next = selectedIds.includes(id)
      ? selectedIds.filter((x) => x !== id)
      : [...selectedIds, id];
    onChange(next);
  };

  if (tree.length === 0) return <p className="cat-tree-empty">No categories available</p>;

  return (
    <div className="cat-tree-container">
      {tree.map((node) => (
        <CategoryNode
          key={node.id}
          node={node}
          selectedIds={selectedIds}
          onToggle={handleToggle}
          depth={0}
        />
      ))}
    </div>
  );
}
