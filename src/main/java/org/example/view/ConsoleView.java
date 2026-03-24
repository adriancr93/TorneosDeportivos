package org.example.view;

import org.example.controller.TorneoController;
import org.example.model.*;
import org.example.util.LoggerUtil;

import java.util.List;
import java.util.Scanner;

import static org.example.view.ConsoleUI.*;

public class ConsoleView {
    private final TorneoController controller;
    private final Scanner scanner;

    public ConsoleView(TorneoController controller) {
        this.controller = controller;
        this.scanner = new Scanner(System.in);
    }

    // ─── Menú principal ────────────────────────────────

    public void mostrarMenu() {
        int opcion;
        do {
            imprimirMenuPrincipal();
            opcion = leerEntero("Seleccione una opción: ");
            try {
                ejecutarOpcionPrincipal(opcion);
            } catch (Exception e) {
                printError("Ocurrió un error procesando la opción. Consulte logs.");
                LoggerUtil.logError("Error en opción de menú: " + opcion, e);
            }
        } while (opcion != 0);
        printHeader("¡Hasta pronto!");
    }

    private void imprimirMenuPrincipal() {
        printHeader("⚽  SISTEMA DE GESTIÓN DE TORNEOS DEPORTIVOS  ⚽");
        System.out.println();
        printMenuOption(1, "Gestión de Equipos");
        printMenuOption(2, "Gestión de Jugadores");
        printMenuOption(3, "Gestión de Torneos");
        printMenuOption(4, "Gestión de Partidos");
        printMenuOption(5, "Estadísticas");
        printMenuOption(6, "Reportes");
        printMenuExit();
        printSeparator();
    }

    private void ejecutarOpcionPrincipal(int opcion) {
        switch (opcion) {
            case 1 -> menuEquipos();
            case 2 -> menuJugadores();
            case 3 -> menuTorneos();
            case 4 -> menuPartidos();
            case 5 -> menuEstadisticas();
            case 6 -> menuReportes();
            case 0 -> { }
            default -> printWarning("Opción inválida. Intente de nuevo.");
        }
    }

    // ─── Submenú Equipos ───────────────────────────────

    private void menuEquipos() {
        int opcion;
        do {
            printSubHeader("GESTIÓN DE EQUIPOS");
            printMenuOption(1, "Crear equipo");
            printMenuOption(2, "Editar equipo");
            printMenuOption(3, "Eliminar equipo");
            printMenuOption(4, "Listar equipos");
            printMenuExit();
            printSeparator();
            opcion = leerEntero("Seleccione una opción: ");
            switch (opcion) {
                case 1 -> crearEquipo();
                case 2 -> editarEquipo();
                case 3 -> eliminarEquipo();
                case 4 -> listarEquipos();
                case 0 -> { }
                default -> printWarning("Opción inválida.");
            }
        } while (opcion != 0);
    }

    // ─── Submenú Jugadores ─────────────────────────────

    private void menuJugadores() {
        int opcion;
        do {
            printSubHeader("GESTIÓN DE JUGADORES");
            printMenuOption(1, "Registrar jugador");
            printMenuOption(2, "Asociar jugador a equipo");
            printMenuOption(3, "Listar jugadores por equipo");
            printMenuExit();
            printSeparator();
            opcion = leerEntero("Seleccione una opción: ");
            switch (opcion) {
                case 1 -> registrarJugador();
                case 2 -> asociarJugador();
                case 3 -> listarJugadoresPorEquipo();
                case 0 -> { }
                default -> printWarning("Opción inválida.");
            }
        } while (opcion != 0);
    }

    // ─── Submenú Torneos ───────────────────────────────

    private void menuTorneos() {
        int opcion;
        do {
            printSubHeader("GESTIÓN DE TORNEOS");
            printMenuOption(1, "Crear torneo");
            printMenuOption(2, "Agregar equipo a torneo");
            printMenuOption(3, "Activar torneo");
            printMenuOption(4, "Finalizar torneo");
            printMenuOption(5, "Listar torneos");
            printMenuExit();
            printSeparator();
            opcion = leerEntero("Seleccione una opción: ");
            switch (opcion) {
                case 1 -> crearTorneo();
                case 2 -> agregarEquipoATorneo();
                case 3 -> activarTorneo();
                case 4 -> finalizarTorneo();
                case 5 -> listarTorneos();
                case 0 -> { }
                default -> printWarning("Opción inválida.");
            }
        } while (opcion != 0);
    }

    // ─── Submenú Partidos ──────────────────────────────

