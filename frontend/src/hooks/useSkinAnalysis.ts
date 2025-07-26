'use client';

import { useUserData } from '@/contexts/UserDataContext';

export const useSkinAnalysis = () => {
  const { checklist, mbti, updateChecklist, setMbti } = useUserData();

  // Get the relevant data for authenticated users only
  const skinData = checklist;

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
    updateSkinData: updateChecklist,
    updateMBTI: setMbti,
    
    // Utilities
    hasData: !!skinData,
  };
};