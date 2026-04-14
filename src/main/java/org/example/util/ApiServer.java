package org.example.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.example.model.*;
import org.example.service.impl.*;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Servidor HTTP REST liviano (usa HttpServer del JDK, sin dependencias extra).
 * Corre en el puerto 8080 y expone datos del torneo en JSON para la app Swift.
 */
public class ApiServer {
    private static final int MIN_EQUIPOS_TORNEO = 4;

    private final HttpServer server;
    private final ObjectMapper mapper = new ObjectMapper();

    // Servicios compartidos con el controlador principal
    private final MongoEquipoService equipoService;
    private final MongoJugadorService jugadorService;
    private final MongoTorneoService torneoService;
    private final MongoPartidoService partidoService;
    private final MongoEstadisticaService estadisticaService;
    private final MongoUsuarioService usuarioService;

    public ApiServer(MongoEquipoService equipoService,
                     MongoJugadorService jugadorService,
                     MongoTorneoService torneoService,
                     MongoPartidoService partidoService,
                     MongoEstadisticaService estadisticaService) throws IOException {
        this(equipoService, jugadorService, torneoService, partidoService, estadisticaService, null);
    }

    public ApiServer(MongoEquipoService equipoService,
                     MongoJugadorService jugadorService,
                     MongoTorneoService torneoService,
                     MongoPartidoService partidoService,
                     MongoEstadisticaService estadisticaService,
                     MongoUsuarioService usuarioService) throws IOException {
        this.equipoService = equipoService;
        this.jugadorService = jugadorService;
        this.torneoService = torneoService;
        this.partidoService = partidoService;
        this.estadisticaService = estadisticaService;
        this.usuarioService = usuarioService;

        server = HttpServer.create(new InetSocketAddress(8080), 0);
        registrarRutas();
    }

    private void registrarRutas() {
        server.createContext("/", this::servirHome);
        server.createContext("/api/auth/register", this::handleAuthRegister);
        server.createContext("/api/auth/login", this::handleAuthLogin);
        server.createContext("/api/torneos", this::handleTorneos);
        server.createContext("/api/equipos", this::handleEquipos);
        server.createContext("/api/jugadores", this::handleJugadores);
        server.createContext("/api/partidos", this::handlePartidos);
        server.createContext("/api/partidos/generar", this::handleGenerarPartidos);
        server.createContext("/api/torneos/simular", this::handleSimularTorneo);
        server.createContext("/api/standings", this::handleStandings);
        server.createContext("/api/goleadores", this::handleGoleadores);
        server.createContext("/api/asistencias", this::handleAsistencias);
        server.createContext("/api/openapi.json", this::servirOpenApi);
        server.createContext("/swagger-ui", this::servirSwaggerUi);
        server.createContext("/swagger", this::redirigirSwaggerUi);
    }

    public void start() {
        server.setExecutor(null);
        server.start();
        System.out.println("\u001B[36m [API] Servidor REST corriendo en http://localhost:8080\u001B[0m");
        System.out.println("\u001B[36m [API] OpenAPI JSON: http://localhost:8080/api/openapi.json\u001B[0m");
        System.out.println("\u001B[36m [API] Swagger UI: http://localhost:8080/swagger-ui\u001B[0m");
        System.out.println("\u001B[36m [API] Auth endpoints: POST /api/auth/register y /api/auth/login\u001B[0m");
    }

    public void stop() {
        server.stop(0);
    }

    // ─── Handlers ─────────────────────────────────