    private void menuPartidos() {
        int opcion;
        do {
            printSubHeader("GESTIÓN DE PARTIDOS");
            printMenuOption(1, "Generar fixture");
            printMenuOption(2, "Registrar resultado");
            printMenuOption(3, "Listar partidos por torneo");
            printMenuExit();
            printSeparator();
            opcion = leerEntero("Seleccione una opción: ");
            switch (opcion) {
                case 1 -> generarPartidos();
                case 2 -> registrarResultado();
                case 3 -> listarPartidosPorTorneo();
                case 0 -> { }
                default -> printWarning("Opción inválida.");
            }
        } while (opcion != 0);
    }

    // ─── Submenú Estadísticas ──────────────────────────

    private void menuEstadisticas() {
        int opcion;
        do {
            printSubHeader("ESTADÍSTICAS");
            printMenuOption(1, "Generar estadísticas");
            printMenuOption(2, "Visualizar estadísticas");
            printMenuExit();
            printSeparator();
            opcion = leerEntero("Seleccione una opción: ");
            switch (opcion) {
                case 1 -> generarEstadisticas();
                case 2 -> visualizarEstadisticas();
                case 0 -> { }
                default -> printWarning("Opción inválida.");
            }
        } while (opcion != 0);
    }

    // ─── Submenú Reportes ──────────────────────────────

    private void menuReportes() {
        int opcion;
        do {
            printSubHeader("REPORTES");
            printMenuOption(1, "Equipo con más goles");
            printMenuOption(2, "Jugador con más goles");
            printMenuOption(3, "Historial de torneos");
            printMenuExit();
            printSeparator();
            opcion = leerEntero("Seleccione una opción: ");
            switch (opcion) {
                case 1 -> equipoMasGoles();
                case 2 -> jugadorMasGoles();
                case 3 -> historialTorneos();
                case 0 -> { }
                default -> printWarning("Opción inválida.");
            }
        } while (opcion != 0);
    }

    // ═══════════════════════════════════════════════════
    //  Operaciones de Equipos
    // ═══════════════════════════════════════════════════

    private void crearEquipo() {
        printSubHeader("CREAR EQUIPO");
        String nombre = leerTexto("Nombre del equipo: ");
        String ciudad = leerTexto("Ciudad: ");
        int anio = leerEntero("Año de fundación: ");
        String entrenador = leerTexto("Entrenador: ");
        Equipo equipo = new Equipo();
        equipo.setNombre(nombre);
        equipo.setCiudad(ciudad);
        equipo.setAnioFundacion(anio);
        equipo.setEntrenador(entrenador);
        Equipo creado = controller.crearEquipo(equipo);
        if (creado != null) {
            printSuccess("Equipo creado exitosamente");
            printDetail("ID", creado.getId());
            printDetail("Nombre", creado.getNombre());
            printDetail("Ciudad", creado.getCiudad());
            printDetail("Entrenador", creado.getEntrenador());
        } else {
            printError("No se pudo crear el equipo.");
        }
    }

    private void editarEquipo() {
        printSubHeader("EDITAR EQUIPO");
        String id = leerTexto("ID del equipo a editar: ");
        String nombre = leerTexto("Nuevo nombre: ");
        String ciudad = leerTexto("Nueva ciudad (enter para omitir): ");
        String entrenador = leerTexto("Nuevo entrenador (enter para omitir): ");
        Equipo equipo = new Equipo();
        if (!nombre.isEmpty()) equipo.setNombre(nombre);
        if (!ciudad.isEmpty()) equipo.setCiudad(ciudad);
        if (!entrenador.isEmpty()) equipo.setEntrenador(entrenador);
        Equipo editado = controller.editarEquipo(id, equipo);
        if (editado != null) {
            printSuccess("Equipo editado exitosamente");
            printDetail("ID", editado.getId());
            printDetail("Nombre", editado.getNombre());
        } else {
            printError("No se pudo editar el equipo.");
        }
    }

    private void eliminarEquipo() {
        printSubHeader("ELIMINAR EQUIPO");
        String id = leerTexto("ID del equipo a eliminar: ");
        boolean ok = controller.eliminarEquipo(id);
        if (ok) {
            printSuccess("Equipo eliminado correctamente.");
        } else {
            printError("No se pudo eliminar el equipo.");
        }
    }

