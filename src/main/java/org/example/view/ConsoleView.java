package org.example.view;

import org.example.controller.MainController;
import org.example.model.*;
import org.example.util.LoggerUtil;

import java.util.List;
import java.util.Scanner;

public class ConsoleView {
    private final MainController controller;
    private final Scanner scanner;

    public ConsoleView(MainController controller) {
        this.controller = controller;
        this.scanner = new Scanner(System.in);
    }

    public void mostrarMenu() {
        int opcion;
        do {
            imprimirBanner();
            opcion = leerEntero("Seleccione una opción: ");
            try {
                ejecutarOpcion(opcion);
            } catch (Exception e) {
                System.out.println("Ocurrió un error procesando la opción. Consulte logs.");
                LoggerUtil.logError("Error en opción de menú: " + opcion, e);
            }
        } while (opcion != 0);
        System.out.println("Hasta pronto.");
    }

    private void imprimirBanner() {
        System.out.println("\n=== Sistema de Gestión de Torneos Deportivos ===");
        System.out.println("1) Equipos - Crear");
        System.out.println("2) Equipos - Editar");
        System.out.println("3) Equipos - Eliminar");
        System.out.println("4) Equipos - Listar");
        System.out.println("5) Jugadores - Registrar");
        System.out.println("6) Jugadores - Asociar a equipo");
        System.out.println("7) Jugadores - Listar por equipo");
        System.out.println("8) Torneos - Crear");
        System.out.println("9) Torneos - Agregar equipo");
        System.out.println("10) Torneos - Activar");
        System.out.println("11) Torneos - Finalizar");
        System.out.println("12) Torneos - Listar");
        System.out.println("13) Partidos - Generar fixture");
        System.out.println("14) Partidos - Registrar resultado");
        System.out.println("15) Partidos - Listar por torneo");
        System.out.println("16) Estadísticas - Generar");
        System.out.println("17) Estadísticas - Visualizar");
        System.out.println("18) Reporte - Equipo con más goles");
        System.out.println("19) Reporte - Jugador con más goles");
        System.out.println("20) Reporte - Historial torneos");
        System.out.println("0) Salir");
    }

    private void ejecutarOpcion(int opcion) {
        switch (opcion) {
            case 1 -> crearEquipo();
            case 2 -> editarEquipo();
            case 3 -> eliminarEquipo();
            case 4 -> listarEquipos();
            case 5 -> registrarJugador();
            case 6 -> asociarJugador();
            case 7 -> listarJugadoresPorEquipo();
            case 8 -> crearTorneo();
            case 9 -> agregarEquipoATorneo();
            case 10 -> activarTorneo();
            case 11 -> finalizarTorneo();
            case 12 -> listarTorneos();
            case 13 -> generarPartidos();
            case 14 -> registrarResultado();
            case 15 -> listarPartidosPorTorneo();
            case 16 -> generarEstadisticas();
            case 17 -> visualizarEstadisticas();
            case 18 -> equipoMasGoles();
            case 19 -> jugadorMasGoles();
            case 20 -> historialTorneos();
            case 0 -> {
            }
            default -> System.out.println("Opción inválida.");
        }
    }

    // --- Equipos ---
    private void crearEquipo() {
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
        System.out.println(creado != null ? "Equipo creado: " + creado : "No se pudo crear el equipo.");
    }

    private void editarEquipo() {
        String id = leerTexto("ID del equipo a editar: ");
        String nombre = leerTexto("Nuevo nombre: ");
        Equipo equipo = new Equipo();
        equipo.setNombre(nombre);
        Equipo editado = controller.editarEquipo(id, equipo);
        System.out.println(editado != null ? "Equipo editado: " + editado : "No se pudo editar el equipo.");
    }

    private void eliminarEquipo() {
        String id = leerTexto("ID del equipo a eliminar: ");
        boolean ok = controller.eliminarEquipo(id);
        System.out.println(ok ? "Equipo eliminado." : "No se pudo eliminar el equipo.");
    }

    private void listarEquipos() {
        List<Equipo> lista = controller.listarEquipos();
        if (lista == null || lista.isEmpty()) {
            System.out.println("No hay equipos registrados.");
            return;
        }
        lista.forEach(System.out::println);
    }

    // --- Jugadores ---
    private void registrarJugador() {
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
        System.out.println(res != null ? "Jugador registrado: " + res : "No se pudo registrar jugador.");
    }

    private void asociarJugador() {
        String jugadorId = leerTexto("ID jugador: ");
        String equipoId = leerTexto("ID equipo: ");
        boolean ok = controller.asociarJugadorAEquipo(jugadorId, equipoId);
        System.out.println(ok ? "Jugador asociado." : "No se pudo asociar jugador.");
    }

