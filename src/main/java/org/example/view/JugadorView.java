package org.example.view;

import org.example.controller.TorneoController;
import org.example.model.Jugador;

import java.util.List;
import java.util.Scanner;

import static org.example.view.ConsoleUI.*;

public class JugadorView {
    private final Scanner scanner;
    private final TorneoController controller;

    public JugadorView(Scanner scanner, TorneoController controller) {
        this.scanner = scanner;
        this.controller = controller;
    }

    public void mostrarMenu() {
        int opcion;
        do {
            printSubHeader("🏃 GESTIÓN DE JUGADORES");
            printMenuOption(1, "Registrar jugador");
            printMenuOption(2, "Asociar jugador a equipo");
            printMenuOption(3, "Listar jugadores por equipo");
            printMenuExit();
            printSeparator();
            opcion = leerEntero(scanner, "Seleccione una opción: ");
            switch (opcion) {
                case 1 -> registrarJugador();
                case 2 -> asociarJugador();
                case 3 -> listarJugadoresPorEquipo();
                case 0 -> { }
                default -> printWarning("Opción inválida.");
            }
        } while (opcion != 0);
    }

    private void registrarJugador() {
        printSubHeader("REGISTRAR JUGADOR");
        String nombre = leerTexto(scanner, "Nombre del jugador: ");
        int edad = leerEntero(scanner, "Edad: ");
        String posicion = leerTexto(scanner, "Posición: ");
        int dorsal = leerEntero(scanner, "Dorsal: ");

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
        String jugadorId = leerTexto(scanner, "ID del jugador: ");
        String equipoId = leerTexto(scanner, "ID del equipo: ");
        boolean ok = controller.asociarJugadorAEquipo(jugadorId, equipoId);
        if (ok) {
            printSuccess("Jugador asociado al equipo correctamente.");
        } else {
            printError("No se pudo asociar el jugador al equipo.");
        }
    }

    private void listarJugadoresPorEquipo() {
        printSubHeader("JUGADORES POR EQUIPO");
        String equipoId = leerTexto(scanner, "ID del equipo: ");
        List<Jugador> lista = controller.listarJugadoresPorEquipo(equipoId);
        if (lista == null || lista.isEmpty()) {
            printWarning("No hay jugadores registrados para este equipo.");
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
}
