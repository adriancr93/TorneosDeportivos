package org.example.interfaces;

import org.example.model.Equipo;
import org.example.model.Jugador;
import java.util.List;

public interface IReporteService {
    Equipo equipoConMasGoles(String torneoId);
    Jugador jugadorConMasGoles(String torneoId);
    List<String> historialTorneos();
}
