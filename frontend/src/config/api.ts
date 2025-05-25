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
    },
    recommend: {
      base: `${API_BASE_URL}/api/recommend`,
    }
  }
};

export default apiConfig; 