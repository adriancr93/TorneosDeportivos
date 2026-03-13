package org.example.service;

import org.example.model.Equipo;
import org.example.model.Jugador;
import java.util.List;

public interface ReporteService {
    Equipo equipoConMasGoles(String torneoId);
    Jugador jugadorConMasGoles(String torneoId);
    List<String> historialTorneos();
}
