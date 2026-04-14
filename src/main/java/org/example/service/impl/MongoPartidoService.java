package org.example.service.impl;

import org.example.interfaces.IPartidoService;
import org.example.model.Jugador;
import org.example.model.ModalidadTorneo;
import org.example.model.Partido;
import org.example.model.PartidoEstado;
import org.example.model.Torneo;
import org.example.repository.PartidoRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class MongoPartidoService implements IPartidoService {
    private final PartidoRepository repo;
    private final MongoTorneoService torneoService;
    private MongoJugadorService jugadorService;

    public MongoPartidoService(PartidoRepository repo, MongoTorneoService torneoService) {
        this.repo = repo;
        this.torneoService = torneoService;
    }

    public void setJugadorService(MongoJugadorService jugadorService) {
        this.jugadorService = jugadorService;
    }

    @Override
    public List<Partido> generarPartidos(String torneoId) {
        Torneo torneo = torneoService.buscarPorId(torneoId);
        if (torneo == null || torneo.getEquipoIds() == null || torneo.getEquipoIds().size() < 2) {
            return new ArrayList<>();
        }
        List<Partido> existentes = repo.findByTorneoId(torneoId);
        if (!existentes.isEmpty()) {
            return existentes;
        }

        if (torneo.getModalidad() == ModalidadTorneo.LIGA) {
            return generarPartidosLiga(torneoId, torneo.getEquipoIds());
        }

        return generarPartidosEliminatoria(torneoId, torneo.getEquipoIds());
    }

    private List<Partido> generarPartidosEliminatoria(String torneoId, List<String> equipoIds) {
        List<String> equipos = new ArrayList<>(equipoIds);
        Collections.shuffle(equipos);

        // Pad to next power of 2 with BYEs (null)
        int n = equipos.size();
        int bracketSize = 1;
        while (bracketSize < n) bracketSize *= 2;
        while (equipos.size() < bracketSize) equipos.add(null);

        List<Partido> generados = new ArrayList<>();
        List<String> currentRound = new ArrayList<>(equipos);

        while (currentRound.size() > 1) {
            String ronda = getNombreRonda(currentRound.size());
            List<String> nextRound = new ArrayList<>();

            for (int i = 0; i < currentRound.size(); i += 2) {
                String local = currentRound.get(i);
                String visitante = currentRound.get(i + 1);

                // BYE: team advances automatically
                if (local == null) {
                    nextRound.add(visitante);
                    continue;
                }
                if (visitante == null) {
                    nextRound.add(local);
                    continue;
                }

                Partido p = new Partido();
                p.setId(UUID.randomUUID().toString().substring(0, 8));
                p.setTorneoId(torneoId);
                p.setEquipoLocalId(local);
                p.setEquipoVisitanteId(visitante);
                p.setEstado(PartidoEstado.PENDIENTE);
                p.setRonda(ronda);
                p.setFecha(ronda);
                p.setGolesPorJugador(new java.util.HashMap<>());
                p.setAsistenciasPorJugador(new java.util.HashMap<>());
                repo.save(p);
                generados.add(p);
                // Placeholder for winner — will be resolved during simulation
                nextRound.add(null);
            }
            currentRound = nextRound;
        }
        return generados;
    }

    private List<Partido> generarPartidosLiga(String torneoId, List<String> equipoIds) {
        List<Partido> generados = new ArrayList<>();
        int jornada = 1;

        for (int i = 0; i < equipoIds.size(); i++) {
            for (int j = i + 1; j < equipoIds.size(); j++) {
                Partido partido = new Partido();
                partido.setId(UUID.randomUUID().toString().substring(0, 8));
                partido.setTorneoId(torneoId);
                partido.setEquipoLocalId(equipoIds.get(i));
                partido.setEquipoVisitanteId(equipoIds.get(j));
                partido.setEstado(PartidoEstado.PENDIENTE);
                partido.setRonda("Jornada " + jornada);
                partido.setFecha("Jornada " + jornada);
                partido.setGolesPorJugador(new HashMap<>());
                partido.setAsistenciasPorJugador(new HashMap<>());
                repo.save(partido);
                generados.add(partido);
                jornada++;
            }
        }

        return generados;
    }

    private String getNombreRonda(int equiposEnRonda) {
        return switch (equiposEnRonda) {
            case 2 -> "Final";
            case 4 -> "Semifinal";
            case 8 -> "Cuartos de Final";
            case 16 -> "Octavos de Final";
            case 32 -> "Dieciseisavos";
            default -> "Ronda de " + equiposEnRonda;
        };
    }

    @Override
    public boolean registrarResultado(String partidoId, int golesLocal, int golesVisitante) {
        Partido p = repo.findById(partidoId);
        if (p == null) return false;
        p.setGolesLocal(golesLocal);
        p.setGolesVisitante(golesVisitante);
        p.setEstado(PartidoEstado.JUGADO);
        repo.save(p);
        return true;
    }

    public Partido guardarPartido(Partido partido) {
        return repo.save(partido);
    }

    public Partido buscarPorId(String partidoId) {
        return repo.findById(partidoId);
    }

    public List<Partido> listarTodos() {
        return repo.findAll();
    }

    @Override
    public List<Partido> listarPartidosPorTorneo(String torneoId) {
        return repo.findByTorneoId(torneoId);
    }

    public void simularEliminatoria(String torneoId, List<Partido> initialPartidos) {
        List<Partido> pendientes = initialPartidos.stream()
                .filter(p -> p.getEstado() != PartidoEstado.JUGADO)
                .collect(Collectors.toList());

        if (pendientes.isEmpty()) return;

        List<String> ganadores = new ArrayList<>();
        for (Partido partido : pendientes) {
            simularPartidoAleatorio(partido);
            ganadores.add(obtenerGanador(partido));
        }

        while (ganadores.size() > 1) {
            String nextRound = getNombreRonda(ganadores.size());
            List<Partido> nextPartidos = new ArrayList<>();
            for (int i = 0; i < ganadores.size(); i += 2) {
                Partido p = new Partido();
                p.setId(UUID.randomUUID().toString().substring(0, 8));
                p.setTorneoId(torneoId);
                p.setEquipoLocalId(ganadores.get(i));
                p.setEquipoVisitanteId(ganadores.get(i + 1));
                p.setEstado(PartidoEstado.PENDIENTE);
                p.setRonda(nextRound);
                p.setFecha(nextRound);
                p.setGolesPorJugador(new HashMap<>());
                p.setAsistenciasPorJugador(new HashMap<>());
                repo.save(p);
                nextPartidos.add(p);
            }

            ganadores.clear();
            List<String> perdedores = new ArrayList<>();
            for (Partido partido : nextPartidos) {
                simularPartidoAleatorio(partido);
                ganadores.add(obtenerGanador(partido));
                perdedores.add(obtenerPerdedor(partido));
            }

            if ("Semifinal".equals(nextRound) && perdedores.size() == 2) {
                Partido tercerLugar = new Partido();
                tercerLugar.setId(UUID.randomUUID().toString().substring(0, 8));
                tercerLugar.setTorneoId(torneoId);
                tercerLugar.setEquipoLocalId(perdedores.get(0));
                tercerLugar.setEquipoVisitanteId(perdedores.get(1));
                tercerLugar.setEstado(PartidoEstado.PENDIENTE);
                tercerLugar.setRonda("Tercer Lugar");
                tercerLugar.setFecha("Tercer Lugar");
                tercerLugar.setGolesPorJugador(new HashMap<>());
                tercerLugar.setAsistenciasPorJugador(new HashMap<>());
                repo.save(tercerLugar);
                simularPartidoAleatorio(tercerLugar);
            }
        }
    }

    public void simularLiga(String torneoId, List<Partido> partidos) {
        for (Partido partido : partidos) {
            if (partido.getEstado() == PartidoEstado.JUGADO) {
                continue;
            }
            simularPartidoAleatorio(partido, true);
        }
    }

    public Map<String, String> obtenerPodioTorneo(String torneoId) {
        List<Partido> partidos = repo.findByTorneoId(torneoId);

        Partido finalPartido = partidos.stream()
                .filter(partido -> "Final".equals(partido.getRonda()))
                .filter(partido -> partido.getEstado() == PartidoEstado.JUGADO)
                .findFirst()
                .orElse(null);

        if (finalPartido == null) {
            return Map.of();
        }

        Partido tercerLugarPartido = partidos.stream()
                .filter(partido -> "Tercer Lugar".equals(partido.getRonda()))
                .filter(partido -> partido.getEstado() == PartidoEstado.JUGADO)
                .findFirst()
                .orElse(null);

        Map<String, String> podio = new HashMap<>();
        podio.put("campeonId", obtenerGanador(finalPartido));
        podio.put("subcampeonId", obtenerPerdedor(finalPartido));
        podio.put("tercerLugarId", tercerLugarPartido != null ? obtenerGanador(tercerLugarPartido) : null);
        return podio;
    }

    private void simularPartidoAleatorio(Partido partido) {
        simularPartidoAleatorio(partido, false);
    }

    private void simularPartidoAleatorio(Partido partido, boolean permitirEmpate) {
        int golesLocal = ThreadLocalRandom.current().nextInt(0, 5);
        int golesVisitante = ThreadLocalRandom.current().nextInt(0, 5);
        while (!permitirEmpate && golesLocal == golesVisitante) {
            golesLocal = ThreadLocalRandom.current().nextInt(0, 5);
            golesVisitante = ThreadLocalRandom.current().nextInt(0, 5);
        }

        Map<String, Integer> golesPorJugador = new HashMap<>();
        Map<String, Integer> asistenciasPorJugador = new HashMap<>();
        distribuirEventosEquipo(partido.getEquipoLocalId(), golesLocal, golesPorJugador, asistenciasPorJugador);
        distribuirEventosEquipo(partido.getEquipoVisitanteId(), golesVisitante, golesPorJugador, asistenciasPorJugador);

        partido.setGolesLocal(golesLocal);
        partido.setGolesVisitante(golesVisitante);
        partido.setEstado(PartidoEstado.JUGADO);
        partido.setGolesPorJugador(golesPorJugador);
        partido.setAsistenciasPorJugador(asistenciasPorJugador);
        repo.save(partido);
    }

    private void distribuirEventosEquipo(String equipoId, int golesEquipo, Map<String, Integer> golesPorJugador, Map<String, Integer> asistenciasPorJugador) {
        if (golesEquipo <= 0 || jugadorService == null) return;
        List<Jugador> jugadores = jugadorService.listarJugadoresPorEquipo(equipoId);
        if (jugadores.isEmpty()) return;
        for (int i = 0; i < golesEquipo; i++) {
            Jugador goleador = jugadores.get(ThreadLocalRandom.current().nextInt(jugadores.size()));
            golesPorJugador.merge(goleador.getId(), 1, Integer::sum);
            if (jugadores.size() > 1 && ThreadLocalRandom.current().nextInt(100) < 75) {
                List<Jugador> posibles = jugadores.stream()
                        .filter(j -> !j.getId().equals(goleador.getId()))
                        .collect(Collectors.toList());
                if (!posibles.isEmpty()) {
                    Jugador asistente = posibles.get(ThreadLocalRandom.current().nextInt(posibles.size()));
                    asistenciasPorJugador.merge(asistente.getId(), 1, Integer::sum);
                }
            }
        }
    }

    private String obtenerGanador(Partido partido) {
        return partido.getGolesLocal() > partido.getGolesVisitante()
                ? partido.getEquipoLocalId()
                : partido.getEquipoVisitanteId();
    }

    private String obtenerPerdedor(Partido partido) {
        return partido.getGolesLocal() > partido.getGolesVisitante()
                ? partido.getEquipoVisitanteId()
                : partido.getEquipoLocalId();
    }
}
