package org.example.view;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import org.example.model.*;
import org.example.service.impl.*;

import java.util.*;

/**
 * Dashboard gráfico nativo con JavaFX.
 * Se lanza desde TorneoController en un hilo separado.
 */
public class DashboardView extends Application {

    // Los servicios se inyectan antes de launch() mediante campos estáticos
    public static MongoEquipoService equipoService;
    public static MongoJugadorService jugadorService;
    public static MongoTorneoService torneoService;
    public static MongoPartidoService partidoService;
    public static MongoEstadisticaService estadisticaService;

    @Override
    public void start(Stage stage) {
        // Resolver torneo activo o el último
        List<Torneo> torneos = torneoService.listarTorneos();
        if (torneos.isEmpty()) {
            mostrarError(stage, "No hay torneos en la base de datos.\nCargue datos de prueba primero (opción 7).");
            return;
        }
        Torneo torneo = torneos.stream()
                .filter(t -> t.getEstado() == TorneoEstado.ACTIVO)
                .findFirst()
                .orElse(torneos.get(torneos.size() - 1));

        Estadistica est = estadisticaService.visualizarEstadisticas(torneo.getId());
        if (est == null) est = estadisticaService.generarEstadisticas(torneo.getId());

        // ─── Layout principal con pestañas ────────────
        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabs.setStyle("-fx-background-color: #1e1e2e; -fx-tab-min-width: 150;");

        if (est != null && est.getTabla() != null) {
            tabs.getTabs().add(crearTabPosiciones(est, torneo));
            tabs.getTabs().add(crearTabGoleadores(est));
            tabs.getTabs().add(crearTabGolesEquipos(est));
        }
        tabs.getTabs().add(crearTabPartidos(torneo));

        // ─── Encabezado ───────────────────────────────
        Label titulo = new Label("⚽  " + torneo.getNombre());
        titulo.setFont(Font.font("System", FontWeight.BOLD, 20));
        titulo.setTextFill(Color.web("#cdd6f4"));
        titulo.setPadding(new Insets(14, 20, 10, 20));

        Label subtitulo = new Label(torneo.getSede() + "  ·  " + torneo.getFechaInicio() + " → " + torneo.getFechaFin()
                + "  ·  Estado: " + torneo.getEstado());
        subtitulo.setFont(Font.font("System", 13));
        subtitulo.setTextFill(Color.web("#a6adc8"));
        subtitulo.setPadding(new Insets(0, 20, 10, 20));

        VBox root = new VBox(0, titulo, subtitulo, tabs);
        root.setStyle("-fx-background-color: #1e1e2e;");
        VBox.setVgrow(tabs, Priority.ALWAYS);

        Scene scene = new Scene(root, 1100, 680);
        scene.setFill(Color.web("#1e1e2e"));
        applyStyle(scene);

        stage.setTitle("Dashboard – " + torneo.getNombre());
        stage.setScene(scene);
        stage.show();
    }

    // ─── Pestaña: Tabla de posiciones + barras de puntos ───

