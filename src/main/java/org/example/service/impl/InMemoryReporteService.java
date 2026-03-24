package org.example.service.impl;

import org.example.interfaces.IReporteService;
import org.example.model.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class InMemoryReporteService implements IReporteService {
    private final InMemoryEquipoService equipoService;
    private final InMemoryJugadorService jugadorService;
    private final InMemoryTorneoService torneoService;
    private final InMemoryPartidoService partidoService;

    public InMemoryReporteService(InMemoryEquipoService equipoService, InMemoryJugadorService jugadorService,
                                   InMemoryTorneoService torneoService, InMemoryPartidoService partidoService) {
        this.equipoService = equipoService;
        this.jugadorService = jugadorService;
        this.torneoService = torneoService;
        this.partidoService = partidoService;
    }

    @Override
    public Equipo equipoConMasGoles(String torneoId) {
        List<Partido> partidos = partidoService.listarPartidosPorTorneo(torneoId);
        java.util.Map<String, Integer> golesPorEquipo = new java.util.HashMap<>();

        for (Partido p : partidos) {
            if (p.getEstado() == PartidoEstado.JUGADO) {
                golesPorEquipo.merge(p.getEquipoLocalId(), p.getGolesLocal(), Integer::sum);
                golesPorEquipo.merge(p.getEquipoVisitanteId(), p.getGolesVisitante(), Integer::sum);
            }
        }

        return golesPorEquipo.entrySet().stream()
                .max(Comparator.comparingInt(java.util.Map.Entry::getValue))
                .map(e -> equipoService.buscarPorId(e.getKey()))
                .orElse(null);
    }

    @Override
    public Jugador jugadorConMasGoles(String torneoId) {
        return jugadorService.listarTodos().stream()
                .max(Comparator.comparingInt(Jugador::getGoles))
                .orElse(null);
    }

    @Override
    public List<String> historialTorneos() {
        List<String> historial = new ArrayList<>();
        for (Torneo t : torneoService.listarTorneos()) {
            historial.add("[" + t.getEstado() + "] " + t.getNombre() + " - " + t.getSede());
        }
        return historial;
    }
}
