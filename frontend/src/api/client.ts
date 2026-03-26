import axios from 'axios';
import type { AuthRequest, AuthResponse, Equipo, Torneo, Standings, GoleadorItem, Partido } from '../types';

const API_BASE_URL = 'http://localhost:8080/api';

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Authentication
export const authApi = {
  register: async (username: string, password: string): Promise<AuthResponse> => {
    const response = await apiClient.post<AuthResponse>('/auth/register', {
      username,
      password,
    } as AuthRequest);
    return response.data;
  },

  login: async (username: string, password: string): Promise<AuthResponse> => {
    const response = await apiClient.post<AuthResponse>('/auth/login', {
      username,
      password,
    } as AuthRequest);
    return response.data;
  },
};

// Equipos
export const equiposApi = {
  getAll: async (): Promise<Equipo[]> => {
    const response = await apiClient.get<Equipo[]>('/equipos');
    return response.data;
  },
};

// Torneos
export const torneosApi = {
  getAll: async (): Promise<Torneo[]> => {
    const response = await apiClient.get<Torneo[]>('/torneos');
    return response.data;
  },
};

// Partidos
export const partidosApi = {
  getAll: async (): Promise<Partido[]> => {
    const response = await apiClient.get<Partido[]>('/partidos');
    return response.data;
  },
};

// Standings
export const standingsApi = {
  getAll: async (): Promise<Standings[]> => {
    const response = await apiClient.get<Standings[]>('/standings');
    return response.data;
  },
};

// Goleadores
export const goleadoresApi = {
  getAll: async (): Promise<GoleadorItem[]> => {
    const response = await apiClient.get<GoleadorItem[]>('/goleadores');
    return response.data;
  },
};

export default apiClient;
