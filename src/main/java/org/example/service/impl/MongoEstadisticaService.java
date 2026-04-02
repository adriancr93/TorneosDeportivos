package org.example.service.impl;

import org.example.interfaces.IEstadisticaService;
import org.example.model.*;
import org.example.repository.EstadisticaRepository;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class MongoEstadisticaService implements IEstadisticaService {
    private final EstadisticaRepository repo;
    private final MongoPartidoService partidoService;
    private final MongoJugadorService jugadorService;

    public MongoEstadisticaService(EstadisticaRepository repo, MongoPartidoService partidoService, MongoJugadorService jugadorService) {
        this.repo = repo;
        this.partidoService = partidoService;
        this.jugadorService = jugadorService;
    }

    @Override
    public Estadistica generarEstadisticas(String torneoId) {
        List<Partido> partidos = partidoService.listarPartidosPorTorneo(torneoId);
        Map<String, int[]> stats = new HashMap<>(); // [puntos, gf, gc, pj]
        Map<String, Integer> golesPorJugador = new HashMap<>();

        for (Partido p : partidos) {
            if (p.getEstado() == PartidoEstado.JUGADO) {
                stats.computeIfAbsent(p.getEquipoLocalId(), k -> new int[4]);
                stats.computeIfAbsent(p.getEquipoVisitanteId(), k -> new int[4]);

                int[] local = stats.get(p.getEquipoLocalId());
                int[] visit = stats.get(p.getEquipoVisitanteId());

                local[1] += p.getGolesLocal();
                local[2] += p.getGolesVisitante();
                local[3]++;
                visit[1] += p.getGolesVisitante();
                visit[2] += p.getGolesLocal();
                visit[3]++;

                if (p.getGolesLocal() > p.getGolesVisitante()) {
                    local[0] += 3;
                } else if (p.getGolesLocal() < p.getGolesVisitante()) {
                    visit[0] += 3;
                } else {
                    local[0] += 1;
                    visit[0] += 1;
                }

                for (Map.Entry<String, Integer> gol : p.getGolesPorJugador().entrySet()) {
                    golesPorJugador.merge(gol.getKey(), gol.getValue(), Integer::sum);
                }
            }
        }

        List<TablaPosicionItem> tabla = stats.entrySet().stream()
                .map(e -> new TablaPosicionItem(e.getKey(), e.getValue()[0], e.getValue()[1], e.getValue()[2], e.getValue()[3]))
                .sorted((a, b) -> {
                    if (b.getPuntos() != a.getPuntos()) return b.getPuntos() - a.getPuntos();
                    return (b.getGolesFavor() - b.getGolesContra()) - (a.getGolesFavor() - a.getGolesContra());
                })
                .collect(Collectors.toList());

        List<GoleadorItem> goleadores = golesPorJugador.entrySet().stream()
            .filter(entry -> entry.getValue() > 0 && jugadorService.buscarPorId(entry.getKey()) != null)
            .map(entry -> new GoleadorItem(entry.getKey(), entry.getValue()))
                .sorted((a, b) -> b.getGoles() - a.getGoles())
                .collect(Collectors.toList());

        Estadistica est = new Estadistica();
        est.setId(UUID.randomUUID().toString().substring(0, 8));
        est.setTorneoId(torneoId);
        est.setFechaGeneracion(LocalDate.now().toString());
        est.setTabla(tabla);
        est.setGoleadores(goleadores);
        repo.save(est);
        return est;
    }

    @Override
    public Estadistica visualizarEstadisticas(String torneoId) {
        return repo.findByTorneoId(torneoId);
    }
}
