'use client';

import { useAuth } from '@/contexts/AuthContext';

export const useAuthActions = () => {
  const { login, logout, refreshToken, switchToGuest, clearError } = useAuth();

  return {
    login,
    logout,
    refreshToken,
    switchToGuest,
    clearError,
  };
};