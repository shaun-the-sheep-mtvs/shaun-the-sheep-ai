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
    },
    routine: {
      base: `${API_BASE_URL}/api/routine`,
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
    }
  }
};

export default apiConfig; 