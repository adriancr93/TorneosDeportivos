package org.example.service;

import org.example.model.Partido;
import java.util.List;

public interface PartidoService {
    List<Partido> generarPartidos(String torneoId);
    boolean registrarResultado(String partidoId, int golesLocal, int golesVisitante);
    List<Partido> listarPartidosPorTorneo(String torneoId);
}
