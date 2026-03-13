package org.example.interfaces;

import org.example.model.Partido;
import java.util.List;

public interface IPartidoService {
    List<Partido> generarPartidos(String torneoId);
    boolean registrarResultado(String partidoId, int golesLocal, int golesVisitante);
    List<Partido> listarPartidosPorTorneo(String torneoId);
}
