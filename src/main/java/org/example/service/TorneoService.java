package org.example.service;

import org.example.model.Torneo;
import java.util.List;

public interface TorneoService {
    Torneo crearTorneo(Torneo torneo);
    boolean agregarEquipoATorneo(String torneoId, String equipoId);
    boolean activarTorneo(String torneoId);
    boolean finalizarTorneo(String torneoId);
    List<Torneo> listarTorneos();
}
