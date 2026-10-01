import { useState, useEffect } from 'react';

// Shared by the master navbar and the admin layout so both use the same saved theme
const STORAGE_THEME = 'adminHMD.colorTheme';

function canStore() {
  try { localStorage.setItem('__t', '1'); localStorage.removeItem('__t'); return true; }
  catch { return false; }
}

function getPreferredTheme(storable) {
  const saved = storable ? localStorage.getItem(STORAGE_THEME) : '';
  if (saved === 'dark' || saved === 'light') return saved;
  if (window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches) return 'dark';
  return 'light';
}

export function useTheme() {
  const storable = canStore();
  const [theme, setTheme] = useState(() => getPreferredTheme(storable));

  useEffect(() => {
    document.documentElement.setAttribute('data-theme', theme);
    document.documentElement.setAttribute('data-bs-theme', theme);
    if (storable) localStorage.setItem(STORAGE_THEME, theme);
  }, [theme]);

  const toggleTheme = () => setTheme(t => (t === 'dark' ? 'light' : 'dark'));

  return { theme, toggleTheme };
}
