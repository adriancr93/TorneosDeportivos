package org.example.view;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import org.example.model.Equipo;
import org.example.model.Partido;
import org.example.model.Torneo;
import org.example.repository.EstadisticaRepository;
import org.example.repository.EquipoRepository;
import org.example.repository.JugadorRepository;
import org.example.repository.PartidoRepository;
import org.example.repository.TorneoRepository;
import org.example.repository.UsuarioRepository;
import org.example.service.impl.MongoEstadisticaService;
import org.example.service.impl.MongoEquipoService;
import org.example.service.impl.MongoJugadorService;
import org.example.service.impl.MongoPartidoService;
import org.example.service.impl.MongoTorneoService;
import org.example.service.impl.MongoUsuarioService;
import org.example.util.ApiServer;

import java.util.*;

public class LoginView extends Application {

    private MongoEquipoService equipoService;
    private MongoJugadorService jugadorService;
    private MongoTorneoService torneoService;
    private MongoPartidoService partidoService;
    private MongoEstadisticaService estadisticaService;
    private MongoUsuarioService usuarioService;
    private ApiServer apiServer;

    @Override
    public void start(Stage stage) {
        inicializarServicios();
        mostrarLogin(stage);
    }

    private void inicializarServicios() {
        EquipoRepository equipoRepo = new EquipoRepository();
        JugadorRepository jugadorRepo = new JugadorRepository();
        TorneoRepository torneoRepo = new TorneoRepository();
        PartidoRepository partidoRepo = new PartidoRepository();
        EstadisticaRepository estadisticaRepo = new EstadisticaRepository();
        UsuarioRepository usuarioRepo = new UsuarioRepository();

        this.equipoService = new MongoEquipoService(equipoRepo);
        this.jugadorService = new MongoJugadorService(jugadorRepo);
        this.torneoService = new MongoTorneoService(torneoRepo);
        this.partidoService = new MongoPartidoService(partidoRepo, torneoService);
        this.estadisticaService = new MongoEstadisticaService(estadisticaRepo, partidoService, jugadorService);
        this.usuarioService = new MongoUsuarioService(usuarioRepo);

        usuarioService.asegurarUsuarioDemo("admin", "admin123");
        usuarioService.asegurarUsuarioDemo("organizador", "torneo2026");

        iniciarApiServer();
    }

    private void iniciarApiServer() {
        try {
            this.apiServer = new ApiServer(equipoService, jugadorService, torneoService, partidoService, estadisticaService);
            Thread apiThread = new Thread(apiServer::start, "api-server");
            apiThread.setDaemon(true);
            apiThread.start();
        } catch (Exception e) {
            System.err.println("[API] No se pudo iniciar el servidor REST/Swagger: " + e.getMessage());
        }
    }

