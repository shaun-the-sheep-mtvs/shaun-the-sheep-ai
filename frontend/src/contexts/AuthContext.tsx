'use client';

import React, { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import { apiConfig } from '@/config/api';

export interface CurrentUser {
  id: string;
  username: string;
  email: string;
}

export interface LoginCredentials {
  username: string;
  password: string;
}

export interface AuthContextType {
  // State
  user: CurrentUser | null;
  isLoggedIn: boolean;
  isGuest: boolean;
  loading: boolean;
  error: string | null;
  
  // Actions
  login: (credentials: LoginCredentials) => Promise<void>;
  logout: () => void;
  refreshToken: () => Promise<void>;
  switchToGuest: () => Promise<void>;
  clearError: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

interface AuthProviderProps {
  children: ReactNode;
}

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
  const [user, setUser] = useState<CurrentUser | null>(null);
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [isGuest, setIsGuest] = useState(false);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Guest token functionality disabled
  const getGuestToken = async (): Promise<string | null> => {
    // Guest access is disabled
    return null;
  };

  // Helper function to validate user token
  const validateUserToken = async (token: string): Promise<CurrentUser | null> => {
    try {
      const response = await fetch(apiConfig.endpoints.auth.me, {
        headers: {
          'Authorization': `Bearer ${token}`,
        }
      });

      if (!response.ok) {
        throw new Error('Invalid token');
      }

      return await response.json();
    } catch (err) {
      console.error('Token validation failed:', err);
      return null;
    }
  };

  // Initialize authentication state
  useEffect(() => {
    const initializeAuth = async () => {
      try {
        // Check for app version and clear storage if needed
        const APP_VERSION = '2024-06-05-b';
        if (localStorage.getItem('app_version') !== APP_VERSION) {
          localStorage.clear();
          localStorage.setItem('app_version', APP_VERSION);
        }

        const existingToken = localStorage.getItem('accessToken');
        
        if (existingToken) {
          // Try to validate existing token
          const userData = await validateUserToken(existingToken);
          
          if (userData) {
            // Valid user token
            setUser(userData);
            setIsLoggedIn(true);
            setIsGuest(false);
          } else {
            // Invalid user token, clear and require login
            localStorage.removeItem('accessToken');
            localStorage.removeItem('refreshToken');
            setUser(null);
            setIsLoggedIn(false);
            setIsGuest(false);
          }
        } else {
          // No token, user must login
          setUser(null);
          setIsLoggedIn(false);
          setIsGuest(false);
        }
      } catch (err) {
        console.error('Auth initialization error:', err);
        setError('Failed to initialize authentication');
      } finally {
        setLoading(false);
      }
    };

    initializeAuth();
  }, []);

  // Login function
  const login = async (credentials: LoginCredentials): Promise<void> => {
    setLoading(true);
    setError(null);

    try {
      const response = await fetch(apiConfig.endpoints.auth.login, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(credentials),
      });

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText || 'Login failed');
      }

      const { accessToken, refreshToken } = await response.json();
      
      // Store tokens
      localStorage.setItem('accessToken', accessToken);
      localStorage.setItem('refreshToken', refreshToken);

      // Validate and set user
      const userData = await validateUserToken(accessToken);
      if (userData) {
        setUser(userData);
        setIsLoggedIn(true);
        setIsGuest(false);
      } else {
        throw new Error('Failed to get user data after login');
      }
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : 'Login failed';
      setError(errorMessage);
      throw err;
    } finally {
      setLoading(false);
    }
  };

  // Logout function
  const logout = (): void => {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    setUser(null);
    setIsLoggedIn(false);
    setIsGuest(false);
    setError(null);
    // No guest mode - user must login again
  };

  // Refresh token function
  const refreshToken = async (): Promise<void> => {
    const refreshTokenValue = localStorage.getItem('refreshToken');
    
    if (!refreshTokenValue) {
      logout();
      return;
    }

    try {
      const response = await fetch(apiConfig.endpoints.auth.refresh, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ refreshToken: refreshTokenValue }),
      });

      if (!response.ok) {
        throw new Error('Token refresh failed');
      }

      const { accessToken } = await response.json();
      localStorage.setItem('accessToken', accessToken);
      
      // Validate new token
      const userData = await validateUserToken(accessToken);
      if (userData) {
        setUser(userData);
      }
    } catch (err) {
      console.error('Token refresh error:', err);
      logout();
    }
  };

  // Switch to guest mode - disabled
  const switchToGuest = async (): Promise<void> => {
    // Guest mode is disabled
    setUser(null);
    setIsLoggedIn(false);
    setIsGuest(false);
  };

  // Clear error
  const clearError = (): void => {
    setError(null);
  };

  const value: AuthContextType = {
    user,
    isLoggedIn,
    isGuest,
    loading,
    error,
    login,
    logout,
    refreshToken,
    switchToGuest,
    clearError,
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
};