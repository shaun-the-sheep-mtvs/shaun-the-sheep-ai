'use client';

import { apiConfig } from '@/config/api';

// Types
interface ApiResponse<T = any> {
  data: T;
  success: boolean;
  message?: string;
}

interface ApiError {
  message: string;
  status?: number;
  code?: string;
}

class ApiService {
  private baseURL: string;

  constructor(baseURL: string = apiConfig.baseURL) {
    this.baseURL = baseURL;
  }

  // Get access token from localStorage
  private getAccessToken(): string | null {
    if (typeof window === 'undefined') return null;
    return localStorage.getItem('accessToken');
  }

  // Create request headers
  private createHeaders(customHeaders: Record<string, string> = {}): Record<string, string> {
    const headers: Record<string, string> = {
      'Content-Type': 'application/json',
      ...customHeaders,
    };

    const token = this.getAccessToken();
    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }

    return headers;
  }

  // Handle API responses
  private async handleResponse<T>(response: Response): Promise<T> {
    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}));
      const error: ApiError = {
        message: errorData.message || `HTTP error! status: ${response.status}`,
        status: response.status,
        code: errorData.code,
      };
      throw error;
    }

    // Handle different response types
    const contentType = response.headers.get('content-type');
    if (contentType && contentType.includes('application/json')) {
      return await response.json();
    }
    
    return await response.text() as T;
  }

  // Generic request method
  private async request<T>(
    endpoint: string,
    options: RequestInit = {}
  ): Promise<T> {
    const url = endpoint.startsWith('http') ? endpoint : `${this.baseURL}${endpoint}`;
    
    const config: RequestInit = {
      ...options,
      headers: this.createHeaders(options.headers as Record<string, string>),
    };

    try {
      const response = await fetch(url, config);
      return await this.handleResponse<T>(response);
    } catch (error) {
      if (error instanceof Error) {
        throw error;
      }
      throw new Error('Network error occurred');
    }
  }

  // HTTP Methods
  async get<T>(endpoint: string, params?: Record<string, string>): Promise<T> {
    const url = params 
      ? `${endpoint}?${new URLSearchParams(params).toString()}`
      : endpoint;
    
    return this.request<T>(url, { method: 'GET' });
  }

  async post<T>(endpoint: string, data?: any): Promise<T> {
    return this.request<T>(endpoint, {
      method: 'POST',
      body: data ? JSON.stringify(data) : undefined,
    });
  }

  async put<T>(endpoint: string, data?: any): Promise<T> {
    return this.request<T>(endpoint, {
      method: 'PUT',
      body: data ? JSON.stringify(data) : undefined,
    });
  }

  async delete<T>(endpoint: string): Promise<T> {
    return this.request<T>(endpoint, { method: 'DELETE' });
  }

  // Auth specific methods
  async login(credentials: { email: string; password: string }) {
    return this.post(apiConfig.endpoints.auth.login, credentials);
  }

  async signup(userData: any) {
    return this.post(apiConfig.endpoints.auth.signup, userData);
  }

  async refreshToken(refreshToken: string) {
    return this.post(apiConfig.endpoints.auth.refresh, { refreshToken });
  }

  async getMe() {
    return this.get(apiConfig.endpoints.auth.me);
  }


  // Checklist methods
  async getChecklist() {
    return this.get(apiConfig.endpoints.checklist.base);
  }

  async submitChecklist(data: any) {
    return this.post(apiConfig.endpoints.checklist.base, data);
  }


  async getMBTI() {
    return this.get(apiConfig.endpoints.checklist.mbti);
  }

  // Product methods
  async getRandomProducts() {
    return this.get(apiConfig.endpoints.recommend.random);
  }

  async getUserRecommendations() {
    return this.get(apiConfig.endpoints.recommend.user);
  }

  async searchProducts(mode: string, word: string) {
    return this.get(apiConfig.endpoints.product.search(mode, word));
  }

  // Chat methods
  async getChatMessages() {
    return this.get(apiConfig.endpoints.chat.base);
  }

  async sendChatMessage(message: any) {
    return this.post(apiConfig.endpoints.chat.base, message);
  }

  // Routine methods
  async getRoutines() {
    return this.get(apiConfig.endpoints.routine.base);
  }

  async createRoutine(routine: any) {
    return this.post(apiConfig.endpoints.routine.create, routine);
  }

  async deleteRoutine(routineId: string) {
    return this.delete(`${apiConfig.endpoints.routine.delete}/${routineId}`);
  }

  // Deep recommendation methods
  async getDeepRecommendations(data: any) {
    return this.post(apiConfig.endpoints.deep.recommend, data);
  }

  async getAnalysisHistory() {
    return this.get(apiConfig.endpoints.deep.analysisHistory);
  }

  // Profile methods
  async getProfile() {
    return this.get(apiConfig.endpoints.profile.base);
  }

  async updateProfile(data: any) {
    return this.put(apiConfig.endpoints.profile.base, data);
  }

  // Skin type methods
  async getSkinTypes() {
    return this.get(apiConfig.endpoints.skinType.getAll);
  }

  async getSkinTypeById(id: number | string) {
    return this.get(apiConfig.endpoints.skinType.getById(id));
  }

  async getSkinTypeByEnglishName(englishName: string) {
    return this.get(apiConfig.endpoints.skinType.getByEnglishName(englishName));
  }

  // Naver API methods
  async getNaverData() {
    return this.get('/api/naver');
  }

  async getUserSkinData() {
    return this.get(apiConfig.endpoints.user.skinData);
  }
}

// Create and export singleton instance
export const apiService = new ApiService();

// Export types for use in components
export type { ApiResponse, ApiError };