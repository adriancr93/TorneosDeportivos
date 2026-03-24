package org.example.view;

import org.example.controller.TorneoController;
import org.example.model.Torneo;

import java.util.List;
import java.util.Scanner;

import static org.example.view.ConsoleUI.*;

public class TorneoView {
    private final Scanner scanner;
    private final TorneoController controller;

    public TorneoView(Scanner scanner, TorneoController controller) {
        this.scanner = scanner;
        this.controller = controller;
    }

    public void mostrarMenu() {
        int opcion;
        do {
            printSubHeader("🏆 GESTIÓN DE TORNEOS");
            printMenuOption(1, "Crear torneo");
            printMenuOption(2, "Agregar equipo a torneo");
            printMenuOption(3, "Activar torneo");
            printMenuOption(4, "Finalizar torneo");
            printMenuOption(5, "Listar torneos");
            printMenuExit();
            printSeparator();
            opcion = leerEntero(scanner, "Seleccione una opción: ");
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

    private void crearTorneo() {
        printSubHeader("CREAR TORNEO");
        String nombre = leerTexto(scanner, "Nombre del torneo: ");
        String sede = leerTexto(scanner, "Sede: ");

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
        String torneoId = leerTexto(scanner, "ID del torneo: ");
        String equipoId = leerTexto(scanner, "ID del equipo: ");
        boolean ok = controller.agregarEquipoATorneo(torneoId, equipoId);
        if (ok) {
            printSuccess("Equipo agregado al torneo correctamente.");
        } else {
            printError("No se pudo agregar el equipo. Verifique que el torneo esté en estado CREADO.");
        }
    }

    private void activarTorneo() {
        printSubHeader("ACTIVAR TORNEO");
        String torneoId = leerTexto(scanner, "ID del torneo: ");
        boolean ok = controller.activarTorneo(torneoId);
        if (ok) {
            printSuccess("Torneo activado correctamente.");
        } else {
            printError("No se pudo activar el torneo. Verifique que esté en estado CREADO.");
        }
    }

    private void finalizarTorneo() {
        printSubHeader("FINALIZAR TORNEO");
        String torneoId = leerTexto(scanner, "ID del torneo: ");
        boolean ok = controller.finalizarTorneo(torneoId);
        if (ok) {
            printSuccess("Torneo finalizado correctamente.");
        } else {
            printError("No se pudo finalizar el torneo. Verifique que esté en estado ACTIVO.");
        }
    }

    private void listarTorneos() {
        printSubHeader("LISTADO DE TORNEOS");
        List<Torneo> lista = controller.listarTorneos();
        if (lista == null || lista.isEmpty()) {
            printWarning("No hay torneos registrados.");
            return;
        }
        String[] headers = {"ID", "Nombre", "Sede", "Estado", "# Equipos"};
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
}
