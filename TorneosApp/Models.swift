import Foundation

// ─── Modelos que mapean el JSON del backend Java ───

struct Torneo: Identifiable, Decodable {
    let id: String
    let nombre: String
    let sede: String
    let fechaInicio: String
    let fechaFin: String
    let estado: String
}

struct Equipo: Identifiable, Decodable {
    let id: String
    let nombre: String
    let ciudad: String
    let entrenador: String
    let anioFundacion: Int
}

struct Standing: Identifiable, Decodable {
    var id: String { equipoId }
    let posicion: Int
    let equipoId: String
    let equipo: String
    let puntos: Int
    let partidosJugados: Int
    let golesFavor: Int
    let golesContra: Int
    let diferencia: Int
    let victorias: Int
    let empates: Int
    let derrotas: Int
}

struct Goleador: Identifiable, Decodable {
    var id: String { jugadorId }
    let jugadorId: String
    let nombre: String
    let goles: Int
    let equipo: String?
}

struct Partido: Identifiable, Decodable {
    let id: String
    let fecha: String
    let estado: String
    let local: String
    let visitante: String
    let golesLocal: Int
    let golesVisitante: Int
}
