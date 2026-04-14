// Auth types
export interface Usuario {
  usuarioId?: string;
  username: string;
  password?: string;
  fechaCreacion?: string;
}

export interface AuthResponse {
  success: boolean;
  message: string;
  username?: string;
  usuarioId?: string;
  token?: string;
}

export interface AuthRequest {
  username: string;
  password: string;
}

export interface Jugador {
  jugadorId: string;
  nombre: string;
  edad?: number;
  numero: number;
  posicion: string;
  equipoId?: string;
}

export interface Equipo {
  equipoId: string;
  nombre: string;
  ciudad: string;
  entrenador: string;
  anioFundacion?: number;
  jugadores?: Jugador[];
}

export interface Torneo {
  torneoId: string;
  nombre: string;
  sede?: string;
  fechaInicio: string;
  fechaFin: string;
  modalidad?: 'ELIMINATORIA' | 'LIGA' | string;
  equipos?: string[];
  cantidadEquipos?: number;
  estado?: string;
  campeonId?: string;
  subcampeonId?: string;
  tercerLugarId?: string;
  campeon?: string;
  subcampeon?: string;
  tercerLugar?: string;
}

export interface Partido {
  partidoId: string;
  torneoId: string;
  equipoLocal: string;
  equipoVisitante: string;
  equipoLocalId?: string;
  equipoVisitanteId?: string;
  golesLocal: number;
  golesVisitante: number;
  fecha: string;
  estado?: string;
  ronda?: string;
  estadio?: string;
  goleadores?: Record<string, number>;
  asistencias?: Record<string, number>;
}

export interface Estadistica {
  estadisticaId?: string;
  equipoId: string;
  partidos: number;
  goles: number;
  golespermitidos: number;
  puntos: number;
  ganados?: number;
  empatados?: number;
  perdidos?: number;
}

export interface GoleadorItem {
  jugadorId: string;
  nombre: string;
  equipo: string;
  goles: number;
  posicion?: string;
}

export interface AsistenciaItem {
  jugadorId: string;
  nombre: string;
  equipo: string;
  asistencias: number;
  posicion?: string;
}

export interface Standings {
  equipoId: string;
  nombre: string;
  partidos: number;
  puntos: number;
  ganados: number;
  empatados: number;
  perdidos: number;
  golesAFavor: number;
  golesEnContra: number;
  diferencia: number;
}

export interface ApiActionResponse<T> {
  success: boolean;
  message: string;
  torneo?: T;
  equipo?: T;
  jugador?: T;
  partidos?: Partido[];
  standings?: Standings[];
  goleadores?: GoleadorItem[];
  asistencias?: AsistenciaItem[];
}
