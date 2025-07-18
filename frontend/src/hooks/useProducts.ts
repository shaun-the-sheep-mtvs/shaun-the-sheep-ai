'use client';

import { useUserData } from '@/contexts/UserDataContext';

export const useProducts = () => {
  const { products, refreshProducts, loading, error } = useUserData();

  // Filter products by category
  const getProductsByCategory = (category: string) => {
    return products.filter(product => 
      product.category && product.category.toLowerCase() === category.toLowerCase()
    );
  };

  // Get featured products (first 3)
  const getFeaturedProducts = () => {
    return products.slice(0, 3);
  };

  // Search products by name or description
  const searchProducts = (query: string) => {
    if (!query.trim()) return products;
    
    const lowerQuery = query.toLowerCase();
    return products.filter(product => 
      product.name.toLowerCase().includes(lowerQuery) ||
      product.description.toLowerCase().includes(lowerQuery)
    );
  };

  return {
    // Data
    products,
    loading,
    error,
    
    // Utilities
    getProductsByCategory,
    getFeaturedProducts,
    searchProducts,
    
    // Actions
    refreshProducts,
    
    // Status
    hasProducts: products.length > 0,
  };
};