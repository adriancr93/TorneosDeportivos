package org.example.controller;

import org.example.interfaces.*;
import org.example.model.*;
import java.util.List;

public class MainController {
    private final IEquipoService equipoService;
    private final IJugadorService jugadorService;
    private final ITorneoService torneoService;
    private final IPartidoService partidoService;
    private final IEstadisticaService estadisticaService;
    private final IReporteService reporteService;

    public MainController(IEquipoService equipoService, IJugadorService jugadorService, ITorneoService torneoService,
                         IPartidoService partidoService, IEstadisticaService estadisticaService, IReporteService reporteService) {
        this.equipoService = equipoService;
        this.jugadorService = jugadorService;
        this.torneoService = torneoService;
        this.partidoService = partidoService;
        this.estadisticaService = estadisticaService;
        this.reporteService = reporteService;
    }

    // Métodos para cada opción del menú
    public Equipo crearEquipo(Equipo equipo) {
        return equipoService.crearEquipo(equipo);
    }

    public Equipo editarEquipo(String id, Equipo equipo) {
        return equipoService.editarEquipo(id, equipo);
    }

    public boolean eliminarEquipo(String id) {
        return equipoService.eliminarEquipo(id);
    }

    public List<Equipo> listarEquipos() {
        return equipoService.listarEquipos();
    }

    public Jugador registrarJugador(Jugador jugador) {
        return jugadorService.registrarJugador(jugador);
    }

    public boolean asociarJugadorAEquipo(String jugadorId, String equipoId) {
        return jugadorService.asociarJugadorAEquipo(jugadorId, equipoId);
    }

    public List<Jugador> listarJugadoresPorEquipo(String equipoId) {
        return jugadorService.listarJugadoresPorEquipo(equipoId);
    }

    public Torneo crearTorneo(Torneo torneo) {
        return torneoService.crearTorneo(torneo);
    }

    public boolean agregarEquipoATorneo(String torneoId, String equipoId) {
        return torneoService.agregarEquipoATorneo(torneoId, equipoId);
    }

    public boolean activarTorneo(String torneoId) {
        return torneoService.activarTorneo(torneoId);
    }

    public boolean finalizarTorneo(String torneoId) {
        return torneoService.finalizarTorneo(torneoId);
    }

    public List<Torneo> listarTorneos() {
        return torneoService.listarTorneos();
    }

    public List<Partido> generarPartidos(String torneoId) {
        return partidoService.generarPartidos(torneoId);
    }

    public boolean registrarResultado(String partidoId, int golesLocal, int golesVisitante) {
        return partidoService.registrarResultado(partidoId, golesLocal, golesVisitante);
    }

    public List<Partido> listarPartidosPorTorneo(String torneoId) {
        return partidoService.listarPartidosPorTorneo(torneoId);
    }

    public Estadistica generarEstadisticas(String torneoId) {
        return estadisticaService.generarEstadisticas(torneoId);
    }

    public Estadistica visualizarEstadisticas(String torneoId) {
        return estadisticaService.visualizarEstadisticas(torneoId);
    }

    public Equipo equipoConMasGoles(String torneoId) {
        return reporteService.equipoConMasGoles(torneoId);
    }

    public Jugador jugadorConMasGoles(String torneoId) {
        return reporteService.jugadorConMasGoles(torneoId);
    }

    public List<String> historialTorneos() {
        return reporteService.historialTorneos();
    }
}
