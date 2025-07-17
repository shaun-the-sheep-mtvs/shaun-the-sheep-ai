'use client';

import { useUserData } from '@/contexts/UserDataContext';
import { useAuth } from '@/contexts/AuthContext';

export const useSkinAnalysis = () => {
  const { checklist, guestChecklist, mbti, updateChecklist, updateGuestChecklist, setMbti } = useUserData();
  const { isGuest } = useAuth();

  // Get the relevant data based on user type
  const skinData = isGuest ? guestChecklist : checklist;

  // Helper function to get percentages
  const getPercentages = () => {
    if (!skinData) return { moisture: 0, oil: 0, sensitivity: 0, tension: 0 };
    
    return {
      moisture: skinData.moisture || 0,
      oil: skinData.oil || 0,
      sensitivity: skinData.sensitivity || 0,
      tension: skinData.tension || 0,
    };
  };

  // Helper function to get MBTI breakdown
  const getMBTIBreakdown = () => {
    return {
      moisture: mbti.charAt(0) === 'M' ? 'Moist' : 'Dry',
      oil: mbti.charAt(1) === 'O' ? 'Oily' : 'Balanced',
      sensitivity: mbti.charAt(2) === 'S' ? 'Sensitive' : 'Insensitive',
      tension: mbti.charAt(3) === 'T' ? 'Tight' : 'Loose',
    };
  };

  return {
    // Data
    skinData,
    mbti,
    percentages: getPercentages(),
    mbtiBreakdown: getMBTIBreakdown(),
    
    // Actions
    updateSkinData: isGuest ? updateGuestChecklist : updateChecklist,
    updateMBTI: setMbti,
    
    // Utilities
    isGuest,
    hasData: !!skinData,
  };
};