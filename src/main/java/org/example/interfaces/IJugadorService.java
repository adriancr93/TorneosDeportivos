package org.example.interfaces;

import org.example.model.Jugador;
import java.util.List;

public interface IJugadorService {
    Jugador registrarJugador(Jugador jugador);
    boolean asociarJugadorAEquipo(String jugadorId, String equipoId);
    List<Jugador> listarJugadoresPorEquipo(String equipoId);
}
