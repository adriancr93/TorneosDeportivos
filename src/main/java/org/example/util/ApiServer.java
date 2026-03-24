package org.example.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.example.model.*;
import org.example.repository.*;
import org.example.service.impl.*;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Servidor HTTP REST liviano (usa HttpServer del JDK, sin dependencias extra).
 * Corre en el puerto 8080 y expone datos del torneo en JSON para la app Swift.
 */
public class ApiServer {

    private final HttpServer server;
    private final ObjectMapper mapper = new ObjectMapper();

    // Servicios compartidos con el controlador principal
    private final MongoEquipoService equipoService;
    private final MongoJugadorService jugadorService;
    private final MongoTorneoService torneoService;
    private final MongoPartidoService partidoService;
    private final MongoEstadisticaService estadisticaService;

    public ApiServer(MongoEquipoService equipoService,
                     MongoJugadorService jugadorService,
                     MongoTorneoService torneoService,
                     MongoPartidoService partidoService,
                     MongoEstadisticaService estadisticaService) throws IOException {
        this.equipoService = equipoService;
        this.jugadorService = jugadorService;
        this.torneoService = torneoService;
        this.partidoService = partidoService;
        this.estadisticaService = estadisticaService;

        server = HttpServer.create(new InetSocketAddress(8080), 0);
        registrarRutas();
    }

    private void registrarRutas() {
        server.createContext("/api/torneos",    ex -> manejar(ex, this::getTorneos));
        server.createContext("/api/equipos",    ex -> manejar(ex, this::getEquipos));
        server.createContext("/api/standings",  ex -> manejar(ex, this::getStandings));
        server.createContext("/api/goleadores", ex -> manejar(ex, this::getGoleadores));
        server.createContext("/api/partidos",   ex -> manejar(ex, this::getPartidos));
    }

    public void start() {
        server.setExecutor(null);
        server.start();
        System.out.println("\u001B[36m [API] Servidor REST corriendo en http://localhost:8080\u001B[0m");
    }

    public void stop() {
        server.stop(0);
    }

    // ─── Handlers ─────────────────────────────────

    private Object getTorneos() {
        return torneoService.listarTorneos();
    }

    private Object getEquipos() {
        return equipoService.listarEquipos();
    }

    private Object getStandings() {
        // Usa el primer torneo activo o el último
        List<Torneo> torneos = torneoService.listarTorneos();
        if (torneos.isEmpty()) return Collections.emptyList();

        Torneo torneo = torneos.stream()
                .filter(t -> t.getEstado() == TorneoEstado.ACTIVO)
                .findFirst()
                .orElse(torneos.get(torneos.size() - 1));

        Estadistica est = estadisticaService.visualizarEstadisticas(torneo.getId());
        if (est == null) est = estadisticaService.generarEstadisticas(torneo.getId());
        if (est == null || est.getTabla() == null) return Collections.emptyList();

        // Enriquecer con nombre del equipo
        List<Map<String, Object>> resultado = new ArrayList<>();
        for (int i = 0; i < est.getTabla().size(); i++) {
            TablaPosicionItem item = est.getTabla().get(i);
            Equipo eq = equipoService.buscarPorId(item.getEquipoId());
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("posicion", i + 1);
            row.put("equipoId", item.getEquipoId());
            row.put("equipo", eq != null ? eq.getNombre() : item.getEquipoId());
            row.put("puntos", item.getPuntos());
            row.put("partidosJugados", item.getPartidosJugados());
            row.put("golesFavor", item.getGolesFavor());
            row.put("golesContra", item.getGolesContra());
            row.put("diferencia", item.getGolesFavor() - item.getGolesContra());

            // Calcular PG, PE, PP
            List<Partido> partidos = partidoService.listarPartidosPorTorneo(torneo.getId());
            int pg = 0, pe = 0, pp = 0;
            for (Partido p : partidos) {
                if (p.getEstado() != PartidoEstado.JUGADO) continue;
                boolean esL = item.getEquipoId().equals(p.getEquipoLocalId());
                boolean esV = item.getEquipoId().equals(p.getEquipoVisitanteId());
                if (!esL && !esV) continue;
                if (p.getGolesLocal() == p.getGolesVisitante()) { pe++; }
                else if ((esL && p.getGolesLocal() > p.getGolesVisitante())
                      || (esV && p.getGolesVisitante() > p.getGolesLocal())) { pg++; }
                else { pp++; }
            }
            row.put("victorias", pg);
            row.put("empates", pe);
            row.put("derrotas", pp);
            resultado.add(row);
        }
        return resultado;
    }

    private Object getGoleadores() {
        List<Torneo> torneos = torneoService.listarTorneos();
        if (torneos.isEmpty()) return Collections.emptyList();

        Torneo torneo = torneos.stream()
                .filter(t -> t.getEstado() == TorneoEstado.ACTIVO)
                .findFirst()
                .orElse(torneos.get(torneos.size() - 1));

        Estadistica est = estadisticaService.visualizarEstadisticas(torneo.getId());
        if (est == null || est.getGoleadores() == null) return Collections.emptyList();

        return est.getGoleadores().stream()
                .filter(g -> g.getGoles() > 0)
                .map(g -> {
                    Jugador j = jugadorService.buscarPorId(g.getJugadorId());
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("jugadorId", g.getJugadorId());
                    row.put("nombre", j != null ? j.getNombre() : g.getJugadorId());
                    row.put("goles", g.getGoles());
                    if (j != null) {
                        Equipo eq = equipoService.buscarPorId(j.getEquipoId());
                        row.put("equipo", eq != null ? eq.getNombre() : "");
                    }
                    return row;
                })
                .collect(Collectors.toList());
    }

    private Object getPartidos() {
        List<Torneo> torneos = torneoService.listarTorneos();
        if (torneos.isEmpty()) return Collections.emptyList();

        Torneo torneo = torneos.stream()
                .filter(t -> t.getEstado() == TorneoEstado.ACTIVO)
                .findFirst()
                .orElse(torneos.get(torneos.size() - 1));

        return partidoService.listarPartidosPorTorneo(torneo.getId()).stream()
                .map(p -> {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("id", p.getId());
                    row.put("fecha", p.getFecha());
                    row.put("estado", p.getEstado());
                    Equipo local = equipoService.buscarPorId(p.getEquipoLocalId());
                    Equipo visitante = equipoService.buscarPorId(p.getEquipoVisitanteId());
                    row.put("local", local != null ? local.getNombre() : p.getEquipoLocalId());
                    row.put("visitante", visitante != null ? visitante.getNombre() : p.getEquipoVisitanteId());
                    row.put("golesLocal", p.getGolesLocal());
                    row.put("golesVisitante", p.getGolesVisitante());
                    return row;
                })
                .collect(Collectors.toList());
    }

    // ─── Infraestructura HTTP ──────────────────────

    @FunctionalInterface
    interface DataSupplier { Object get() throws Exception; }

    private void manejar(HttpExchange exchange, DataSupplier supplier) throws IOException {
        // CORS para desarrollo local
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");

        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        try {
            Object data = supplier.get();
            byte[] response = mapper.writeValueAsBytes(data);
            exchange.sendResponseHeaders(200, response.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response);
            }
        } catch (Exception e) {
            byte[] error = ("{\"error\":\"" + e.getMessage() + "\"}").getBytes();
            exchange.sendResponseHeaders(500, error.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(error);
            }
        }
    }
}
