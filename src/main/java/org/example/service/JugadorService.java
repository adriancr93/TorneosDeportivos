package org.example.service;

import org.example.model.Jugador;
import java.util.List;

public interface JugadorService {
    Jugador registrarJugador(Jugador jugador);
    boolean asociarJugadorAEquipo(String jugadorId, String equipoId);
    List<Jugador> listarJugadoresPorEquipo(String equipoId);
}
