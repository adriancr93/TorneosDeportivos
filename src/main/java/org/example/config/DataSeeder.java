package org.example.config;

import org.example.model.*;
import org.example.service.impl.*;
import org.example.view.ConsoleUI;

import java.util.List;
import java.util.Random;

public class DataSeeder {
    private final MongoEquipoService equipoService;
    private final MongoJugadorService jugadorService;
    private final MongoTorneoService torneoService;
    private final MongoPartidoService partidoService;
    private final MongoEstadisticaService estadisticaService;

    public DataSeeder(MongoEquipoService equipoService, MongoJugadorService jugadorService,
                      MongoTorneoService torneoService, MongoPartidoService partidoService,
                      MongoEstadisticaService estadisticaService) {
        this.equipoService = equipoService;
        this.jugadorService = jugadorService;
        this.torneoService = torneoService;
        this.partidoService = partidoService;
        this.estadisticaService = estadisticaService;
    }

    public void cargarDatosDePrueba() {
        ConsoleUI.printInfo("Cargando datos de prueba...");

        // ─── 1. Crear 6 equipos ────────────────────────
        String[][] equiposData = {
                {"Saprissa", "San José", "2005", "Jeaustin Campos"},
                {"Liga Deportiva Alajuelense", "Alajuela", "1919", "Alexandre Guimarães"},
                {"Herediano", "Heredia", "1921", "Jafet Soto"},
                {"Cartaginés", "Cartago", "1906", "Géiner Segura"},
                {"Santos de Guápiles", "Guápiles", "1962", "Douglas Sequeira"},
                {"Sporting FC", "San José", "2016", "Hernán Medford"}
        };

        String[] equipoIds = new String[6];
        for (int i = 0; i < equiposData.length; i++) {
            Equipo e = new Equipo();
            e.setNombre(equiposData[i][0]);
            e.setCiudad(equiposData[i][1]);
            e.setAnioFundacion(Integer.parseInt(equiposData[i][2]));
            e.setEntrenador(equiposData[i][3]);
            Equipo creado = equipoService.crearEquipo(e);
            equipoIds[i] = creado.getId();
        }
        ConsoleUI.printSuccess("6 equipos creados");

        // ─── 2. Crear jugadores por equipo ─────────────
        String[][] jugadoresNombres = {
                {"Kevin Chamorro", "Kendall Waston", "Bryan Oviedo", "Yeltsin Tejeda", "Joel Campbell"},
                {"Leonel Moreira", "Giancarlo González", "Bryan Ruiz", "Johan Venegas", "Ariel Rodríguez"},
                {"Esteban Alvarado", "Keysher Fuller", "Celso Borges", "Randall Leal", "Jonathan Moya"},
                {"Daryl Dervite", "Ian Smith", "Marvin Angulo", "Ulises Segura", "Álvaro Saborío"},
                {"Danny Carvajal", "Pablo Arboine", "Diego Madrigal", "Christian Bolaños", "Marco Ureña"},
                {"Patrick Pemberton", "Ricardo Blanco", "David Guzmán", "Josué Martínez", "Jaylon Hadden"}
        };
        String[] posiciones = {"Portero", "Defensa", "Defensa", "Mediocampista", "Delantero"};

        for (int eq = 0; eq < 6; eq++) {
            for (int j = 0; j < 5; j++) {
                Jugador jug = new Jugador();
                jug.setNombre(jugadoresNombres[eq][j]);
                jug.setEdad(22 + (j * 3));
                jug.setPosicion(posiciones[j]);
                jug.setDorsal(j + 1);
                jug.setEquipoId(equipoIds[eq]);
                jugadorService.registrarJugador(jug);
            }
        }
        ConsoleUI.printSuccess("30 jugadores creados y asignados");

        // ─── 3. Crear torneo ───────────────────────────
        Torneo torneo = new Torneo();
        torneo.setNombre("Torneo Clausura 2026");
        torneo.setSede("Costa Rica");
        torneo.setFechaInicio("2026-01-15");
        torneo.setFechaFin("2026-06-30");
        Torneo torneoCreado = torneoService.crearTorneo(torneo);

        for (String equipoId : equipoIds) {
            torneoService.agregarEquipoATorneo(torneoCreado.getId(), equipoId);
        }
        torneoService.activarTorneo(torneoCreado.getId());
        ConsoleUI.printSuccess("Torneo '" + torneoCreado.getNombre() + "' creado con 6 equipos");

        // ─── 4. Generar partidos round-robin ───────────
        List<Partido> partidos = partidoService.generarPartidos(torneoCreado.getId());
        ConsoleUI.printSuccess(partidos.size() + " partidos generados (round-robin)");

        // ─── 5. Simular resultados aleatorios ──────────
        Random rnd = new Random(42); // seed para reproducibilidad
        for (Partido p : partidos) {
            int golesLocal = rnd.nextInt(5);
            int golesVisitante = rnd.nextInt(4);
            partidoService.registrarResultado(p.getId(), golesLocal, golesVisitante);
        }
        ConsoleUI.printSuccess("Resultados simulados para todos los partidos");

        // ─── 6. Generar estadísticas y tabla ───────────
        Estadistica est = estadisticaService.generarEstadisticas(torneoCreado.getId());
        ConsoleUI.printSuccess("Estadísticas generadas");

        // Mostrar tabla de posiciones
        if (est.getTabla() != null && !est.getTabla().isEmpty()) {
            System.out.println();
            ConsoleUI.printInfo("TABLA DE POSICIONES - " + torneoCreado.getNombre());
            String[] headers = {"#", "Equipo", "PTS", "PJ", "GF", "GC", "DIF"};
            String[][] rows = new String[est.getTabla().size()][];
            for (int i = 0; i < est.getTabla().size(); i++) {
                TablaPosicionItem t = est.getTabla().get(i);
                // Buscar nombre del equipo
                Equipo eq = equipoService.buscarPorId(t.getEquipoId());
                String nombre = eq != null ? eq.getNombre() : t.getEquipoId();
                rows[i] = new String[]{
                        String.valueOf(i + 1),
                        nombre,
                        String.valueOf(t.getPuntos()),
                        String.valueOf(t.getPartidosJugados()),
                        String.valueOf(t.getGolesFavor()),
                        String.valueOf(t.getGolesContra()),
                        String.valueOf(t.getGolesFavor() - t.getGolesContra())
                };
            }
            ConsoleUI.printTable(headers, rows);
        }

        ConsoleUI.printSuccess("¡Datos de prueba cargados exitosamente!");
    }
}