    private Tab crearTabPosiciones(Estadistica est, Torneo torneo) {
        List<Partido> partidos = partidoService.listarPartidosPorTorneo(torneo.getId());

        // Tabla
        TableView<Map<String, Object>> tabla = new TableView<>();
        tabla.setStyle("-fx-background-color: #313244; -fx-text-fill: white;");
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        tabla.getColumns().addAll(
                col("#",    m -> String.valueOf(m.get("pos")),   40),
                col("Equipo",  m -> (String) m.get("equipo"),  200),
                col("PJ",   m -> String.valueOf(m.get("pj")),    45),
                col("PG",   m -> String.valueOf(m.get("pg")),    45),
                col("PE",   m -> String.valueOf(m.get("pe")),    45),
                col("PP",   m -> String.valueOf(m.get("pp")),    45),
                col("GF",   m -> String.valueOf(m.get("gf")),    45),
                col("GC",   m -> String.valueOf(m.get("gc")),    45),
                col("DIF",  m -> String.valueOf(m.get("dif")),   50),
                col("PTS",  m -> String.valueOf(m.get("pts")),   50)
        );

        List<Map<String, Object>> rows = new ArrayList<>();
        for (int i = 0; i < est.getTabla().size(); i++) {
            TablaPosicionItem t = est.getTabla().get(i);
            Equipo eq = equipoService.buscarPorId(t.getEquipoId());
            // PG/PE/PP
            int pg = 0, pe = 0, pp = 0;
            for (Partido p : partidos) {
                if (p.getEstado() != PartidoEstado.JUGADO) continue;
                boolean esL = t.getEquipoId().equals(p.getEquipoLocalId());
                boolean esV = t.getEquipoId().equals(p.getEquipoVisitanteId());
                if (!esL && !esV) continue;
                if (p.getGolesLocal() == p.getGolesVisitante()) pe++;
                else if ((esL && p.getGolesLocal() > p.getGolesVisitante())
                      || (esV && p.getGolesVisitante() > p.getGolesLocal())) pg++;
                else pp++;
            }
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("pos", i + 1);
            row.put("equipo", eq != null ? eq.getNombre() : t.getEquipoId());
            row.put("pj", t.getPartidosJugados());
            row.put("pg", pg); row.put("pe", pe); row.put("pp", pp);
            row.put("gf", t.getGolesFavor()); row.put("gc", t.getGolesContra());
            row.put("dif", t.getGolesFavor() - t.getGolesContra());
            row.put("pts", t.getPuntos());
            rows.add(row);
        }
        tabla.setItems(FXCollections.observableArrayList(rows));

        // Gráfico barras de puntos
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Equipo");
        yAxis.setLabel("Puntos");
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Puntos por equipo");
        barChart.setLegendVisible(false);
        barChart.setStyle("-fx-background-color: #313244;");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (Map<String, Object> row : rows) {
            String nombre = (String) row.get("equipo");
            int pts = (int) row.get("pts");
            series.getData().add(new XYChart.Data<>(abrev(nombre), pts));
        }
        barChart.getData().add(series);

        SplitPane split = new SplitPane(tabla, barChart);
        split.setDividerPositions(0.55);
        split.setStyle("-fx-background-color: #1e1e2e;");

        Tab tab = new Tab("📊 Posiciones", split);
        tab.setStyle("-fx-background-color: #313244; -fx-text-fill: white;");
        return tab;
    }

    // ─── Pestaña: Goleadores ───────────────────────

    private Tab crearTabGoleadores(Estadistica est) {
        if (est.getGoleadores() == null || est.getGoleadores().isEmpty())
            return new Tab("⚽ Goleadores", new Label("Sin datos"));

        List<GoleadorItem> gols = est.getGoleadores().stream()
                .filter(g -> g.getGoles() > 0)
                .sorted((a, b) -> b.getGoles() - a.getGoles())
                .limit(10)
                .toList();

        // Gráfico de barras horizontales (BarChart con ejes invertidos)
        CategoryAxis yAxis = new CategoryAxis();
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Goles");
        BarChart<Number, String> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle("Top 10 goleadores");
        chart.setLegendVisible(false);
        chart.setStyle("-fx-background-color: #313244;");

        XYChart.Series<Number, String> series = new XYChart.Series<>();
        for (GoleadorItem g : gols) {
            Jugador j = jugadorService.buscarPorId(g.getJugadorId());
            String nombre = j != null ? j.getNombre() : g.getJugadorId();
            series.getData().add(new XYChart.Data<>(g.getGoles(), nombre));
        }
        chart.getData().add(series);

        // Tabla al lado
        TableView<GoleadorItem> tabla = new TableView<>(FXCollections.observableArrayList(gols));
        tabla.setStyle("-fx-background-color: #313244;");
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<GoleadorItem, String> colNombre = new TableColumn<>("Jugador");
        colNombre.setCellValueFactory(c -> {
            Jugador j = jugadorService.buscarPorId(c.getValue().getJugadorId());
            return new javafx.beans.property.SimpleStringProperty(j != null ? j.getNombre() : c.getValue().getJugadorId());
        });
        TableColumn<GoleadorItem, String> colGoles = new TableColumn<>("Goles");
        colGoles.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(String.valueOf(c.getValue().getGoles())));
        TableColumn<GoleadorItem, String> colEquipo = new TableColumn<>("Equipo");
        colEquipo.setCellValueFactory(c -> {
            Jugador j = jugadorService.buscarPorId(c.getValue().getJugadorId());
            if (j == null) return new javafx.beans.property.SimpleStringProperty("-");
            Equipo eq = equipoService.buscarPorId(j.getEquipoId());
            return new javafx.beans.property.SimpleStringProperty(eq != null ? eq.getNombre() : "-");
        });
        tabla.getColumns().addAll(colNombre, colGoles, colEquipo);