    private void listarEquipos() {
        printSubHeader("LISTADO DE EQUIPOS");
        List<Equipo> lista = controller.listarEquipos();
        if (lista == null || lista.isEmpty()) {
            printWarning("No hay equipos registrados.");
            return;
        }
        String[] headers = {"ID", "Nombre", "Ciudad", "Fundación", "Entrenador", "PTS", "GF", "GC"};
        String[][] rows = lista.stream().map(e -> new String[]{
                e.getId() != null ? e.getId() : "-",
                e.getNombre() != null ? e.getNombre() : "-",
                e.getCiudad() != null ? e.getCiudad() : "-",
                String.valueOf(e.getAnioFundacion()),
                e.getEntrenador() != null ? e.getEntrenador() : "-",
                String.valueOf(e.getPuntos()),
                String.valueOf(e.getGolesFavor()),
                String.valueOf(e.getGolesContra())
        }).toArray(String[][]::new);
        printTable(headers, rows);
        printInfo("Total: " + lista.size() + " equipo(s)");
    }

    // ═══════════════════════════════════════════════════
    //  Operaciones de Jugadores
    // ═══════════════════════════════════════════════════

    private void registrarJugador() {
        printSubHeader("REGISTRAR JUGADOR");
        String nombre = leerTexto("Nombre del jugador: ");
        int edad = leerEntero("Edad: ");
        String posicion = leerTexto("Posición: ");
        int dorsal = leerEntero("Dorsal: ");
        Jugador j = new Jugador();
        j.setNombre(nombre);
        j.setEdad(edad);
        j.setPosicion(posicion);
        j.setDorsal(dorsal);
        Jugador res = controller.registrarJugador(j);
        if (res != null) {
            printSuccess("Jugador registrado exitosamente");
            printDetail("ID", res.getId());
            printDetail("Nombre", res.getNombre());
            printDetail("Posición", res.getPosicion());
            printDetail("Dorsal", String.valueOf(res.getDorsal()));
        } else {
            printError("No se pudo registrar el jugador.");
        }
    }

    private void asociarJugador() {
        printSubHeader("ASOCIAR JUGADOR A EQUIPO");
        String jugadorId = leerTexto("ID del jugador: ");
        String equipoId = leerTexto("ID del equipo: ");
        boolean ok = controller.asociarJugadorAEquipo(jugadorId, equipoId);
        if (ok) {
            printSuccess("Jugador asociado al equipo correctamente.");
        } else {
            printError("No se pudo asociar el jugador.");
        }
    }

    private void listarJugadoresPorEquipo() {
        printSubHeader("JUGADORES POR EQUIPO");
        String equipoId = leerTexto("ID del equipo: ");
        List<Jugador> lista = controller.listarJugadoresPorEquipo(equipoId);
        if (lista == null || lista.isEmpty()) {
            printWarning("No hay jugadores para este equipo.");
            return;
        }
        String[] headers = {"ID", "Nombre", "Edad", "Posición", "Dorsal", "Goles"};
        String[][] rows = lista.stream().map(j -> new String[]{
                j.getId() != null ? j.getId() : "-",
                j.getNombre() != null ? j.getNombre() : "-",
                String.valueOf(j.getEdad()),
                j.getPosicion() != null ? j.getPosicion() : "-",
                String.valueOf(j.getDorsal()),
                String.valueOf(j.getGoles())
        }).toArray(String[][]::new);
        printTable(headers, rows);
        printInfo("Total: " + lista.size() + " jugador(es)");
    }

    // ═══════════════════════════════════════════════════
    //  Operaciones de Torneos
    // ═══════════════════════════════════════════════════

    private void crearTorneo() {
        printSubHeader("CREAR TORNEO");
        String nombre = leerTexto("Nombre del torneo: ");
        String sede = leerTexto("Sede: ");
        Torneo t = new Torneo();
        t.setNombre(nombre);
        t.setSede(sede);
        Torneo res = controller.crearTorneo(t);
        if (res != null) {
            printSuccess("Torneo creado exitosamente");
            printDetail("ID", res.getId());
            printDetail("Nombre", res.getNombre());
            printDetail("Sede", res.getSede());
            printDetail("Estado", String.valueOf(res.getEstado()));
        } else {
            printError("No se pudo crear el torneo.");
        }
    }

    private void agregarEquipoATorneo() {
        printSubHeader("AGREGAR EQUIPO A TORNEO");
        String torneoId = leerTexto("ID del torneo: ");
        String equipoId = leerTexto("ID del equipo: ");
        boolean ok = controller.agregarEquipoATorneo(torneoId, equipoId);
        if (ok) {
            printSuccess("Equipo agregado al torneo correctamente.");
        } else {
            printError("No se pudo agregar el equipo al torneo.");
        }
    }

