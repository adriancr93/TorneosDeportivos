package org.example.controller;

import org.example.config.DataSeeder;
import org.example.model.*;
import org.example.repository.*;
import org.example.service.impl.*;
import org.example.util.ApiServer;
import org.example.view.*;

import java.util.List;
import java.util.Scanner;

public class TorneoController {
    private final Scanner scanner;

    // Servicios (MongoDB)
    private final MongoEquipoService equipoService;
    private final MongoJugadorService jugadorService;
    private final MongoTorneoService torneoService;
    private final MongoPartidoService partidoService;
    private final MongoEstadisticaService estadisticaService;
    private final MongoReporteService reporteService;
    private final MongoUsuarioService usuarioService;

    // Vistas
    private final EquipoView equipoView;
    private final JugadorView jugadorView;
    private final TorneoView torneoView;
    private final PartidoView partidoView;
    private final EstadisticaView estadisticaView;
    private final ReporteView reporteView;

    public TorneoController() {
        this.scanner = new Scanner(System.in);

        // Inicializar repositorios
        EquipoRepository equipoRepo = new EquipoRepository();
        JugadorRepository jugadorRepo = new JugadorRepository();
        TorneoRepository torneoRepo = new TorneoRepository();
        PartidoRepository partidoRepo = new PartidoRepository();
        EstadisticaRepository estadisticaRepo = new EstadisticaRepository();
        UsuarioRepository usuarioRepo = new UsuarioRepository();

        // Inicializar servicios con MongoDB
        this.equipoService = new MongoEquipoService(equipoRepo);
        this.jugadorService = new MongoJugadorService(jugadorRepo);
        this.torneoService = new MongoTorneoService(torneoRepo);
        this.partidoService = new MongoPartidoService(partidoRepo, torneoService);
        this.partidoService.setJugadorService(jugadorService);
        this.estadisticaService = new MongoEstadisticaService(estadisticaRepo, partidoService, jugadorService);
        this.reporteService = new MongoReporteService(equipoService, jugadorService, torneoService, partidoService);
        this.usuarioService = new MongoUsuarioService(usuarioRepo);
        this.usuarioService.asegurarUsuarioDemo("demo@example.com", "demo123");

        // Inicializar API REST (puerto 8080) en hilo de fondo
        try {
            ApiServer apiServer = new ApiServer(equipoService, jugadorService, torneoService, partidoService, estadisticaService, usuarioService);
            Thread apiThread = new Thread(apiServer::start, "api-server");
            apiThread.setDaemon(true);
            apiThread.start();
        } catch (Exception e) {
            ConsoleUI.printWarning("API REST no pudo iniciar: " + e.getMessage());
        }

        // Inicializar vistas
        this.equipoView = new EquipoView(scanner, this);
        this.jugadorView = new JugadorView(scanner, this);
        this.torneoView = new TorneoView(scanner, this);
        this.partidoView = new PartidoView(scanner, this);
        this.estadisticaView = new EstadisticaView(scanner, this);
        this.reporteView = new ReporteView(scanner, this);
    }

    // ─── Punto de entrada: menú principal ──────────
    public void iniciar() {
        int opcion;
        do {
            ConsoleUI.printHeader("⚽  SISTEMA DE GESTIÓN DE TORNEOS DEPORTIVOS  ⚽");
            System.out.println();
            ConsoleUI.printMenuOption(1, "Gestión de Equipos");
            ConsoleUI.printMenuOption(2, "Gestión de Jugadores");
            ConsoleUI.printMenuOption(3, "Gestión de Torneos");
            ConsoleUI.printMenuOption(4, "Gestión de Partidos");
            ConsoleUI.printMenuOption(5, "Estadísticas");
            ConsoleUI.printMenuOption(6, "Reportes");
            ConsoleUI.printMenuOption(7, "Cargar datos de prueba");
            ConsoleUI.printMenuExit();
            ConsoleUI.printSeparator();
            opcion = ConsoleUI.leerEntero(scanner, "Seleccione una opción: ");
            switch (opcion) {
                case 1 -> equipoView.mostrarMenu();
                case 2 -> jugadorView.mostrarMenu();
                case 3 -> torneoView.mostrarMenu();
                case 4 -> partidoView.mostrarMenu();
                case 5 -> estadisticaView.mostrarMenu();
                case 6 -> reporteView.mostrarMenu();
                case 7 -> cargarDatosDePrueba();
                case 0 -> ConsoleUI.printHeader("¡Hasta pronto!");
                default -> ConsoleUI.printWarning("Opción inválida. Intente de nuevo.");
            }
        } while (opcion != 0);
    }

