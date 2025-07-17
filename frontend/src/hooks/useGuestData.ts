'use client';

import { useUserData } from '@/contexts/UserDataContext';
import { useAuth } from '@/contexts/AuthContext';

export const useGuestData = () => {
  const { guestChecklist, updateGuestChecklist, clearGuestData } = useUserData();
  const { isGuest } = useAuth();

  // Check if guest data exists and is valid
  const hasValidGuestData = () => {
    if (!guestChecklist) return false;
    
    // Check if data is less than 30 minutes old
    const thirtyMinutesAgo = Date.now() - (30 * 60 * 1000);
    return guestChecklist.timestamp > thirtyMinutesAgo;
  };

  // Get remaining time for guest session
  const getRemainingTime = () => {
    if (!guestChecklist) return 0;
    
    const thirtyMinutesInMs = 30 * 60 * 1000;
    const elapsed = Date.now() - guestChecklist.timestamp;
    const remaining = thirtyMinutesInMs - elapsed;
    
    return Math.max(0, remaining);
  };

  // Format remaining time as human readable
  const getFormattedRemainingTime = () => {
    const remaining = getRemainingTime();
    const minutes = Math.floor(remaining / (60 * 1000));
    const seconds = Math.floor((remaining % (60 * 1000)) / 1000);
    
    return `${minutes}:${seconds.toString().padStart(2, '0')}`;
  };

  return {
    // Data
    guestChecklist,
    
    // Status
    isGuest,
    hasValidData: hasValidGuestData(),
    remainingTime: getRemainingTime(),
    formattedRemainingTime: getFormattedRemainingTime(),
    
    // Actions
    updateGuestData: updateGuestChecklist,
    clearGuestData,
  };
};