    private void activarTorneo() {
        printSubHeader("ACTIVAR TORNEO");
        String torneoId = leerTexto("ID del torneo: ");
        boolean ok = controller.activarTorneo(torneoId);
        if (ok) {
            printSuccess("Torneo activado correctamente.");
        } else {
            printError("No se pudo activar el torneo.");
        }
    }

    private void finalizarTorneo() {
        printSubHeader("FINALIZAR TORNEO");
        String torneoId = leerTexto("ID del torneo: ");
        boolean ok = controller.finalizarTorneo(torneoId);
        if (ok) {
            printSuccess("Torneo finalizado correctamente.");
        } else {
            printError("No se pudo finalizar el torneo.");
        }
    }

    private void listarTorneos() {
        printSubHeader("LISTADO DE TORNEOS");
        List<Torneo> lista = controller.listarTorneos();
        if (lista == null || lista.isEmpty()) {
            printWarning("No hay torneos registrados.");
            return;
        }
        String[] headers = {"ID", "Nombre", "Sede", "Estado", "Equipos"};
        String[][] rows = lista.stream().map(t -> new String[]{
                t.getId() != null ? t.getId() : "-",
                t.getNombre() != null ? t.getNombre() : "-",
                t.getSede() != null ? t.getSede() : "-",
                t.getEstado() != null ? t.getEstado().name() : "-",
                t.getEquipoIds() != null ? String.valueOf(t.getEquipoIds().size()) : "0"
        }).toArray(String[][]::new);
        printTable(headers, rows);
        printInfo("Total: " + lista.size() + " torneo(s)");
    }

    // ═══════════════════════════════════════════════════
    //  Operaciones de Partidos
    // ═══════════════════════════════════════════════════

    private void generarPartidos() {
        printSubHeader("GENERAR FIXTURE");
        String torneoId = leerTexto("ID del torneo: ");
        List<Partido> lista = controller.generarPartidos(torneoId);
        if (lista != null && !lista.isEmpty()) {
            printSuccess("Se generaron " + lista.size() + " partido(s)");
            mostrarTablaPartidos(lista);
        } else {
            printError("No se generaron partidos. Verifique que el torneo tenga al menos 2 equipos.");
        }
    }

    private void registrarResultado() {
        printSubHeader("REGISTRAR RESULTADO");
        String partidoId = leerTexto("ID del partido: ");
        int gLocal = leerEntero("Goles equipo local: ");
        int gVisit = leerEntero("Goles equipo visitante: ");
        boolean ok = controller.registrarResultado(partidoId, gLocal, gVisit);
        if (ok) {
            printSuccess("Resultado registrado correctamente.");
        } else {
            printError("No se pudo registrar el resultado.");
        }
    }

    private void listarPartidosPorTorneo() {
        printSubHeader("PARTIDOS POR TORNEO");
        String torneoId = leerTexto("ID del torneo: ");
        List<Partido> lista = controller.listarPartidosPorTorneo(torneoId);
        if (lista == null || lista.isEmpty()) {
            printWarning("No hay partidos para este torneo.");
            return;
        }
        mostrarTablaPartidos(lista);
    }

    private void mostrarTablaPartidos(List<Partido> lista) {
        String[] headers = {"ID", "Local", "Goles L", "Goles V", "Visitante", "Estado", "Fecha"};
        String[][] rows = lista.stream().map(p -> new String[]{
                p.getId() != null ? p.getId() : "-",
                p.getEquipoLocalId() != null ? p.getEquipoLocalId() : "-",
                String.valueOf(p.getGolesLocal()),
                String.valueOf(p.getGolesVisitante()),
                p.getEquipoVisitanteId() != null ? p.getEquipoVisitanteId() : "-",
                p.getEstado() != null ? p.getEstado().name() : "-",
                p.getFecha() != null ? p.getFecha() : "-"
        }).toArray(String[][]::new);
        printTable(headers, rows);
        printInfo("Total: " + lista.size() + " partido(s)");
    }

    // ═══════════════════════════════════════════════════
    //  Operaciones de Estadísticas
    // ═══════════════════════════════════════════════════

    private void generarEstadisticas() {
        printSubHeader("GENERAR ESTADÍSTICAS");
        String torneoId = leerTexto("ID del torneo: ");
        Estadistica e = controller.generarEstadisticas(torneoId);
        if (e != null) {
            printSuccess("Estadísticas generadas para el torneo " + torneoId);
            mostrarEstadistica(e);
        } else {
            printError("No se pudieron generar estadísticas.");
        }
    }