    // ─── Servicios de Equipo ───────────────────────
    public Equipo crearEquipo(Equipo equipo) { return equipoService.crearEquipo(equipo); }
    public Equipo editarEquipo(String id, Equipo equipo) { return equipoService.editarEquipo(id, equipo); }
    public boolean eliminarEquipo(String id) { return equipoService.eliminarEquipo(id); }
    public List<Equipo> listarEquipos() { return equipoService.listarEquipos(); }

    // ─── Servicios de Jugador ──────────────────────
    public Jugador registrarJugador(Jugador jugador) { return jugadorService.registrarJugador(jugador); }
    public boolean asociarJugadorAEquipo(String jugadorId, String equipoId) { return jugadorService.asociarJugadorAEquipo(jugadorId, equipoId); }
    public List<Jugador> listarJugadoresPorEquipo(String equipoId) { return jugadorService.listarJugadoresPorEquipo(equipoId); }

    // ─── Servicios de Torneo ───────────────────────
    public Torneo crearTorneo(Torneo torneo) { return torneoService.crearTorneo(torneo); }
    public boolean agregarEquipoATorneo(String torneoId, String equipoId) { return torneoService.agregarEquipoATorneo(torneoId, equipoId); }
    public boolean activarTorneo(String torneoId) { return torneoService.activarTorneo(torneoId); }
    public boolean finalizarTorneo(String torneoId) { return torneoService.finalizarTorneo(torneoId); }
    public List<Torneo> listarTorneos() { return torneoService.listarTorneos(); }

    // ─── Servicios de Partido ──────────────────────
    public List<Partido> generarPartidos(String torneoId) { return partidoService.generarPartidos(torneoId); }
    public boolean registrarResultado(String partidoId, int golesLocal, int golesVisitante) { return partidoService.registrarResultado(partidoId, golesLocal, golesVisitante); }
    public List<Partido> listarPartidosPorTorneo(String torneoId) { return partidoService.listarPartidosPorTorneo(torneoId); }
    public List<Partido> simularTorneo(String torneoId) {
        torneoService.activarTorneo(torneoId);
        List<Partido> partidos = partidoService.generarPartidos(torneoId);
        if (partidos == null || partidos.isEmpty()) return partidos;
        partidoService.simularEliminatoria(torneoId, partidos);
        estadisticaService.generarEstadisticas(torneoId);
        return partidoService.listarPartidosPorTorneo(torneoId);
    }

    // ─── Servicios de Estadísticas ─────────────────
    public Estadistica generarEstadisticas(String torneoId) { return estadisticaService.generarEstadisticas(torneoId); }
    public Estadistica visualizarEstadisticas(String torneoId) { return estadisticaService.visualizarEstadisticas(torneoId); }

    // ─── Servicios de Reportes ─────────────────────
    public Equipo equipoConMasGoles(String torneoId) { return reporteService.equipoConMasGoles(torneoId); }
    public Jugador jugadorConMasGoles(String torneoId) { return reporteService.jugadorConMasGoles(torneoId); }
    public List<String> historialTorneos() { return reporteService.historialTorneos(); }

    // ─── Buscar por ID ─────────────────────────────
    public Equipo buscarEquipoPorId(String id) { return equipoService.buscarPorId(id); }
    public Jugador buscarJugadorPorId(String id) { return jugadorService.buscarPorId(id); }

    // ─── Datos de prueba ───────────────────────────
    private void cargarDatosDePrueba() {
        DataSeeder seeder = new DataSeeder(equipoService, jugadorService, torneoService, partidoService, estadisticaService);
        seeder.cargarDatosDePrueba();
    }
}