        SplitPane split = new SplitPane(tabla, chart);
        split.setDividerPositions(0.4);
        split.setStyle("-fx-background-color: #1e1e2e;");

        return new Tab("⚽ Goleadores", split);
    }

    // ─── Pestaña: Goles por equipo (Pie + BarChart GF vs GC) ───

    private Tab crearTabGolesEquipos(Estadistica est) {
        // Pie chart de distribución de goles a favor
        PieChart pie = new PieChart();
        pie.setTitle("Distribución de goles a favor");
        pie.setLegendSide(Side.RIGHT);
        pie.setStyle("-fx-background-color: #313244;");

        // BarChart GF vs GC
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Equipo");
        yAxis.setLabel("Goles");
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Goles a favor vs Goles en contra");
        barChart.setStyle("-fx-background-color: #313244;");

        XYChart.Series<String, Number> seriesGF = new XYChart.Series<>();
        seriesGF.setName("Goles a favor");
        XYChart.Series<String, Number> seriesGC = new XYChart.Series<>();
        seriesGC.setName("Goles en contra");

        for (TablaPosicionItem item : est.getTabla()) {
            Equipo eq = equipoService.buscarPorId(item.getEquipoId());
            String nombre = eq != null ? abrev(eq.getNombre()) : item.getEquipoId();
            pie.getData().add(new PieChart.Data(nombre, item.getGolesFavor()));
            seriesGF.getData().add(new XYChart.Data<>(nombre, item.getGolesFavor()));
            seriesGC.getData().add(new XYChart.Data<>(nombre, item.getGolesContra()));
        }
        barChart.getData().addAll(seriesGF, seriesGC);

        VBox vbox = new VBox(12, pie, barChart);
        vbox.setPadding(new Insets(12));
        vbox.setStyle("-fx-background-color: #1e1e2e;");
        VBox.setVgrow(pie, Priority.ALWAYS);
        VBox.setVgrow(barChart, Priority.ALWAYS);

        ScrollPane scroll = new ScrollPane(vbox);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: #1e1e2e;");

        return new Tab("📈 Goles por equipo", scroll);
    }

    // ─── Pestaña: Resultados de partidos ───────────

    private Tab crearTabPartidos(Torneo torneo) {
        List<Partido> partidos = partidoService.listarPartidosPorTorneo(torneo.getId());

        TableView<Partido> tabla = new TableView<>(FXCollections.observableArrayList(partidos));
        tabla.setStyle("-fx-background-color: #313244;");
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Partido, String> colFecha = new TableColumn<>("Jornada");
        colFecha.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getFecha()));

        TableColumn<Partido, String> colLocal = new TableColumn<>("Local");
        colLocal.setCellValueFactory(c -> {
            Equipo eq = equipoService.buscarPorId(c.getValue().getEquipoLocalId());
            return new javafx.beans.property.SimpleStringProperty(eq != null ? eq.getNombre() : c.getValue().getEquipoLocalId());
        });

        TableColumn<Partido, String> colResult = new TableColumn<>("Resultado");
        colResult.setCellValueFactory(c -> {
            Partido p = c.getValue();
            String r = p.getEstado() == PartidoEstado.JUGADO
                    ? p.getGolesLocal() + " – " + p.getGolesVisitante()
                    : "vs";
            return new javafx.beans.property.SimpleStringProperty(r);
        });

        TableColumn<Partido, String> colVisitante = new TableColumn<>("Visitante");
        colVisitante.setCellValueFactory(c -> {
            Equipo eq = equipoService.buscarPorId(c.getValue().getEquipoVisitanteId());
            return new javafx.beans.property.SimpleStringProperty(eq != null ? eq.getNombre() : c.getValue().getEquipoVisitanteId());
        });

        TableColumn<Partido, String> colEstado = new TableColumn<>("Estado");
        colEstado.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getEstado().name()));

        tabla.getColumns().addAll(colFecha, colLocal, colResult, colVisitante, colEstado);

        VBox vbox = new VBox(tabla);
        VBox.setVgrow(tabla, Priority.ALWAYS);
        vbox.setStyle("-fx-background-color: #1e1e2e;");
        vbox.setPadding(new Insets(8));

        return new Tab("📅 Partidos", vbox);
    }

    // ─── Helpers ──────────────────────────────────

    @SuppressWarnings("unchecked")
    private TableColumn<Map<String, Object>, String> col(String titulo,
                                                          java.util.function.Function<Map<String, Object>, String> getter,
                                                          double width) {
        TableColumn<Map<String, Object>, String> col = new TableColumn<>(titulo);
        col.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(getter.apply(c.getValue())));
        col.setPrefWidth(width);
        return col;
    }

    private String abrev(String nombre) {
        String[] partes = nombre.split(" ");
        if (partes.length >= 2) {
            return partes[0].substring(0, Math.min(4, partes[0].length())) + "."
                 + partes[1].substring(0, Math.min(4, partes[1].length()));
        }
        return nombre.substring(0, Math.min(8, nombre.length()));
    }

    private void mostrarError(Stage stage, String mensaje) {
        Label lbl = new Label(mensaje);
        lbl.setTextFill(Color.web("#f38ba8"));
        lbl.setFont(Font.font("System", FontWeight.BOLD, 16));
        lbl.setPadding(new Insets(40));
        Scene scene = new Scene(new BorderPane(lbl), 500, 200);
        scene.setFill(Color.web("#1e1e2e"));
        stage.setScene(scene);
        stage.show();
    }

    private void applyStyle(Scene scene) {
        scene.getStylesheets().add(
            "data:text/css," +
            ".tab-pane .tab { -fx-background-color: #313244; -fx-text-base-color: #cdd6f4; }" +
            ".tab-pane .tab:selected { -fx-background-color: #45475a; }" +
            ".table-view { -fx-background-color: #313244; -fx-text-fill: #cdd6f4; }" +
            ".table-view .column-header { -fx-background-color: #45475a; }" +
            ".table-view .column-header .label { -fx-text-fill: #cdd6f4; -fx-font-weight: bold; }" +
            ".table-row-cell { -fx-background-color: #313244; -fx-text-fill: #cdd6f4; }" +
            ".table-row-cell:odd { -fx-background-color: #2a2a3e; }" +
            ".table-row-cell:selected { -fx-background-color: #585b70; }" +
            ".chart { -fx-background-color: #313244; }" +
            ".chart-plot-background { -fx-background-color: #24273a; }" +
            ".axis { -fx-text-fill: #cdd6f4; }" +
            ".axis-label { -fx-text-fill: #a6adc8; }" +
            ".chart-title { -fx-text-fill: #cdd6f4; -fx-font-weight: bold; }" +
            ".chart-legend { -fx-background-color: #313244; -fx-text-fill: #cdd6f4; }"
        );
    }

    /**
     * Lanza el dashboard en un hilo de JavaFX separado de la consola.
     */
    public static void lanzar(MongoEquipoService es, MongoJugadorService js,
                               MongoTorneoService ts, MongoPartidoService ps,
                               MongoEstadisticaService ests) {
        DashboardView.equipoService = es;
        DashboardView.jugadorService = js;
        DashboardView.torneoService = ts;
        DashboardView.partidoService = ps;
        DashboardView.estadisticaService = ests;

        Platform.setImplicitExit(false);
        Thread t = new Thread(() -> Application.launch(DashboardView.class), "javafx-dashboard");
        t.setDaemon(true);
        t.start();
    }
}
