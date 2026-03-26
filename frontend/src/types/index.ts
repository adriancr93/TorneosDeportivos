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

// Equipo types
export interface Jugador {
  jugadorId: string;
  nombre: string;
  numero: number;
  posicion: string;
  equipoId?: string;
}

export interface Equipo {
  equipoId: string;
  nombre: string;
  ciudad: string;
  entrenador: string;
  jugadores?: Jugador[];
}

// Torneo types
export interface Torneo {
  torneoId: string;
  nombre: string;
  fechaInicio: string;
  fechaFin: string;
  equipos?: string[];
  estado?: string;
}

// Partido types
export interface Partido {
  partidoId: string;
  torneoId: string;
  equipoLocal: string;
  equipoVisitante: string;
  golesLocal: number;
  golesVisitante: number;
  fecha: string;
  estadio?: string;
}

// Estadística types
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
  equipoId: string;
  nombre: string;
  numeroGoles: number;
  posicion?: string;
}

// Standings/Tabla
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