    private void mostrarLogin(Stage stage) {
        Label titulo = new Label("Inicio de sesion");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 24));
        titulo.setTextFill(Color.web("#1f2937"));

        Label subtitulo = new Label("Ingrese sus credenciales para gestionar torneos");
        subtitulo.setTextFill(Color.web("#4b5563"));

        TextField txtUsuario = new TextField();
        txtUsuario.setPromptText("Usuario");

        PasswordField txtPassword = new PasswordField();
        txtPassword.setPromptText("Contrasena");

        Label lblEstado = new Label();
        lblEstado.setTextFill(Color.web("#b91c1c"));

        Button btnEntrar = new Button("Ingresar");
        btnEntrar.setDefaultButton(true);
        btnEntrar.setOnAction(e -> {
            String usuario = txtUsuario.getText() == null ? "" : txtUsuario.getText().trim();
            String pass = txtPassword.getText() == null ? "" : txtPassword.getText().trim();

            if (autenticar(usuario, pass)) {
                mostrarPanelTorneos(stage, usuario);
            } else {
                lblEstado.setText("Credenciales incorrectas. Puede crear un usuario abajo.");
            }
        });

        Button btnRegistrar = new Button("Crear usuario");
        btnRegistrar.setOnAction(e -> {
            String usuario = txtUsuario.getText() == null ? "" : txtUsuario.getText().trim();
            String pass = txtPassword.getText() == null ? "" : txtPassword.getText().trim();

            if (usuario.isEmpty() || pass.isEmpty()) {
                lblEstado.setTextFill(Color.web("#b91c1c"));
                lblEstado.setText("Para registrar, complete usuario y contrasena.");
                return;
            }

            var creado = usuarioService.registrarUsuario(usuario, pass);
            if (creado == null) {
                lblEstado.setTextFill(Color.web("#b91c1c"));
                lblEstado.setText("No se pudo registrar. El usuario ya existe o datos invalidos.");
                return;
            }
            lblEstado.setTextFill(Color.web("#166534"));
            lblEstado.setText("Usuario creado. Ahora puede iniciar sesion.");
        });

        HBox acciones = new HBox(10, btnEntrar, btnRegistrar);

        VBox card = new VBox(10, titulo, subtitulo, txtUsuario, txtPassword, acciones, lblEstado);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(26));
        card.setMaxWidth(420);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: #e5e7eb; -fx-border-radius: 12;");

        StackPane root = new StackPane(card);
        root.setPadding(new Insets(24));
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #e0f2fe, #f8fafc);");

        Scene scene = new Scene(root, 740, 520);
        stage.setTitle("Torneos Deportivos - Login");
        stage.setScene(scene);
        stage.show();
    }

    private boolean autenticar(String usuario, String pass) {
        return usuarioService.autenticar(usuario, pass);
    }

    private void mostrarPanelTorneos(Stage stage, String usuario) {
        Label titulo = new Label("Crear torneo y generar partidos");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 22));

        Label bienvenido = new Label("Bienvenido, " + usuario);
        bienvenido.setTextFill(Color.web("#475569"));

        TextField txtNombre = new TextField();
        txtNombre.setPromptText("Nombre del torneo");

        TextField txtSede = new TextField();
        txtSede.setPromptText("Sede");

        TextField txtInicio = new TextField();
        txtInicio.setPromptText("Fecha inicio (YYYY-MM-DD)");

        TextField txtFin = new TextField();
        txtFin.setPromptText("Fecha fin (YYYY-MM-DD)");

        List<Equipo> equipos = equipoService.listarEquipos();
        Map<String, String> labelAIdEquipo = new HashMap<>();
        ListView<String> equiposList = new ListView<>();
        equiposList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        for (Equipo eq : equipos) {
            String etiqueta = eq.getNombre() + " (" + eq.getId() + ")";
            labelAIdEquipo.put(etiqueta, eq.getId());
            equiposList.getItems().add(etiqueta);
        }

        Label lblEquipos = new Label("Seleccione equipos (minimo 2)");
        Label lblEstado = new Label();
        lblEstado.setWrapText(true);

        TableView<Partido> tablaPartidos = construirTablaPartidos();

        Button btnCrear = new Button("Crear torneo + generar partidos");
        btnCrear.setOnAction(e -> {
            String nombre = txtNombre.getText() == null ? "" : txtNombre.getText().trim();
            String sede = txtSede.getText() == null ? "" : txtSede.getText().trim();
            String inicio = txtInicio.getText() == null ? "" : txtInicio.getText().trim();
            String fin = txtFin.getText() == null ? "" : txtFin.getText().trim();
            List<String> seleccionados = new ArrayList<>(equiposList.getSelectionModel().getSelectedItems());

            if (nombre.isEmpty() || sede.isEmpty()) {
                lblEstado.setTextFill(Color.web("#b91c1c"));
                lblEstado.setText("Debe completar nombre y sede.");
                return;
            }

            if (seleccionados.size() < 2) {
                lblEstado.setTextFill(Color.web("#b91c1c"));
                lblEstado.setText("Debe seleccionar al menos 2 equipos para generar partidos.");
                return;
            }

            Torneo torneo = new Torneo();
            torneo.setNombre(nombre);
            torneo.setSede(sede);
            torneo.setFechaInicio(inicio);
            torneo.setFechaFin(fin);

            Torneo creado = torneoService.crearTorneo(torneo);
            if (creado == null) {
                lblEstado.setTextFill(Color.web("#b91c1c"));
                lblEstado.setText("No se pudo crear el torneo.");
                return;
            }

            for (String etiqueta : seleccionados) {
                String equipoId = labelAIdEquipo.get(etiqueta);
                if (equipoId != null) {
                    torneoService.agregarEquipoATorneo(creado.getId(), equipoId);
                }
            }

            torneoService.activarTorneo(creado.getId());
            List<Partido> partidos = partidoService.generarPartidos(creado.getId());

            tablaPartidos.setItems(FXCollections.observableArrayList(partidos));
            lblEstado.setTextFill(Color.web("#166534"));
            lblEstado.setText("Torneo creado con ID " + creado.getId() + " y " + partidos.size() + " partido(s) generado(s).");
        });

        VBox formulario = new VBox(8,
                titulo,
                bienvenido,
                txtNombre,
                txtSede,
                txtInicio,
                txtFin,
                lblEquipos,
                equiposList,
                btnCrear,
                lblEstado
        );

        formulario.setPadding(new Insets(16));
        formulario.setMinWidth(360);
        formulario.setPrefWidth(420);
        VBox.setVgrow(equiposList, Priority.ALWAYS);

        VBox tablaBox = new VBox(8, new Label("Partidos generados"), tablaPartidos);
        tablaBox.setPadding(new Insets(16));
        VBox.setVgrow(tablaPartidos, Priority.ALWAYS);

        HBox root = new HBox(10, formulario, tablaBox);
        HBox.setHgrow(tablaBox, Priority.ALWAYS);
        root.setPadding(new Insets(14));
        root.setStyle("-fx-background-color: #f8fafc;");

        Scene scene = new Scene(root, 1080, 620);
        stage.setTitle("Torneos Deportivos - Panel");
        stage.setScene(scene);
        stage.show();
    }

    private TableView<Partido> construirTablaPartidos() {
        TableView<Partido> tabla = new TableView<>();
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        TableColumn<Partido, String> colJornada = new TableColumn<>("Jornada");
        colJornada.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getFecha()));

        TableColumn<Partido, String> colLocal = new TableColumn<>("Local");
        colLocal.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(nombreEquipo(c.getValue().getEquipoLocalId())));

        TableColumn<Partido, String> colVisitante = new TableColumn<>("Visitante");
        colVisitante.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(nombreEquipo(c.getValue().getEquipoVisitanteId())));

        TableColumn<Partido, String> colEstado = new TableColumn<>("Estado");
        colEstado.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getEstado().name()));

        tabla.getColumns().add(colJornada);
        tabla.getColumns().add(colLocal);
        tabla.getColumns().add(colVisitante);
        tabla.getColumns().add(colEstado);
        return tabla;
    }

    private String nombreEquipo(String idEquipo) {
        Equipo eq = equipoService.buscarPorId(idEquipo);
        return eq != null ? eq.getNombre() : idEquipo;
    }
}