    private void handleAuthRegister(HttpExchange exchange) throws IOException {
        setCorsHeaders(exchange);

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        if (usuarioService == null) {
            byte[] error = "{\"success\":false,\"message\":\"Servicio de usuarios no disponible\"}".getBytes();
            exchange.sendResponseHeaders(500, error.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(error);
            }
            return;
        }

        try {
            AuthHandler authHandler = new AuthHandler(mapper, usuarioService);
            Map<String, Object> result = authHandler.handleRegister(exchange.getRequestBody());
            byte[] response = mapper.writeValueAsBytes(result);
            exchange.sendResponseHeaders(200, response.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response);
            }
        } catch (Exception e) {
            byte[] error = ("{\"success\":false,\"message\":\"" + e.getMessage() + "\"}").getBytes();
            exchange.sendResponseHeaders(500, error.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(error);
            }
        }
    }

    private void handleAuthLogin(HttpExchange exchange) throws IOException {
        setCorsHeaders(exchange);

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        if (usuarioService == null) {
            byte[] error = "{\"success\":false,\"message\":\"Servicio de usuarios no disponible\"}".getBytes();
            exchange.sendResponseHeaders(500, error.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(error);
            }
            return;
        }

        try {
            AuthHandler authHandler = new AuthHandler(mapper, usuarioService);
            Map<String, Object> result = authHandler.handleLogin(exchange.getRequestBody());
            byte[] response = mapper.writeValueAsBytes(result);
            exchange.sendResponseHeaders(200, response.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response);
            }
        } catch (Exception e) {
            byte[] error = ("{\"success\":false,\"message\":\"" + e.getMessage() + "\"}").getBytes();
            exchange.sendResponseHeaders(500, error.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(error);
            }
        }
    }

    private void handleTorneos(HttpExchange exchange) throws IOException {
        setCorsHeaders(exchange);

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        try {
            if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendJson(exchange, 200, getTorneos());
                return;
            }

            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            Map<String, Object> body = readJsonMap(exchange);
            String nombre = stringValue(body.get("nombre"));
            String sede = stringValue(body.get("sede"));
            String fechaInicio = stringValue(body.get("fechaInicio"));
            String fechaFin = stringValue(body.get("fechaFin"));
            String modalidadRaw = stringValue(body.get("modalidad"));
            List<String> equipoIds = uniqueStringList(body.get("equipoIds"));

            if (nombre.isBlank() || fechaInicio.isBlank() || fechaFin.isBlank()) {
                sendJson(exchange, 400, Map.of("success", false, "message", "Nombre y fechas son obligatorios"));
                return;
            }

            if (equipoIds.size() < MIN_EQUIPOS_TORNEO) {
                sendJson(exchange, 400, Map.of("success", false, "message", "El torneo debe tener al menos " + MIN_EQUIPOS_TORNEO + " equipos"));
                return;
            }

            for (String equipoId : equipoIds) {
                if (equipoService.buscarPorId(equipoId) == null) {
                    sendJson(exchange, 400, Map.of("success", false, "message", "Equipo no encontrado: " + equipoId));
                    return;
                }
            }

            Torneo torneo = new Torneo();
            torneo.setNombre(nombre);
            torneo.setSede(sede.isBlank() ? "Por definir" : sede);
            torneo.setFechaInicio(fechaInicio);
            torneo.setFechaFin(fechaFin);
            torneo.setModalidad(parseModalidad(modalidadRaw));
            torneo.setEquipoIds(equipoIds);
            Torneo creado = torneoService.crearTorneo(torneo);
            sendJson(exchange, 201, Map.of("success", true, "message", "Torneo creado", "torneo", mapTorneo(creado)));
        } catch (Exception e) {
            sendJson(exchange, 500, Map.of("success", false, "message", e.getMessage()));
        }
    }

    private void handleEquipos(HttpExchange exchange) throws IOException {
        setCorsHeaders(exchange);

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        try {
            if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendJson(exchange, 200, getEquipos());
                return;
            }

            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            Map<String, Object> body = readJsonMap(exchange);
            String nombre = stringValue(body.get("nombre"));
            String ciudad = stringValue(body.get("ciudad"));
            String entrenador = stringValue(body.get("entrenador"));
            int anioFundacion = intValue(body.get("anioFundacion"));

            if (nombre.isBlank() || ciudad.isBlank() || entrenador.isBlank()) {
                sendJson(exchange, 400, Map.of("success", false, "message", "Nombre, ciudad y entrenador son obligatorios"));
                return;
            }

            Equipo equipo = new Equipo();
            equipo.setNombre(nombre);
            equipo.setCiudad(ciudad);
            equipo.setEntrenador(entrenador);
            equipo.setAnioFundacion(anioFundacion);
            Equipo creado = equipoService.crearEquipo(equipo);
            sendJson(exchange, 201, Map.of("success", true, "message", "Equipo creado", "equipo", mapEquipo(creado)));
        } catch (Exception e) {
            sendJson(exchange, 500, Map.of("success", false, "message", e.getMessage()));
        }
    }

    private void handleJugadores(HttpExchange exchange) throws IOException {
        setCorsHeaders(exchange);

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        try {
            if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendJson(exchange, 200, getJugadores(exchange));
                return;
            }

            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            Map<String, Object> body = readJsonMap(exchange);
            String nombre = stringValue(body.get("nombre"));
            String posicion = stringValue(body.get("posicion"));
            String equipoId = stringValue(body.get("equipoId"));
            int edad = intValue(body.get("edad"));
            int dorsal = intValue(body.get("dorsal"));

            if (nombre.isBlank() || posicion.isBlank() || equipoId.isBlank()) {
                sendJson(exchange, 400, Map.of("success", false, "message", "Nombre, posición y equipo son obligatorios"));
                return;
            }

            if (equipoService.buscarPorId(equipoId) == null) {
                sendJson(exchange, 400, Map.of("success", false, "message", "El equipo seleccionado no existe"));
                return;
            }

            Jugador jugador = new Jugador();
            jugador.setNombre(nombre);
            jugador.setPosicion(posicion);
            jugador.setEdad(edad);
            jugador.setDorsal(dorsal);
            jugador.setEquipoId(equipoId);
            Jugador creado = jugadorService.registrarJugador(jugador);
            sendJson(exchange, 201, Map.of("success", true, "message", "Jugador creado", "jugador", mapJugador(creado)));
        } catch (Exception e) {
            sendJson(exchange, 500, Map.of("success", false, "message", e.getMessage()));
        }
    }

    private void handlePartidos(HttpExchange exchange) throws IOException {
        setCorsHeaders(exchange);

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        try {
            sendJson(exchange, 200, getPartidos(exchange));
        } catch (Exception e) {
            sendJson(exchange, 500, Map.of("success", false, "message", e.getMessage()));
        }
    }

    private void handleGenerarPartidos(HttpExchange exchange) throws IOException {
        setCorsHeaders(exchange);

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        try {
            String torneoId = extractTorneoId(exchange);
            Torneo torneo = requireTorneo(torneoId);
            if (torneo.getEstado() == TorneoEstado.FINALIZADO) {
                sendJson(exchange, 400, Map.of("success", false, "message", "El torneo ya finalizó y no admite nuevas operaciones"));
                return;
            }
            if (torneo.getEquipoIds() == null || torneo.getEquipoIds().size() < MIN_EQUIPOS_TORNEO) {
                sendJson(exchange, 400, Map.of("success", false, "message", "El torneo requiere al menos " + MIN_EQUIPOS_TORNEO + " equipos para generar partidos"));
                return;
            }

            torneoService.activarTorneo(torneo.getId());
            List<Partido> partidos = partidoService.generarPartidos(torneo.getId());
            estadisticaService.generarEstadisticas(torneo.getId());
            sendJson(exchange, 200, Map.of("success", true, "message", "Partidos generados", "partidos", partidos.stream().map(this::mapPartido).collect(Collectors.toList())));
        } catch (IllegalArgumentException e) {
            sendJson(exchange, 400, Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            sendJson(exchange, 500, Map.of("success", false, "message", e.getMessage()));
        }
    }

    private void handleSimularTorneo(HttpExchange exchange) throws IOException {
        setCorsHeaders(exchange);

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        try {
            String torneoId = extractTorneoId(exchange);
            Torneo torneo = requireTorneo(torneoId);
            if (torneo.getEstado() == TorneoEstado.FINALIZADO) {
                sendJson(exchange, 400, Map.of("success", false, "message", "El torneo ya fue finalizado. Consulta sus estadísticas."));
                return;
            }
            if (torneo.getEquipoIds() == null || torneo.getEquipoIds().size() < MIN_EQUIPOS_TORNEO) {
                sendJson(exchange, 400, Map.of("success", false, "message", "El torneo requiere al menos " + MIN_EQUIPOS_TORNEO + " equipos para simular"));
                return;
            }

            torneoService.activarTorneo(torneo.getId());
            List<Partido> partidos = partidoService.generarPartidos(torneo.getId());

            Estadistica estadistica;
            Torneo torneoActualizado;
            if (torneo.getModalidad() == ModalidadTorneo.LIGA) {
                partidoService.simularLiga(torneo.getId(), partidos);
                estadistica = estadisticaService.generarEstadisticas(torneo.getId());
                torneoActualizado = registrarPodioDesdeTabla(torneo.getId(), estadistica);
            } else {
                simularEliminatoria(torneo.getId(), partidos);
                Map<String, String> podio = partidoService.obtenerPodioTorneo(torneo.getId());
                torneoActualizado = podio.isEmpty()
                        ? torneoService.buscarPorId(torneo.getId())
                        : torneoService.registrarPodioFinal(
                                torneo.getId(),
                                podio.get("campeonId"),
                                podio.get("subcampeonId"),
                                podio.get("tercerLugarId")
                        );
                estadistica = estadisticaService.generarEstadisticas(torneo.getId());
            }
            sendJson(exchange, 200, Map.of(
                    "success", true,
                    "message", "Torneo simulado correctamente",
                    "torneo", mapTorneo(torneoActualizado),
                    "partidos", partidoService.listarPartidosPorTorneo(torneo.getId()).stream().map(this::mapPartido).collect(Collectors.toList()),
                    "standings", buildStandings(torneo.getId(), estadistica),
                    "goleadores", buildPlayerRanking(torneo.getId(), false),
                    "asistencias", buildPlayerRanking(torneo.getId(), true)
            ));
        } catch (IllegalArgumentException e) {
            sendJson(exchange, 400, Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            sendJson(exchange, 500, Map.of("success", false, "message", e.getMessage()));
        }
    }

    private void handleStandings(HttpExchange exchange) throws IOException {
        handleReadOnly(exchange, () -> getStandings(exchange));
    }

    private void handleGoleadores(HttpExchange exchange) throws IOException {
        handleReadOnly(exchange, () -> getGoleadores(exchange));
    }

    private void handleAsistencias(HttpExchange exchange) throws IOException {
        handleReadOnly(exchange, () -> getAsistencias(exchange));
    }

    private Object getTorneos() {
        return torneoService.listarTorneos().stream().map(this::mapTorneo).collect(Collectors.toList());
    }

    private Object getEquipos() {
        return equipoService.listarEquipos().stream().map(this::mapEquipo).collect(Collectors.toList());
    }

    private Object getJugadores(HttpExchange exchange) {
        String equipoId = queryParams(exchange).get("equipoId");
        List<Jugador> jugadores = (equipoId == null || equipoId.isBlank())
                ? jugadorService.listarTodos()
                : jugadorService.listarJugadoresPorEquipo(equipoId);
        return jugadores.stream().map(this::mapJugador).collect(Collectors.toList());
    }

    private Object getStandings(HttpExchange exchange) {
        String torneoId = resolveTorneoId(exchange);
        if (torneoId == null) return Collections.emptyList();
        Estadistica est = estadisticaService.visualizarEstadisticas(torneoId);
        if (est == null) est = estadisticaService.generarEstadisticas(torneoId);
        return buildStandings(torneoId, est);
    }

    private Object getGoleadores(HttpExchange exchange) {
        String torneoId = resolveTorneoId(exchange);
        return torneoId == null ? Collections.emptyList() : buildPlayerRanking(torneoId, false);
    }

    private Object getAsistencias(HttpExchange exchange) {
        String torneoId = resolveTorneoId(exchange);
        return torneoId == null ? Collections.emptyList() : buildPlayerRanking(torneoId, true);
    }

    private Object getPartidos(HttpExchange exchange) {
        String torneoId = resolveTorneoId(exchange);
        if (torneoId == null) return Collections.emptyList();
        return partidoService.listarPartidosPorTorneo(torneoId).stream().map(this::mapPartido).collect(Collectors.toList());
    }

    private void handleReadOnly(HttpExchange exchange, DataSupplier supplier) throws IOException {
        setCorsHeaders(exchange);

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        try {
            sendJson(exchange, 200, supplier.get());
        } catch (Exception e) {
            sendJson(exchange, 500, Map.of("success", false, "message", e.getMessage()));
        }
    }

    private void simularEliminatoria(String torneoId, List<Partido> initialPartidos) {
        partidoService.simularEliminatoria(torneoId, initialPartidos);
    }

    private ModalidadTorneo parseModalidad(String modalidadRaw) {
        if (modalidadRaw == null || modalidadRaw.isBlank()) {
            return ModalidadTorneo.ELIMINATORIA;
        }

        try {
            return ModalidadTorneo.valueOf(modalidadRaw.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("La modalidad debe ser LIGA o ELIMINATORIA");
        }
    }

    private Torneo registrarPodioDesdeTabla(String torneoId, Estadistica estadistica) {
        if (estadistica == null || estadistica.getTabla() == null || estadistica.getTabla().isEmpty()) {
            return torneoService.buscarPorId(torneoId);
        }

        String campeonId = estadistica.getTabla().size() > 0 ? estadistica.getTabla().get(0).getEquipoId() : null;
        String subcampeonId = estadistica.getTabla().size() > 1 ? estadistica.getTabla().get(1).getEquipoId() : null;
        String tercerLugarId = estadistica.getTabla().size() > 2 ? estadistica.getTabla().get(2).getEquipoId() : null;
        return torneoService.registrarPodioFinal(torneoId, campeonId, subcampeonId, tercerLugarId);
    }

    private List<Map<String, Object>> buildStandings(String torneoId, Estadistica estadistica) {
        if (estadistica == null || estadistica.getTabla() == null) {
            return Collections.emptyList();
        }

        List<Partido> partidos = partidoService.listarPartidosPorTorneo(torneoId);
        List<Map<String, Object>> resultado = new ArrayList<>();
        for (int i = 0; i < estadistica.getTabla().size(); i++) {
            TablaPosicionItem item = estadistica.getTabla().get(i);
            Equipo eq = equipoService.buscarPorId(item.getEquipoId());
            int ganados = 0;
            int empatados = 0;
            int perdidos = 0;

            for (Partido partido : partidos) {
                if (partido.getEstado() != PartidoEstado.JUGADO) continue;
                boolean local = item.getEquipoId().equals(partido.getEquipoLocalId());
                boolean visitante = item.getEquipoId().equals(partido.getEquipoVisitanteId());
                if (!local && !visitante) continue;
                if (partido.getGolesLocal() == partido.getGolesVisitante()) empatados++;
                else if ((local && partido.getGolesLocal() > partido.getGolesVisitante())
                        || (visitante && partido.getGolesVisitante() > partido.getGolesLocal())) ganados++;
                else perdidos++;
            }

            Map<String, Object> row = new LinkedHashMap<>();
            row.put("posicion", i + 1);
            row.put("equipoId", item.getEquipoId());
            row.put("nombre", eq != null ? eq.getNombre() : item.getEquipoId());
            row.put("partidos", item.getPartidosJugados());
            row.put("puntos", item.getPuntos());
            row.put("ganados", ganados);
            row.put("empatados", empatados);
            row.put("perdidos", perdidos);
            row.put("golesAFavor", item.getGolesFavor());
            row.put("golesEnContra", item.getGolesContra());
            row.put("diferencia", item.getGolesFavor() - item.getGolesContra());
            resultado.add(row);
        }
        return resultado;
    }

    private List<Map<String, Object>> buildPlayerRanking(String torneoId, boolean asistencias) {
        Map<String, Integer> ranking = new HashMap<>();
        for (Partido partido : partidoService.listarPartidosPorTorneo(torneoId)) {
            if (partido.getEstado() != PartidoEstado.JUGADO) continue;
            Map<String, Integer> source = asistencias ? partido.getAsistenciasPorJugador() : partido.getGolesPorJugador();
            for (Map.Entry<String, Integer> entry : source.entrySet()) {
                ranking.merge(entry.getKey(), entry.getValue(), Integer::sum);
            }
        }

        return ranking.entrySet().stream()
                .filter(entry -> entry.getValue() > 0)
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                .map(entry -> {
                    Jugador jugador = jugadorService.buscarPorId(entry.getKey());
                    Equipo equipo = jugador != null ? equipoService.buscarPorId(jugador.getEquipoId()) : null;
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("jugadorId", entry.getKey());
                    row.put("nombre", jugador != null ? jugador.getNombre() : entry.getKey());
                    row.put("equipo", equipo != null ? equipo.getNombre() : "Sin equipo");
                    row.put(asistencias ? "asistencias" : "goles", entry.getValue());
                    row.put("posicion", jugador != null ? jugador.getPosicion() : "");
                    return row;
                })
                .collect(Collectors.toList());
    }

    private Map<String, Object> mapTorneo(Torneo torneo) {
        Map<String, Object> row = new LinkedHashMap<>();
        Equipo campeon = torneo.getCampeonId() != null ? equipoService.buscarPorId(torneo.getCampeonId()) : null;
        Equipo subcampeon = torneo.getSubcampeonId() != null ? equipoService.buscarPorId(torneo.getSubcampeonId()) : null;
        Equipo tercerLugar = torneo.getTercerLugarId() != null ? equipoService.buscarPorId(torneo.getTercerLugarId()) : null;
        row.put("torneoId", torneo.getId());
        row.put("nombre", torneo.getNombre());
        row.put("sede", torneo.getSede());
        row.put("fechaInicio", torneo.getFechaInicio());
        row.put("fechaFin", torneo.getFechaFin());
        row.put("modalidad", torneo.getModalidad() != null ? torneo.getModalidad().name() : ModalidadTorneo.ELIMINATORIA.name());
        row.put("estado", torneo.getEstado() != null ? torneo.getEstado().name() : TorneoEstado.CREADO.name());
        row.put("equipos", torneo.getEquipoIds() != null ? torneo.getEquipoIds() : Collections.emptyList());
        row.put("cantidadEquipos", torneo.getEquipoIds() != null ? torneo.getEquipoIds().size() : 0);
        row.put("campeonId", torneo.getCampeonId());
        row.put("subcampeonId", torneo.getSubcampeonId());
        row.put("tercerLugarId", torneo.getTercerLugarId());
        row.put("campeon", campeon != null ? campeon.getNombre() : null);
        row.put("subcampeon", subcampeon != null ? subcampeon.getNombre() : null);
        row.put("tercerLugar", tercerLugar != null ? tercerLugar.getNombre() : null);
        return row;
    }

    private Map<String, Object> mapEquipo(Equipo equipo) {
        List<Jugador> jugadores = jugadorService.listarJugadoresPorEquipo(equipo.getId());
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("equipoId", equipo.getId());
        row.put("nombre", equipo.getNombre());
        row.put("ciudad", equipo.getCiudad());
        row.put("entrenador", equipo.getEntrenador());
        row.put("anioFundacion", equipo.getAnioFundacion());
        row.put("jugadores", jugadores.stream().map(this::mapJugador).collect(Collectors.toList()));
        return row;
    }

    private Map<String, Object> mapJugador(Jugador jugador) {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("jugadorId", jugador.getId());
        row.put("nombre", jugador.getNombre());
        row.put("edad", jugador.getEdad());
        row.put("posicion", jugador.getPosicion());
        row.put("numero", jugador.getDorsal());
        row.put("equipoId", jugador.getEquipoId());
        return row;
    }

    private Map<String, Object> mapPartido(Partido partido) {
        Equipo local = equipoService.buscarPorId(partido.getEquipoLocalId());
        Equipo visitante = equipoService.buscarPorId(partido.getEquipoVisitanteId());
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("partidoId", partido.getId());
        row.put("torneoId", partido.getTorneoId());
        row.put("fecha", partido.getFecha());
        row.put("estado", partido.getEstado() != null ? partido.getEstado().name() : PartidoEstado.PENDIENTE.name());
        row.put("equipoLocal", local != null ? local.getNombre() : partido.getEquipoLocalId());
        row.put("equipoVisitante", visitante != null ? visitante.getNombre() : partido.getEquipoVisitanteId());
        row.put("equipoLocalId", partido.getEquipoLocalId());
        row.put("equipoVisitanteId", partido.getEquipoVisitanteId());
        row.put("golesLocal", partido.getEstado() == PartidoEstado.JUGADO ? partido.getGolesLocal() : -1);
        row.put("golesVisitante", partido.getEstado() == PartidoEstado.JUGADO ? partido.getGolesVisitante() : -1);
        row.put("ronda", partido.getRonda());
        row.put("goleadores", partido.getGolesPorJugador());
        row.put("asistencias", partido.getAsistenciasPorJugador());
        return row;
    }

    private Map<String, Object> readJsonMap(HttpExchange exchange) throws IOException {
        byte[] bodyBytes = exchange.getRequestBody().readAllBytes();
        if (bodyBytes.length == 0) {
            return new HashMap<>();
        }
        return mapper.readValue(bodyBytes, new TypeReference<Map<String, Object>>() {});
    }

    private void sendJson(HttpExchange exchange, int status, Object payload) throws IOException {
        byte[] response = mapper.writeValueAsBytes(payload);
        exchange.sendResponseHeaders(status, response.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response);
        }
    }

    private String resolveTorneoId(HttpExchange exchange) {
        String torneoId = queryParams(exchange).get("torneoId");
        if (torneoId != null && !torneoId.isBlank()) {
            return torneoId;
        }

        List<Torneo> torneos = torneoService.listarTorneos();
        if (torneos.isEmpty()) {
            return null;
        }

        return torneos.stream()
                .filter(torneo -> torneo.getEstado() == TorneoEstado.ACTIVO)
                .findFirst()
                .orElse(torneos.get(torneos.size() - 1))
                .getId();
    }

    private String extractTorneoId(HttpExchange exchange) throws IOException {
        Map<String, Object> body = readJsonMap(exchange);
        String torneoId = stringValue(body.get("torneoId"));
        if (torneoId.isBlank()) {
            throw new IllegalArgumentException("Debes indicar el torneo a operar");
        }
        return torneoId;
    }

    private Torneo requireTorneo(String torneoId) {
        Torneo torneo = torneoService.buscarPorId(torneoId);
        if (torneo == null) {
            throw new IllegalArgumentException("Torneo no encontrado");
        }
        return torneo;
    }

    private Map<String, String> queryParams(HttpExchange exchange) {
        Map<String, String> params = new HashMap<>();
        String rawQuery = exchange.getRequestURI().getRawQuery();
        if (rawQuery == null || rawQuery.isBlank()) {
            return params;
        }

        for (String pair : rawQuery.split("&")) {
            String[] tokens = pair.split("=", 2);
            String key = URLDecoder.decode(tokens[0], StandardCharsets.UTF_8);
            String value = tokens.length > 1 ? URLDecoder.decode(tokens[1], StandardCharsets.UTF_8) : "";
            params.put(key, value);
        }
        return params;
    }

    private List<String> uniqueStringList(Object value) {
        if (!(value instanceof List<?> list)) {
            return new ArrayList<>();
        }
        return list.stream()
                .map(this::stringValue)
                .filter(item -> !item.isBlank())
                .distinct()
                .collect(Collectors.toList());
    }

    private String stringValue(Object value) {
        return value == null ? "" : String.valueOf(value).trim();
    }

    private int intValue(Object value) {
        if (value == null) return 0;
        if (value instanceof Number number) return number.intValue();
        try {
            return Integer.parseInt(String.valueOf(value).trim());
        } catch (NumberFormatException ignored) {
            return 0;
        }
    }

    // ─── Infraestructura HTTP ──────────────────────

    private void servirHome(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        String path = exchange.getRequestURI().getPath();
        if (!"/".equals(path)) {
            byte[] notFound = "404 Not Found".getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=utf-8");
            exchange.sendResponseHeaders(404, notFound.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(notFound);
            }
            return;
        }

        String html = """
                <!doctype html>
                <html lang=\"es\">
                <head>
                  <meta charset=\"UTF-8\" />
                  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />
                  <title>TorneosDeportivos API</title>
                </head>
                <body style=\"font-family: system-ui, sans-serif; padding: 24px;\">
                  <h1>TorneosDeportivos API</h1>
                  <p>El servidor esta corriendo correctamente.</p>
                                    <p>La interfaz grafica principal vive en el frontend React (http://localhost:5173).</p>
                  <ul>
                    <li><a href=\"/swagger-ui\">Swagger UI</a></li>
                    <li><a href=\"/api/openapi.json\">OpenAPI JSON</a></li>
                    <li>POST <a href=\"/api/auth/register\">/api/auth/register</a></li>
                    <li>POST <a href=\"/api/auth/login\">/api/auth/login</a></li>
                    <li>GET <a href=\"/api/torneos\">/api/torneos</a></li>
                    <li>GET <a href=\"/api/equipos\">/api/equipos</a></li>
                    <li>GET <a href=\"/api/partidos\">/api/partidos</a></li>
                    <li>GET <a href=\"/api/standings\">/api/standings</a></li>
                    <li>GET <a href=\"/api/goleadores\">/api/goleadores</a></li>
                  </ul>
                </body>
                </html>
                """;

        byte[] response = html.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "text/html; charset=utf-8");
        exchange.sendResponseHeaders(200, response.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response);
        }
    }

    private void servirOpenApi(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");

        Map<String, Object> spec = construirOpenApiSpec();
        byte[] response = mapper.writeValueAsBytes(spec);
        exchange.sendResponseHeaders(200, response.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response);
        }
    }

    private void servirSwaggerUi(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        String html = """
                <!doctype html>
                <html lang=\"es\">
                <head>
                  <meta charset=\"UTF-8\" />
                  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />
                  <title>Swagger UI - TorneosDeportivos</title>
                  <link rel=\"stylesheet\" href=\"https://unpkg.com/swagger-ui-dist@5/swagger-ui.css\" />
                </head>
                <body>
                  <div id=\"swagger-ui\"></div>
                  <script src=\"https://unpkg.com/swagger-ui-dist@5/swagger-ui-bundle.js\"></script>
                  <script>
                    window.ui = SwaggerUIBundle({
                      url: '/api/openapi.json',
                      dom_id: '#swagger-ui'
                    });
                  </script>
                </body>
                </html>
                """;

        byte[] response = html.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "text/html; charset=utf-8");
        exchange.sendResponseHeaders(200, response.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response);
        }
    }

    private void redirigirSwaggerUi(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Location", "/swagger-ui");
        exchange.sendResponseHeaders(302, -1);
    }

    private Map<String, Object> construirOpenApiSpec() {
        Map<String, Object> spec = new LinkedHashMap<>();

        // Info
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("title", "TorneosDeportivos API");
        info.put("version", "1.0.0");
        info.put("description", "API REST para gestión de torneos deportivos: autenticación, torneos, equipos, partidos, posiciones y goleadores.");
        spec.put("openapi", "3.0.3");
        spec.put("info", info);
        spec.put("servers", List.of(Map.of("url", "http://localhost:8080", "description", "Servidor local")));

        // Tags
        spec.put("tags", List.of(
                Map.of("name", "Auth",       "description", "Registro e inicio de sesión"),
                Map.of("name", "Torneos",    "description", "Gestión de torneos"),
                Map.of("name", "Equipos",    "description", "Gestión de equipos"),
                Map.of("name", "Partidos",   "description", "Partidos del torneo activo"),
                Map.of("name", "Estadísticas", "description", "Tabla de posiciones y goleadores")
        ));

        // ── Paths ─────────────────────────────────────────────────────────────
        Map<String, Object> paths = new LinkedHashMap<>();

        // POST /api/auth/register
        paths.put("/api/auth/register", Map.of(
                "post", Map.of(
                        "tags", List.of("Auth"),
                        "summary", "Registrar usuario",
                        "description", "Crea una cuenta nueva con correo y contraseña.",
                        "requestBody", Map.of(
                                "required", true,
                                "content", Map.of("application/json", Map.of(
                                        "schema", Map.of("$ref", "#/components/schemas/AuthRequest"),
                                        "example", Map.of("email", "usuario@ejemplo.com", "password", "miClave123")
                                ))
                        ),
                        "responses", Map.of(
                                "200", Map.of(
                                        "description", "Registro exitoso",
                                        "content", Map.of("application/json", Map.of(
                                                "schema", Map.of("$ref", "#/components/schemas/AuthResponse")
                                        ))
                                ),
                                "400", Map.of("description", "Datos inválidos o correo ya registrado"),
                                "500", Map.of("description", "Error interno del servidor")
                        )
                )
        ));

        // POST /api/auth/login
        paths.put("/api/auth/login", Map.of(
                "post", Map.of(
                        "tags", List.of("Auth"),
                        "summary", "Iniciar sesión",
                        "description", "Autentica un usuario con correo y contraseña.",
                        "requestBody", Map.of(
                                "required", true,
                                "content", Map.of("application/json", Map.of(
                                        "schema", Map.of("$ref", "#/components/schemas/AuthRequest"),
                                        "example", Map.of("email", "demo@example.com", "password", "demo123")
                                ))
                        ),
                        "responses", Map.of(
                                "200", Map.of(
                                        "description", "Login exitoso",
                                        "content", Map.of("application/json", Map.of(
                                                "schema", Map.of("$ref", "#/components/schemas/AuthResponse")
                                        ))
                                ),
                                "401", Map.of("description", "Credenciales incorrectas"),
                                "500", Map.of("description", "Error interno del servidor")
                        )
                )
        ));

        // GET /api/torneos
        paths.put("/api/torneos", pathGet(
                "Listar torneos", "Devuelve todos los torneos registrados en el sistema.",
                List.of("Torneos"),
                Map.of("type", "array", "items", Map.of("$ref", "#/components/schemas/Torneo"))
        ));

        // GET /api/equipos
        paths.put("/api/equipos", pathGet(
                "Listar equipos", "Devuelve todos los equipos registrados.",
                List.of("Equipos"),
                Map.of("type", "array", "items", Map.of("$ref", "#/components/schemas/Equipo"))
        ));

        // GET /api/partidos
        paths.put("/api/partidos", pathGet(
                "Listar partidos", "Devuelve los partidos del torneo activo (o el último torneo).",
                List.of("Partidos"),
                Map.of("type", "array", "items", Map.of("$ref", "#/components/schemas/Partido"))
        ));

        // GET /api/standings
        paths.put("/api/standings", pathGet(
                "Tabla de posiciones", "Devuelve la tabla de posiciones del torneo activo o último torneo.",
                List.of("Estadísticas"),
                Map.of("type", "array", "items", Map.of("$ref", "#/components/schemas/Standing"))
        ));

        // GET /api/goleadores
        paths.put("/api/goleadores", pathGet(
                "Goleadores", "Devuelve el listado de goleadores (solo jugadores con al menos 1 gol).",
                List.of("Estadísticas"),
                Map.of("type", "array", "items", Map.of("$ref", "#/components/schemas/Goleador"))
        ));

        spec.put("paths", paths);

        // ── Components / Schemas ──────────────────────────────────────────────
        Map<String, Object> schemas = new LinkedHashMap<>();

        // AuthRequest
        Map<String, Object> authReqProps = new LinkedHashMap<>();
        authReqProps.put("email",    Map.of("type", "string", "format", "email",    "example", "usuario@ejemplo.com"));
        authReqProps.put("password", Map.of("type", "string", "minLength", 6,       "example", "miClave123"));
        schemas.put("AuthRequest", Map.of(
                "type", "object",
                "required", List.of("email", "password"),
                "properties", authReqProps
        ));

        // AuthResponse
        Map<String, Object> authRespProps = new LinkedHashMap<>();
        authRespProps.put("success",    Map.of("type", "boolean"));
        authRespProps.put("message",    Map.of("type", "string", "example", "Login exitoso"));
        authRespProps.put("username",   Map.of("type", "string", "example", "usuario@ejemplo.com"));
        authRespProps.put("usuarioId",  Map.of("type", "string", "example", "64a1f2b3c4d5e6f7a8b9c0d1"));
        schemas.put("AuthResponse", Map.of(
                "type", "object",
                "properties", authRespProps
        ));

        // Torneo
        Map<String, Object> torneoProps = new LinkedHashMap<>();
        torneoProps.put("id",          Map.of("type", "string", "example", "64a1f2b3c4d5e6f7a8b9c0d1"));
        torneoProps.put("nombre",      Map.of("type", "string", "example", "Liga Mayor 2026"));
        torneoProps.put("sede",        Map.of("type", "string", "example", "Estadio Nacional"));
        torneoProps.put("fechaInicio", Map.of("type", "string", "format", "date", "example", "2026-01-15"));
        torneoProps.put("fechaFin",    Map.of("type", "string", "format", "date", "example", "2026-06-30"));
        torneoProps.put("estado",      Map.of("type", "string", "enum", List.of("ACTIVO", "INACTIVO", "FINALIZADO"), "example", "ACTIVO"));
        torneoProps.put("equipoIds",   Map.of("type", "array", "items", Map.of("type", "string")));
        schemas.put("Torneo", Map.of(
                "type", "object",
                "properties", torneoProps
        ));

        // Equipo
        Map<String, Object> equipoProps = new LinkedHashMap<>();
        equipoProps.put("id",              Map.of("type", "string",  "example", "64a1f2b3c4d5e6f7a8b9c0d2"));
        equipoProps.put("nombre",          Map.of("type", "string",  "example", "Deportivo FC"));
        equipoProps.put("ciudad",          Map.of("type", "string",  "example", "San José"));
        equipoProps.put("anioFundacion",   Map.of("type", "integer", "example", 1995));
        equipoProps.put("entrenador",      Map.of("type", "string",  "example", "Carlos Méndez"));
        equipoProps.put("golesFavor",      Map.of("type", "integer", "example", 24));
        equipoProps.put("golesContra",     Map.of("type", "integer", "example", 10));
        equipoProps.put("puntos",          Map.of("type", "integer", "example", 21));
        equipoProps.put("partidosJugados", Map.of("type", "integer", "example", 9));
        schemas.put("Equipo", Map.of(
                "type", "object",
                "properties", equipoProps
        ));

        // Partido
        Map<String, Object> partidoProps = new LinkedHashMap<>();
        partidoProps.put("id",              Map.of("type", "string",  "example", "64a1f2b3c4d5e6f7a8b9c0d3"));
        partidoProps.put("fecha",           Map.of("type", "string",  "format", "date", "example", "2026-03-10"));
        partidoProps.put("estado",          Map.of("type", "string",  "enum", List.of("PROGRAMADO", "JUGADO", "CANCELADO"), "example", "JUGADO"));
        partidoProps.put("local",           Map.of("type", "string",  "example", "Deportivo FC"));
        partidoProps.put("visitante",       Map.of("type", "string",  "example", "Atlético Norte"));
        partidoProps.put("golesLocal",      Map.of("type", "integer", "example", 3));
        partidoProps.put("golesVisitante",  Map.of("type", "integer", "example", 1));
        schemas.put("Partido", Map.of(
                "type", "object",
                "properties", partidoProps
        ));

        // Standing
        Map<String, Object> standingProps = new LinkedHashMap<>();
        standingProps.put("posicion",       Map.of("type", "integer", "example", 1));
        standingProps.put("equipoId",       Map.of("type", "string",  "example", "64a1f2b3c4d5e6f7a8b9c0d2"));
        standingProps.put("equipo",         Map.of("type", "string",  "example", "Deportivo FC"));
        standingProps.put("puntos",         Map.of("type", "integer", "example", 21));
        standingProps.put("partidosJugados",Map.of("type", "integer", "example", 9));
        standingProps.put("golesFavor",     Map.of("type", "integer", "example", 24));
        standingProps.put("golesContra",    Map.of("type", "integer", "example", 10));
        standingProps.put("diferencia",     Map.of("type", "integer", "example", 14));
        standingProps.put("victorias",      Map.of("type", "integer", "example", 7));
        standingProps.put("empates",        Map.of("type", "integer", "example", 0));
        standingProps.put("derrotas",       Map.of("type", "integer", "example", 2));
        schemas.put("Standing", Map.of(
                "type", "object",
                "properties", standingProps
        ));

        // Goleador
        Map<String, Object> goleadorProps = new LinkedHashMap<>();
        goleadorProps.put("jugadorId", Map.of("type", "string",  "example", "64a1f2b3c4d5e6f7a8b9c0d4"));
        goleadorProps.put("nombre",    Map.of("type", "string",  "example", "Juan Pérez"));
        goleadorProps.put("goles",     Map.of("type", "integer", "example", 12));
        goleadorProps.put("equipo",    Map.of("type", "string",  "example", "Deportivo FC"));
        schemas.put("Goleador", Map.of(
                "type", "object",
                "properties", goleadorProps
        ));

        spec.put("components", Map.of("schemas", schemas));

        return spec;
    }

    private Map<String, Object> pathGet(String summary, String description, List<String> tags, Map<String, Object> responseSchema) {
        Map<String, Object> operation = new LinkedHashMap<>();
        operation.put("tags", tags);
        operation.put("summary", summary);
        operation.put("description", description);
        operation.put("responses", Map.of(
                "200", Map.of(
                        "description", "OK",
                        "content", Map.of("application/json", Map.of("schema", responseSchema))
                ),
                "500", Map.of("description", "Error interno del servidor")
        ));
        return Map.of("get", operation);
    }

    @FunctionalInterface
    interface DataSupplier { Object get() throws Exception; }

    private void setCorsHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
    }

    @SuppressWarnings("unused")
    private void manejar(HttpExchange exchange, DataSupplier supplier) throws IOException {
        // CORS para desarrollo local
        setCorsHeaders(exchange);

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

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
