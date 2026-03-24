package org.example.service.impl;

import org.example.interfaces.IReporteService;
import org.example.model.*;

import java.util.*;
import java.util.stream.Collectors;

public class MongoReporteService implements IReporteService {
    private final MongoEquipoService equipoService;
    private final MongoJugadorService jugadorService;
    private final MongoTorneoService torneoService;
    private final MongoPartidoService partidoService;

    public MongoReporteService(MongoEquipoService equipoService, MongoJugadorService jugadorService,
                                MongoTorneoService torneoService, MongoPartidoService partidoService) {
        this.equipoService = equipoService;
        this.jugadorService = jugadorService;
        this.torneoService = torneoService;
        this.partidoService = partidoService;
    }

    @Override
    public Equipo equipoConMasGoles(String torneoId) {
        List<Partido> partidos = partidoService.listarPartidosPorTorneo(torneoId);
        Map<String, Integer> golesPorEquipo = new HashMap<>();

        for (Partido p : partidos) {
            if (p.getEstado() == PartidoEstado.JUGADO) {
                golesPorEquipo.merge(p.getEquipoLocalId(), p.getGolesLocal(), Integer::sum);
                golesPorEquipo.merge(p.getEquipoVisitanteId(), p.getGolesVisitante(), Integer::sum);
            }
        }

        return golesPorEquipo.entrySet().stream()
                .max(Comparator.comparingInt(Map.Entry::getValue))
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
        return torneoService.listarTorneos().stream()
                .map(t -> "[" + t.getEstado() + "] " + t.getNombre() + " - " + t.getSede())
                .collect(Collectors.toList());
    }
}