    private void visualizarEstadisticas() {
        printSubHeader("VISUALIZAR ESTADÍSTICAS");
        String torneoId = leerTexto("ID del torneo: ");
        Estadistica e = controller.visualizarEstadisticas(torneoId);
        if (e != null) {
            mostrarEstadistica(e);
        } else {
            printWarning("No hay estadísticas generadas para este torneo.");
        }
    }

    private void mostrarEstadistica(Estadistica e) {
        printDetail("Torneo", e.getTorneoId());
        printDetail("Fecha", e.getFechaGeneracion());

        if (e.getTabla() != null && !e.getTabla().isEmpty()) {
            System.out.println();
            printInfo("TABLA DE POSICIONES");
            String[] hPos = {"#", "Equipo", "PTS", "GF", "GC", "DIF", "PJ"};
            String[][] rPos = new String[e.getTabla().size()][];
            for (int i = 0; i < e.getTabla().size(); i++) {
                TablaPosicionItem t = e.getTabla().get(i);
                rPos[i] = new String[]{
                        String.valueOf(i + 1),
                        t.getEquipoId() != null ? t.getEquipoId() : "-",
                        String.valueOf(t.getPuntos()),
                        String.valueOf(t.getGolesFavor()),
                        String.valueOf(t.getGolesContra()),
                        String.valueOf(t.getGolesFavor() - t.getGolesContra()),
                        String.valueOf(t.getPartidosJugados())
                };
            }
            printTable(hPos, rPos);
        }

        if (e.getGoleadores() != null && !e.getGoleadores().isEmpty()) {
            System.out.println();
            printInfo("TABLA DE GOLEADORES");
            String[] hGol = {"#", "Jugador", "Goles"};
            String[][] rGol = new String[e.getGoleadores().size()][];
            for (int i = 0; i < e.getGoleadores().size(); i++) {
                GoleadorItem g = e.getGoleadores().get(i);
                rGol[i] = new String[]{
                        String.valueOf(i + 1),
                        g.getJugadorId() != null ? g.getJugadorId() : "-",
                        String.valueOf(g.getGoles())
                };
            }
            printTable(hGol, rGol);
        }
    }

    // ═══════════════════════════════════════════════════
    //  Operaciones de Reportes
    // ═══════════════════════════════════════════════════

    private void equipoMasGoles() {
        printSubHeader("EQUIPO CON MÁS GOLES");
        String torneoId = leerTexto("ID del torneo: ");
        Equipo e = controller.equipoConMasGoles(torneoId);
        if (e != null) {
            printSuccess("Equipo con más goles:");
            printDetail("ID", e.getId());
            printDetail("Nombre", e.getNombre());
            printDetail("Ciudad", e.getCiudad() != null ? e.getCiudad() : "-");
            printDetail("Goles a favor", String.valueOf(e.getGolesFavor()));
        } else {
            printWarning("No hay datos disponibles.");
        }
    }

    private void jugadorMasGoles() {
        printSubHeader("JUGADOR CON MÁS GOLES");
        String torneoId = leerTexto("ID del torneo: ");
        Jugador j = controller.jugadorConMasGoles(torneoId);
        if (j != null) {
            printSuccess("Jugador con más goles:");
            printDetail("ID", j.getId());
            printDetail("Nombre", j.getNombre());
            printDetail("Goles", String.valueOf(j.getGoles()));
            printDetail("Equipo", j.getEquipoId() != null ? j.getEquipoId() : "Sin equipo");
        } else {
            printWarning("No hay datos disponibles.");
        }
    }

    private void historialTorneos() {
        printSubHeader("HISTORIAL DE TORNEOS");
        List<String> h = controller.historialTorneos();
        if (h == null || h.isEmpty()) {
            printWarning("No hay historial de torneos.");
            return;
        }
        for (int i = 0; i < h.size(); i++) {
            System.out.println(CYAN + "  " + (i + 1) + ". " + RESET + h.get(i));
        }
        printInfo("Total: " + h.size() + " torneo(s) en historial");
    }

    // ═══════════════════════════════════════════════════
    //  Helpers de entrada
    // ═══════════════════════════════════════════════════

    private String leerTexto(String label) {
        System.out.print(MAGENTA + " ▸ " + RESET + label);
        return scanner.nextLine();
    }

    private int leerEntero(String label) {
        System.out.print(MAGENTA + " ▸ " + RESET + label);
        while (!scanner.hasNextInt()) {
            printWarning("Valor inválido. Ingrese un número entero.");
            System.out.print(MAGENTA + " ▸ " + RESET + label);
            scanner.next();
        }
        int val = scanner.nextInt();
        scanner.nextLine();
        return val;
    }
}
