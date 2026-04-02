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
            printMenuOption(4, "Simular torneo completo");
            printMenuExit();
            printSeparator();
            opcion = leerEntero(scanner, "Seleccione una opción: ");
            switch (opcion) {
                case 1 -> generarPartidos();
                case 2 -> registrarResultado();
                case 3 -> listarPartidosPorTorneo();
                case 4 -> simularTorneo();
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
        if (gLocal == gVisit) {
            printWarning("En formato eliminatorio no se permiten empates. Ingrese un resultado diferente.");
            return;
        }
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

    private void simularTorneo() {
        printSubHeader("SIMULAR TORNEO COMPLETO");
        String torneoId = leerTexto(scanner, "ID del torneo: ");
        List<Partido> resultado = controller.simularTorneo(torneoId);
        if (resultado == null || resultado.isEmpty()) {
            printError("No se pudo simular el torneo. Verifique que tenga al menos 2 equipos.");
            return;
        }
        printSuccess("Torneo simulado – " + resultado.size() + " partido(s) jugados");
        mostrarTablaPartidos(resultado);
    }

    private void mostrarTablaPartidos(List<Partido> lista) {
        String[] headers = {"ID", "Ronda", "Local", "Goles L", "Goles V", "Visitante", "Estado"};
        String[][] rows = lista.stream().map(p -> new String[]{
                p.getId() != null ? p.getId() : "-",
                p.getRonda() != null ? p.getRonda() : "-",
                p.getEquipoLocalId() != null ? p.getEquipoLocalId() : "-",
                String.valueOf(p.getGolesLocal()),
                String.valueOf(p.getGolesVisitante()),
                p.getEquipoVisitanteId() != null ? p.getEquipoVisitanteId() : "-",
                p.getEstado() != null ? p.getEstado().name() : "-"
        }).toArray(String[][]::new);
        printTable(headers, rows);
        printInfo("Total: " + lista.size() + " partido(s)");
    }
}