    private void listarJugadoresPorEquipo() {
        String equipoId = leerTexto("ID equipo: ");
        List<Jugador> lista = controller.listarJugadoresPorEquipo(equipoId);
        if (lista == null || lista.isEmpty()) {
            System.out.println("No hay jugadores para este equipo.");
            return;
        }
        lista.forEach(System.out::println);
    }

    // --- Torneos ---
    private void crearTorneo() {
        String nombre = leerTexto("Nombre del torneo: ");
        String sede = leerTexto("Sede: ");
        Torneo t = new Torneo();
        t.setNombre(nombre);
        t.setSede(sede);
        Torneo res = controller.crearTorneo(t);
        System.out.println(res != null ? "Torneo creado: " + res : "No se pudo crear torneo.");
    }

    private void agregarEquipoATorneo() {
        String torneoId = leerTexto("ID torneo: ");
        String equipoId = leerTexto("ID equipo: ");
        boolean ok = controller.agregarEquipoATorneo(torneoId, equipoId);
        System.out.println(ok ? "Equipo agregado al torneo." : "No se pudo agregar equipo.");
    }

    private void activarTorneo() {
        String torneoId = leerTexto("ID torneo: ");
        boolean ok = controller.activarTorneo(torneoId);
        System.out.println(ok ? "Torneo activado." : "No se pudo activar torneo.");
    }

    private void finalizarTorneo() {
        String torneoId = leerTexto("ID torneo: ");
        boolean ok = controller.finalizarTorneo(torneoId);
        System.out.println(ok ? "Torneo finalizado." : "No se pudo finalizar torneo.");
    }

    private void listarTorneos() {
        List<Torneo> lista = controller.listarTorneos();
        if (lista == null || lista.isEmpty()) {
            System.out.println("No hay torneos registrados.");
            return;
        }
        lista.forEach(System.out::println);
    }

    // --- Partidos ---
    private void generarPartidos() {
        String torneoId = leerTexto("ID torneo: ");
        List<Partido> lista = controller.generarPartidos(torneoId);
        System.out.println((lista != null && !lista.isEmpty()) ? "Partidos generados:" : "No se generaron partidos.");
        if (lista != null) lista.forEach(System.out::println);
    }

    private void registrarResultado() {
        String partidoId = leerTexto("ID partido: ");
        int gLocal = leerEntero("Goles local: ");
        int gVisit = leerEntero("Goles visitante: ");
        boolean ok = controller.registrarResultado(partidoId, gLocal, gVisit);
        System.out.println(ok ? "Resultado registrado." : "No se pudo registrar resultado.");
    }

    private void listarPartidosPorTorneo() {
        String torneoId = leerTexto("ID torneo: ");
        List<Partido> lista = controller.listarPartidosPorTorneo(torneoId);
        if (lista == null || lista.isEmpty()) {
            System.out.println("No hay partidos para este torneo.");
            return;
        }
        lista.forEach(System.out::println);
    }

    // --- Estadísticas ---
    private void generarEstadisticas() {
        String torneoId = leerTexto("ID torneo: ");
        Estadistica e = controller.generarEstadisticas(torneoId);
        System.out.println(e != null ? "Estadísticas generadas: " + e : "No se pudo generar estadísticas.");
    }

    private void visualizarEstadisticas() {
        String torneoId = leerTexto("ID torneo: ");
        Estadistica e = controller.visualizarEstadisticas(torneoId);
        System.out.println(e != null ? e : "No hay estadísticas para este torneo.");
    }

    // --- Reportes ---
    private void equipoMasGoles() {
        String torneoId = leerTexto("ID torneo: ");
        Equipo e = controller.equipoConMasGoles(torneoId);
        System.out.println(e != null ? "Equipo con más goles: " + e : "No hay datos.");
    }

    private void jugadorMasGoles() {
        String torneoId = leerTexto("ID torneo: ");
        Jugador j = controller.jugadorConMasGoles(torneoId);
        System.out.println(j != null ? "Jugador con más goles: " + j : "No hay datos.");
    }

    private void historialTorneos() {
        List<String> h = controller.historialTorneos();
        if (h == null || h.isEmpty()) {
            System.out.println("No hay historial.");
            return;
        }
        h.forEach(System.out::println);
    }

    // --- Helpers ---
    private String leerTexto(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    private int leerEntero(String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextInt()) {
            System.out.print("Valor inválido. Intente de nuevo: ");
            scanner.next();
        }
        int val = scanner.nextInt();
        scanner.nextLine();
        return val;
    }
}
