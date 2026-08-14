// src/main.jsx
import ReactDOM from 'react-dom/client';
import AppRouter from './routes/AppRouter';
import { AuthProvider } from './contexts/AuthContext';
import { CartProvider } from './contexts/CartContext';

ReactDOM.createRoot(document.getElementById('root')).render(
  <AuthProvider>
    <CartProvider>
      <AppRouter />
    </CartProvider>
  </AuthProvider>
);