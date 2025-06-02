// API Configuration for different environments
const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL || 'http://localhost:8080';

export const apiConfig = {
  baseURL: API_BASE_URL,
  endpoints: {
    auth: {
      login: `${API_BASE_URL}/api/auth/login`,
      signup: `${API_BASE_URL}/api/auth/signup`,
      refresh: `${API_BASE_URL}/api/auth/refresh`,
      me: `${API_BASE_URL}/api/auth/me`,
    },
    checklist: {
      base: `${API_BASE_URL}/api/checklist`,
      mbti: `${API_BASE_URL}/api/checklist/mbti`,
      guest: `${API_BASE_URL}/api/checklist/guest`,
    },
    routine: {
      base: `${API_BASE_URL}/api/routine`,
      existingall: `${API_BASE_URL}/api/routine/all-existing`,
      existing: `${API_BASE_URL}/api/routine/existing`,
      create: `${API_BASE_URL}/api/routine/create`,
      delete: `${API_BASE_URL}/api/routine/delete`,
    },
    recommend: {
      diagnoses: `${API_BASE_URL}/api/recommend/diagnoses`,
      random: `${API_BASE_URL}/api/recommend/random-recommendations`,
      user: `${API_BASE_URL}/api/recommend/user-recommendations`,
    },
    chat: {
      base: `${API_BASE_URL}/api/chat-messages`,
    },
    deep: {
      recommend: `${API_BASE_URL}/api/deep/recommend`,
      routineChange: `${API_BASE_URL}/api/deep/routine-change`,
      productRecommend: `${API_BASE_URL}/api/deep/product-recommend`,
      analysisHistory: `${API_BASE_URL}/api/deep/all-recommend`,
    },
    user: {
      skinData: `${API_BASE_URL}/api/user/skin-data`,
    },
    profile: {
      base: `${API_BASE_URL}/api/profile`,
    },
    guest: {
      create: `${API_BASE_URL}/api/guest/create`,
      get: `${API_BASE_URL}/api/guest/get`,
      update: `${API_BASE_URL}/api/guest/update`,
      delete: `${API_BASE_URL}/api/guest/delete`,
    },
    guest: {
      create: `${API_BASE_URL}/api/guest/create`,
      get: `${API_BASE_URL}/api/guest/get`,
      update: `${API_BASE_URL}/api/guest/update`,
      delete: `${API_BASE_URL}/api/guest/delete`,
    }
  }
};

export default apiConfig; 