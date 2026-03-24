package org.example.view;

import org.example.controller.TorneoController;
import org.example.model.Partido;

import java.util.List;
import java.util.Scanner;

import static org.example.view.ConsoleUI.*;

public class PartidoView {
    private final Scanner scanner;
    private final TorneoController controller;

    public PartidoView(Scanner scanner, TorneoController controller) {
        this.scanner = scanner;
        this.controller = controller;
    }

    public void mostrarMenu() {
        int opcion;
        do {
            printSubHeader("📋 GESTIÓN DE PARTIDOS");
            printMenuOption(1, "Generar fixture");
            printMenuOption(2, "Registrar resultado");
            printMenuOption(3, "Listar partidos por torneo");
            printMenuExit();
            printSeparator();
            opcion = leerEntero(scanner, "Seleccione una opción: ");
            switch (opcion) {
                case 1 -> generarPartidos();
                case 2 -> registrarResultado();
                case 3 -> listarPartidosPorTorneo();
                case 0 -> { }
                default -> printWarning("Opción inválida.");
            }
        } while (opcion != 0);
    }

    private void generarPartidos() {
        printSubHeader("GENERAR FIXTURE");
        String torneoId = leerTexto(scanner, "ID del torneo: ");
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
        String partidoId = leerTexto(scanner, "ID del partido: ");
        int gLocal = leerEntero(scanner, "Goles equipo local: ");
        int gVisit = leerEntero(scanner, "Goles equipo visitante: ");
        boolean ok = controller.registrarResultado(partidoId, gLocal, gVisit);
        if (ok) {
            printSuccess("Resultado registrado correctamente.");
        } else {
            printError("No se pudo registrar el resultado.");
        }
    }

    private void listarPartidosPorTorneo() {
        printSubHeader("PARTIDOS POR TORNEO");
        String torneoId = leerTexto(scanner, "ID del torneo: ");
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
}
