package org.example.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.example.model.*;
import org.example.service.impl.*;
import org.example.view.DashboardView;
import org.example.util.AuthHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
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
    private final MongoUsuarioService usuarioService;
    private final boolean habilitarAperturaDashboardDesdeRoot;
    private volatile boolean dashboardLanzadoDesdeRoot;

    public ApiServer(MongoEquipoService equipoService,
                     MongoJugadorService jugadorService,
                     MongoTorneoService torneoService,
                     MongoPartidoService partidoService,
                     MongoEstadisticaService estadisticaService) throws IOException {
        this(equipoService, jugadorService, torneoService, partidoService, estadisticaService, null, false);
    }

    public ApiServer(MongoEquipoService equipoService,
                     MongoJugadorService jugadorService,
                     MongoTorneoService torneoService,
                     MongoPartidoService partidoService,
                     MongoEstadisticaService estadisticaService,
                     boolean habilitarAperturaDashboardDesdeRoot) throws IOException {
        this(equipoService, jugadorService, torneoService, partidoService, estadisticaService, null, habilitarAperturaDashboardDesdeRoot);
    }

    public ApiServer(MongoEquipoService equipoService,
                     MongoJugadorService jugadorService,
                     MongoTorneoService torneoService,
                     MongoPartidoService partidoService,
                     MongoEstadisticaService estadisticaService,
                     MongoUsuarioService usuarioService,
                     boolean habilitarAperturaDashboardDesdeRoot) throws IOException {
        this.equipoService = equipoService;
        this.jugadorService = jugadorService;
        this.torneoService = torneoService;
        this.partidoService = partidoService;
        this.estadisticaService = estadisticaService;
        this.usuarioService = usuarioService;
        this.habilitarAperturaDashboardDesdeRoot = habilitarAperturaDashboardDesdeRoot;
        this.dashboardLanzadoDesdeRoot = false;

        server = HttpServer.create(new InetSocketAddress(8080), 0);
        registrarRutas();
    }

    private void registrarRutas() {
        server.createContext("/", this::servirHome);
        server.createContext("/api/auth/register", this::handleAuthRegister);
        server.createContext("/api/auth/login", this::handleAuthLogin);
        server.createContext("/api/torneos",    ex -> manejar(ex, this::getTorneos));
        server.createContext("/api/equipos",    ex -> manejar(ex, this::getEquipos));
        server.createContext("/api/standings",  ex -> manejar(ex, this::getStandings));
        server.createContext("/api/goleadores", ex -> manejar(ex, this::getGoleadores));
        server.createContext("/api/partidos",   ex -> manejar(ex, this::getPartidos));
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
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");

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
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");

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

        lanzarDashboardDesdeRootSiCorresponde();

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
                  <p>Al abrir esta URL se intenta lanzar el dashboard JavaFX una sola vez (modo consola).</p>
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

    private synchronized void lanzarDashboardDesdeRootSiCorresponde() {
        if (!habilitarAperturaDashboardDesdeRoot || dashboardLanzadoDesdeRoot) {
            return;
        }
        dashboardLanzadoDesdeRoot = true;
        try {
            DashboardView.lanzar(equipoService, jugadorService, torneoService, partidoService, estadisticaService);
            System.out.println("\u001B[36m [API] Dashboard JavaFX abierto desde /\u001B[0m");
        } catch (Exception e) {
            System.err.println("[API] No se pudo abrir Dashboard JavaFX desde /: " + e.getMessage());
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
