// src/contexts/CartContext.jsx
import React, { createContext, useState, useEffect } from 'react';
import { cartService } from '../services/cartService';

export const CartContext = createContext();

export function CartProvider({ children }) {
  const [cart, setCart] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchCart = async () => {
      try {
        const cartData = await cartService.getCart();
        if (cartData.data && cartData.data.cart) {
          setCart(cartData.data.cart);
        } else {
          setCart(null);
        }
      } catch (err) {
        setCart(null);
      } finally {
        setLoading(false);
      }
    };
    fetchCart();
  }, []);

  return (
    <CartContext.Provider value={{ cart, setCart, loading }}>
      {children}
    </CartContext.Provider>
  );
}
