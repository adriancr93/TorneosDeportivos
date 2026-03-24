package org.example.view;

import org.example.controller.TorneoController;
import org.example.model.Equipo;
import org.example.model.Jugador;

import java.util.List;
import java.util.Scanner;

import static org.example.view.ConsoleUI.*;

public class ReporteView {
    private final Scanner scanner;
    private final TorneoController controller;

    public ReporteView(Scanner scanner, TorneoController controller) {
        this.scanner = scanner;
        this.controller = controller;
    }

    public void mostrarMenu() {
        int opcion;
        do {
            printSubHeader("📈 REPORTES");
            printMenuOption(1, "Equipo con más goles");
            printMenuOption(2, "Jugador con más goles");
            printMenuOption(3, "Historial de torneos");
            printMenuExit();
            printSeparator();
            opcion = leerEntero(scanner, "Seleccione una opción: ");
            switch (opcion) {
                case 1 -> equipoMasGoles();
                case 2 -> jugadorMasGoles();
                case 3 -> historialTorneos();
                case 0 -> { }
                default -> printWarning("Opción inválida.");
            }
        } while (opcion != 0);
    }

    private void equipoMasGoles() {
        printSubHeader("EQUIPO CON MÁS GOLES");
        String torneoId = leerTexto(scanner, "ID del torneo: ");
        Equipo e = controller.equipoConMasGoles(torneoId);
        if (e != null) {
            printSuccess("Equipo con más goles:");
            String[] headers = {"ID", "Nombre", "Ciudad", "GF", "GC", "PTS"};
            String[][] rows = {
                    {
                            e.getId() != null ? e.getId() : "-",
                            e.getNombre() != null ? e.getNombre() : "-",
                            e.getCiudad() != null ? e.getCiudad() : "-",
                            String.valueOf(e.getGolesFavor()),
                            String.valueOf(e.getGolesContra()),
                            String.valueOf(e.getPuntos())
                    }
            };
            printTable(headers, rows);
        } else {
            printWarning("No hay datos disponibles para este torneo.");
        }
    }

    private void jugadorMasGoles() {
        printSubHeader("JUGADOR CON MÁS GOLES");
        String torneoId = leerTexto(scanner, "ID del torneo: ");
        Jugador j = controller.jugadorConMasGoles(torneoId);
        if (j != null) {
            printSuccess("Jugador con más goles:");
            String[] headers = {"ID", "Nombre", "Goles", "Equipo", "Posición"};
            String[][] rows = {
                    {
                            j.getId() != null ? j.getId() : "-",
                            j.getNombre() != null ? j.getNombre() : "-",
                            String.valueOf(j.getGoles()),
                            j.getEquipoId() != null ? j.getEquipoId() : "Sin equipo",
                            j.getPosicion() != null ? j.getPosicion() : "-"
                    }
            };
            printTable(headers, rows);
        } else {
            printWarning("No hay datos disponibles para este torneo.");
        }
    }

    private void historialTorneos() {
        printSubHeader("HISTORIAL DE TORNEOS");
        List<String> historial = controller.historialTorneos();
        if (historial == null || historial.isEmpty()) {
            printWarning("No hay historial de torneos.");
            return;
        }
        String[] headers = {"#", "Torneo"};
        String[][] rows = new String[historial.size()][];
        for (int i = 0; i < historial.size(); i++) {
            rows[i] = new String[]{String.valueOf(i + 1), historial.get(i)};
        }
        printTable(headers, rows);
        printInfo("Total: " + historial.size() + " torneo(s) en historial");
    }
}
