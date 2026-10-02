// src/main.jsx
import ReactDOM from 'react-dom/client';
import { GoogleOAuthProvider } from '@react-oauth/google';
import AppRouter from './routes/AppRouter';
import { AuthProvider } from './contexts/AuthContext';
import { CartProvider } from './contexts/CartContext';
import { GOOGLE_CLIENT_ID } from './configs/constants';

ReactDOM.createRoot(document.getElementById('root')).render(
  // locale must be set here: GoogleLogin reads it from this provider (also loads the GSI script with ?hl=en)
  <GoogleOAuthProvider clientId={GOOGLE_CLIENT_ID} locale="en">
    <AuthProvider>
      <CartProvider>
        <AppRouter />
      </CartProvider>
    </AuthProvider>
  </GoogleOAuthProvider>
);
