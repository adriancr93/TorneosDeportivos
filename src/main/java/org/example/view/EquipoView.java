package org.example.view;

import org.example.controller.TorneoController;
import org.example.model.Equipo;

import java.util.List;
import java.util.Scanner;

import static org.example.view.ConsoleUI.*;

public class EquipoView {
    private final Scanner scanner;
    private final TorneoController controller;

    public EquipoView(Scanner scanner, TorneoController controller) {
        this.scanner = scanner;
        this.controller = controller;
    }

    public void mostrarMenu() {
        int opcion;
        do {
            printSubHeader("⚽ GESTIÓN DE EQUIPOS");
            printMenuOption(1, "Crear equipo");
            printMenuOption(2, "Editar equipo");
            printMenuOption(3, "Eliminar equipo");
            printMenuOption(4, "Listar equipos");
            printMenuExit();
            printSeparator();
            opcion = leerEntero(scanner, "Seleccione una opción: ");
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

    private void crearEquipo() {
        printSubHeader("CREAR EQUIPO");
        String nombre = leerTexto(scanner, "Nombre del equipo: ");
        String ciudad = leerTexto(scanner, "Ciudad: ");
        int anio = leerEntero(scanner, "Año de fundación: ");
        String entrenador = leerTexto(scanner, "Entrenador: ");

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
        String id = leerTexto(scanner, "ID del equipo a editar: ");
        String nombre = leerTexto(scanner, "Nuevo nombre (enter para omitir): ");
        String ciudad = leerTexto(scanner, "Nueva ciudad (enter para omitir): ");
        String entrenador = leerTexto(scanner, "Nuevo entrenador (enter para omitir): ");

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
            printError("No se encontró el equipo con ese ID.");
        }
    }

    private void eliminarEquipo() {
        printSubHeader("ELIMINAR EQUIPO");
        String id = leerTexto(scanner, "ID del equipo a eliminar: ");
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
        String[] headers = {"ID", "Nombre", "Ciudad", "Año", "Entrenador", "PTS", "GF", "GC"};
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
}
