'use client';

import React, { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import { useAuth } from './AuthContext';
import { apiConfig } from '@/config/api';

// Types
export interface CheckListResponse {
  id: number;
  moisture: number;
  oil: number;
  sensitivity: number;
  tension: number;
  createdAt: string;
}

export interface GuestChecklistData {
  moisture: number;
  oil: number;
  sensitivity: number;
  tension: number;
  troubles: string[];
  timestamp: number;
  mbtiCode?: string;
  mbtiId?: number;
  mbtiDescription?: string;
  skinTypeId?: number;
  skinTypeName?: string;
  skinTypeDescription?: string;
  concerns?: Array<{id: number, label: string, description: string}>;
}

export interface Product {
  name: string;
  description: string;
  imageUrl?: string;
  category?: string;
}

export interface UserDataContextType {
  // State
  checklist: CheckListResponse | null;
  guestChecklist: GuestChecklistData | null;
  mbti: string;
  products: Product[];
  loading: boolean;
  error: string | null;
  
  // Actions
  updateChecklist: (data: Partial<CheckListResponse>) => void;
  updateGuestChecklist: (data: GuestChecklistData) => void;
  refreshProducts: () => Promise<void>;
  clearGuestData: () => void;
  setMbti: (mbti: string) => void;
  clearError: () => void;
}

const UserDataContext = createContext<UserDataContextType | undefined>(undefined);

export const useUserData = () => {
  const context = useContext(UserDataContext);
  if (context === undefined) {
    throw new Error('useUserData must be used within a UserDataProvider');
  }
  return context;
};

// Default products for guests
const defaultProducts: Product[] = [
  { name: "수분 에센스", description: "진정효과 수분공급 민감피부용 에센스", category: "수분" },
  { name: "진정 세럼", description: "피부 진정케어 세럼 민감 피부용", category: "진정" },
  { name: "보습 크림", description: "저자극 수분 크림 민감 피부용", category: "보습" },
];

interface UserDataProviderProps {
  children: ReactNode;
}

export const UserDataProvider: React.FC<UserDataProviderProps> = ({ children }) => {
  const { user, isLoggedIn, isGuest, loading: authLoading } = useAuth();
  
  const [checklist, setChecklist] = useState<CheckListResponse | null>(null);
  const [guestChecklist, setGuestChecklist] = useState<GuestChecklistData | null>(null);
  const [mbti, setMbtiState] = useState<string>("default");
  const [products, setProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // Helper function to check and load guest data
  const checkGuestData = (): boolean => {
    const savedData = sessionStorage.getItem('guestChecklistData');
    if (savedData) {
      try {
        const data: GuestChecklistData = JSON.parse(savedData);
        // Check if data is less than 30 minutes old
        if (Date.now() - data.timestamp < 30 * 60 * 1000) {
          setGuestChecklist(data);
          return true;
        } else {
          // Clear expired data
          sessionStorage.removeItem('guestChecklistData');
          sessionStorage.removeItem('guestSignupData');
          setGuestChecklist(null);
        }
      } catch (err) {
        console.error('Error parsing guest data:', err);
        sessionStorage.removeItem('guestChecklistData');
      }
    }
    return false;
  };

  // Calculate MBTI from checklist data
  const calculateMBTI = (data: CheckListResponse | GuestChecklistData): string => {
    const { moisture, oil, sensitivity, tension } = data;
    const m = moisture >= 60 ? "M" : "D";
    const o = oil >= 60 ? "O" : "B";
    const s = sensitivity >= 60 ? "S" : "I";
    const t = tension >= 60 ? "T" : "L";
    return m + o + s + t;
  };

  // Fetch user checklist data
  const fetchUserChecklist = async () => {
    if (!isLoggedIn || authLoading) return;
    
    setLoading(true);
    setError(null);

    try {
      const token = localStorage.getItem('accessToken');
      if (!token) return;

      const response = await fetch(apiConfig.endpoints.checklist.base, {
        headers: { 'Authorization': `Bearer ${token}` }
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const data: CheckListResponse[] = await response.json();
      
      if (data.length === 0) {
        setError('저장된 체크리스트가 없습니다.');
        setChecklist(null);
      } else {
        const latestChecklist = data[0];
        setChecklist(latestChecklist);
        setMbtiState(calculateMBTI(latestChecklist));
      }
    } catch (err) {
      console.error('Error fetching checklist:', err);
      setError('체크리스트를 불러오는 데 실패했습니다.');
    } finally {
      setLoading(false);
    }
  };

  // Fetch MBTI data separately (for existing users)
  const fetchMBTI = async () => {
    if (!isLoggedIn || authLoading) return;

    try {
      const token = localStorage.getItem('accessToken');
      if (!token) return;

      const response = await fetch(apiConfig.endpoints.checklist.mbti, {
        headers: { 'Authorization': `Bearer ${token}` }
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const data = await response.text();
      
      if (!data || data.trim() === '' || data === 'null') {
        setError('MBTI 결과가 없습니다.');
      } else {
        setMbtiState(data.trim());
      }
    } catch (err) {
      console.error('Error fetching MBTI:', err);
      setError('MBTI 불러오는 데 실패했습니다.');
    }
  };

  // Fetch products
  const fetchProducts = async () => {
    if (isGuest) {
      setProducts(defaultProducts);
      return;
    }

    if (!isLoggedIn) return;

    try {
      const token = localStorage.getItem('accessToken');
      if (!token) return;

      const response = await fetch(apiConfig.endpoints.recommend.random, {
        headers: { 'Authorization': `Bearer ${token}` }
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const data = await response.json();
      
      const transformedProducts = data.map((product: any) => ({
        name: product.productName,
        description: `${product.recommendedType} - ${product.ingredients.join(', ')}`,
        imageUrl: product.imageUrl
      }));
      
      setProducts(transformedProducts);
    } catch (err) {
      console.error('Error fetching products:', err);
      setError('제품을 불러오는 데 실패했습니다.');
    }
  };

  // Initialize data based on auth state
  useEffect(() => {
    if (authLoading) return;

    if (isGuest) {
      const hasGuestData = checkGuestData();
      if (hasGuestData && guestChecklist) {
        setMbtiState(calculateMBTI(guestChecklist));
        setProducts(defaultProducts); // Only show products if guest has skin data
      } else {
        setProducts([]); // No products for guests without skin data
      }
    } else if (isLoggedIn) {
      fetchUserChecklist();
      fetchMBTI();
      fetchProducts();
    }
  }, [isLoggedIn, isGuest, authLoading]);

  // Update MBTI when guest checklist changes
  useEffect(() => {
    if (isGuest && guestChecklist) {
      setMbtiState(calculateMBTI(guestChecklist));
    }
  }, [isGuest, guestChecklist]);

  // Actions
  const updateChecklist = (data: Partial<CheckListResponse>) => {
    setChecklist(prev => prev ? { ...prev, ...data } : null);
  };

  const updateGuestChecklist = (data: GuestChecklistData) => {
    setGuestChecklist(data);
    sessionStorage.setItem('guestChecklistData', JSON.stringify(data));
    setMbtiState(calculateMBTI(data));
  };

  const refreshProducts = async () => {
    await fetchProducts();
  };

  const clearGuestData = () => {
    setGuestChecklist(null);
    sessionStorage.removeItem('guestChecklistData');
    sessionStorage.removeItem('guestSignupData');
  };

  const setMbti = (newMbti: string) => {
    setMbtiState(newMbti);
  };

  const clearError = () => {
    setError(null);
  };

  const value: UserDataContextType = {
    checklist,
    guestChecklist,
    mbti,
    products,
    loading,
    error,
    updateChecklist,
    updateGuestChecklist,
    refreshProducts,
    clearGuestData,
    setMbti,
    clearError,
  };

  return (
    <UserDataContext.Provider value={value}>
      {children}
    </UserDataContext.Provider>
  );
};