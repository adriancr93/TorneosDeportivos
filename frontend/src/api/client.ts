import axios from 'axios';
import type {
  ApiActionResponse,
  AsistenciaItem,
  AuthRequest,
  AuthResponse,
  Equipo,
  GoleadorItem,
  Jugador,
  Partido,
  Standings,
  Torneo,
} from '../types';

const API_BASE_URL = 'http://localhost:8080/api';

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

const normalizeTorneo = (raw: Record<string, unknown>): Torneo => ({
  torneoId: String(raw.torneoId ?? raw.id ?? ''),
  nombre: String(raw.nombre ?? ''),
  sede: String(raw.sede ?? ''),
  fechaInicio: String(raw.fechaInicio ?? ''),
  fechaFin: String(raw.fechaFin ?? ''),
  modalidad: String(raw.modalidad ?? 'ELIMINATORIA'),
  estado: String(raw.estado ?? ''),
  equipos: (raw.equipos as string[] | undefined) ?? (raw.equipoIds as string[] | undefined) ?? [],
  cantidadEquipos: Number(raw.cantidadEquipos ?? ((raw.equipos as unknown[] | undefined)?.length ?? (raw.equipoIds as unknown[] | undefined)?.length ?? 0)),
  campeonId: raw.campeonId ? String(raw.campeonId) : undefined,
  subcampeonId: raw.subcampeonId ? String(raw.subcampeonId) : undefined,
  tercerLugarId: raw.tercerLugarId ? String(raw.tercerLugarId) : undefined,
  campeon: raw.campeon ? String(raw.campeon) : undefined,
  subcampeon: raw.subcampeon ? String(raw.subcampeon) : undefined,
  tercerLugar: raw.tercerLugar ? String(raw.tercerLugar) : undefined,
});

const normalizePartido = (raw: Record<string, unknown>): Partido => ({
  partidoId: String(raw.partidoId ?? raw.id ?? ''),
  torneoId: String(raw.torneoId ?? ''),
  equipoLocal: String(raw.equipoLocal ?? raw.local ?? ''),
  equipoVisitante: String(raw.equipoVisitante ?? raw.visitante ?? ''),
  equipoLocalId: raw.equipoLocalId ? String(raw.equipoLocalId) : undefined,
  equipoVisitanteId: raw.equipoVisitanteId ? String(raw.equipoVisitanteId) : undefined,
  golesLocal: Number(raw.golesLocal ?? -1),
  golesVisitante: Number(raw.golesVisitante ?? -1),
  fecha: String(raw.fecha ?? ''),
  estado: String(raw.estado ?? ''),
  ronda: raw.ronda ? String(raw.ronda) : undefined,
  goleadores: (raw.goleadores as Record<string, number> | undefined) ?? undefined,
  asistencias: (raw.asistencias as Record<string, number> | undefined) ?? undefined,
});

const normalizeStanding = (raw: Record<string, unknown>): Standings => ({
  equipoId: String(raw.equipoId ?? ''),
  nombre: String(raw.nombre ?? raw.equipo ?? ''),
  partidos: Number(raw.partidos ?? raw.partidosJugados ?? 0),
  puntos: Number(raw.puntos ?? 0),
  ganados: Number(raw.ganados ?? raw.victorias ?? 0),
  empatados: Number(raw.empatados ?? raw.empates ?? 0),
  perdidos: Number(raw.perdidos ?? raw.derrotas ?? 0),
  golesAFavor: Number(raw.golesAFavor ?? raw.golesFavor ?? 0),
  golesEnContra: Number(raw.golesEnContra ?? raw.golesContra ?? 0),
  diferencia: Number(raw.diferencia ?? 0),
});

const normalizeGoleador = (raw: Record<string, unknown>): GoleadorItem => ({
  jugadorId: String(raw.jugadorId ?? raw.id ?? ''),
  nombre: String(raw.nombre ?? ''),
  equipo: String(raw.equipo ?? ''),
  goles: Number(raw.goles ?? raw.numeroGoles ?? 0),
  posicion: raw.posicion ? String(raw.posicion) : undefined,
});

const normalizeAsistencia = (raw: Record<string, unknown>): AsistenciaItem => ({
  jugadorId: String(raw.jugadorId ?? raw.id ?? ''),
  nombre: String(raw.nombre ?? ''),
  equipo: String(raw.equipo ?? ''),
  asistencias: Number(raw.asistencias ?? 0),
  posicion: raw.posicion ? String(raw.posicion) : undefined,
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

  create: async (payload: Omit<Equipo, 'equipoId' | 'jugadores'>): Promise<ApiActionResponse<Equipo>> => {
    const response = await apiClient.post<ApiActionResponse<Equipo>>('/equipos', payload);
    return response.data;
  },
};

export const jugadoresApi = {
  getAll: async (equipoId?: string): Promise<Jugador[]> => {
    const response = await apiClient.get<Jugador[]>('/jugadores', { params: equipoId ? { equipoId } : undefined });
    return response.data;
  },

  create: async (payload: Omit<Jugador, 'jugadorId'>): Promise<ApiActionResponse<Jugador>> => {
    const response = await apiClient.post<ApiActionResponse<Jugador>>('/jugadores', payload);
    return response.data;
  },
};

export const torneosApi = {
  getAll: async (): Promise<Torneo[]> => {
    const response = await apiClient.get<Array<Record<string, unknown>>>('/torneos');
    return response.data.map(normalizeTorneo);
  },

  create: async (payload: { nombre: string; sede: string; fechaInicio: string; fechaFin: string; modalidad: 'ELIMINATORIA' | 'LIGA'; equipoIds: string[] }): Promise<ApiActionResponse<Torneo>> => {
    const response = await apiClient.post<ApiActionResponse<Torneo>>('/torneos', payload);
    return response.data;
  },

  simulate: async (torneoId: string): Promise<ApiActionResponse<Torneo>> => {
    const response = await apiClient.post<ApiActionResponse<Torneo>>('/torneos/simular', { torneoId });
    return response.data;
  },
};

export const partidosApi = {
  getAll: async (torneoId?: string): Promise<Partido[]> => {
    const response = await apiClient.get<Array<Record<string, unknown>>>('/partidos', { params: torneoId ? { torneoId } : undefined });
    return response.data.map(normalizePartido);
  },

  generate: async (torneoId: string): Promise<ApiActionResponse<Partido>> => {
    const response = await apiClient.post<ApiActionResponse<Partido>>('/partidos/generar', { torneoId });
    return response.data;
  },
};

export const standingsApi = {
  getAll: async (torneoId?: string): Promise<Standings[]> => {
    const response = await apiClient.get<Array<Record<string, unknown>>>('/standings', { params: torneoId ? { torneoId } : undefined });
    return response.data.map(normalizeStanding);
  },
};

export const goleadoresApi = {
  getAll: async (torneoId?: string): Promise<GoleadorItem[]> => {
    const response = await apiClient.get<Array<Record<string, unknown>>>('/goleadores', { params: torneoId ? { torneoId } : undefined });
    return response.data.map(normalizeGoleador);
  },
};

export const asistenciasApi = {
  getAll: async (torneoId?: string): Promise<AsistenciaItem[]> => {
    try {
      const response = await apiClient.get<Array<Record<string, unknown>>>('/asistencias', { params: torneoId ? { torneoId } : undefined });
      return response.data.map(normalizeAsistencia);
    } catch (error) {
      if (axios.isAxiosError(error) && error.response?.status === 404) {
        return [];
      }
      throw error;
    }
  },
};

export default apiClient;
