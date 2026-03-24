package org.example.view;

import org.example.controller.TorneoController;
import org.example.model.*;

import java.util.List;
import java.util.Scanner;

import static org.example.view.ConsoleUI.*;

public class EstadisticaView {
    private final Scanner scanner;
    private final TorneoController controller;

    public EstadisticaView(Scanner scanner, TorneoController controller) {
        this.scanner = scanner;
        this.controller = controller;
    }

    public void mostrarMenu() {
        int opcion;
        do {
            printSubHeader("📊 ESTADÍSTICAS");
            printMenuOption(1, "Generar estadísticas de torneo");
            printMenuOption(2, "Visualizar estadísticas de torneo");
            printMenuExit();
            printSeparator();
            opcion = leerEntero(scanner, "Seleccione una opción: ");
            switch (opcion) {
                case 1 -> generarEstadisticas();
                case 2 -> visualizarEstadisticas();
                case 0 -> { }
                default -> printWarning("Opción inválida.");
            }
        } while (opcion != 0);
    }

    private void generarEstadisticas() {
        printSubHeader("GENERAR ESTADÍSTICAS");
        String torneoId = leerTexto(scanner, "ID del torneo: ");
        Estadistica e = controller.generarEstadisticas(torneoId);
        if (e != null) {
            printSuccess("Estadísticas generadas correctamente");
            mostrarEstadistica(e);
        } else {
            printError("No se pudieron generar las estadísticas.");
        }
    }

    private void visualizarEstadisticas() {
        printSubHeader("VISUALIZAR ESTADÍSTICAS");
        String torneoId = leerTexto(scanner, "ID del torneo: ");
        Estadistica e = controller.visualizarEstadisticas(torneoId);
        if (e != null) {
            mostrarEstadistica(e);
        } else {
            printWarning("No hay estadísticas generadas para este torneo. Genérelas primero.");
        }
    }

    private void mostrarEstadistica(Estadistica e) {
        printDetail("Torneo", e.getTorneoId());
        printDetail("Fecha generación", e.getFechaGeneracion());

        if (e.getTabla() != null && !e.getTabla().isEmpty()) {
            System.out.println();
            printInfo("⚽ TABLA DE POSICIONES");
            String[] hPos = {"#", "Equipo", "PJ", "PG", "PE", "PP", "GF", "GC", "DIF", "PTS"};
            String[][] rPos = new String[e.getTabla().size()][];

            // Recalcular PG, PE, PP a partir de partidos del torneo
            List<Partido> partidos = controller.listarPartidosPorTorneo(e.getTorneoId());

            for (int i = 0; i < e.getTabla().size(); i++) {
                TablaPosicionItem t = e.getTabla().get(i);
                // Resolver nombre del equipo
                Equipo eq = controller.buscarEquipoPorId(t.getEquipoId());
                String nombre = eq != null ? eq.getNombre() : t.getEquipoId();

                // Calcular victorias, empates y derrotas
                int pg = 0, pe = 0, pp = 0;
                for (Partido p : partidos) {
                    if (p.getEstado() != PartidoEstado.JUGADO) continue;
                    boolean esLocal = t.getEquipoId().equals(p.getEquipoLocalId());
                    boolean esVisitante = t.getEquipoId().equals(p.getEquipoVisitanteId());
                    if (!esLocal && !esVisitante) continue;

                    if (p.getGolesLocal() == p.getGolesVisitante()) {
                        pe++;
                    } else if ((esLocal && p.getGolesLocal() > p.getGolesVisitante())
                            || (esVisitante && p.getGolesVisitante() > p.getGolesLocal())) {
                        pg++;
                    } else {
                        pp++;
                    }
                }

                rPos[i] = new String[]{
                        String.valueOf(i + 1),
                        nombre,
                        String.valueOf(t.getPartidosJugados()),
                        String.valueOf(pg),
                        String.valueOf(pe),
                        String.valueOf(pp),
                        String.valueOf(t.getGolesFavor()),
                        String.valueOf(t.getGolesContra()),
                        String.valueOf(t.getGolesFavor() - t.getGolesContra()),
                        String.valueOf(t.getPuntos())
                };
            }
            printTable(hPos, rPos);
        }

        if (e.getGoleadores() != null && !e.getGoleadores().isEmpty()) {
            System.out.println();
            printInfo("🥇 TABLA DE GOLEADORES");
            String[] hGol = {"#", "Jugador", "Goles"};
            String[][] rGol = new String[e.getGoleadores().size()][];
            for (int i = 0; i < e.getGoleadores().size(); i++) {
                GoleadorItem g = e.getGoleadores().get(i);
                Jugador j = controller.buscarJugadorPorId(g.getJugadorId());
                String nombre = j != null ? j.getNombre() : g.getJugadorId();
                rGol[i] = new String[]{
                        String.valueOf(i + 1),
                        nombre != null ? nombre : "-",
                        String.valueOf(g.getGoles())
                };
            }
            printTable(hGol, rGol);
        }
    }
}
