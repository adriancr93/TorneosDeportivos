package org.example.model;

public class Equipo {
    private String id;
    private String nombre;
    private String ciudad;
    private int anioFundacion;
    private String entrenador;
    private int golesFavor;
    private int golesContra;
    private int puntos;
    private int partidosJugados;

    public Equipo() {}

    public Equipo(String id, String nombre, String ciudad, int anioFundacion, String entrenador,
                 int golesFavor, int golesContra, int puntos, int partidosJugados) {
        this.id = id;
        this.nombre = nombre;
        this.ciudad = ciudad;
        this.anioFundacion = anioFundacion;
        this.entrenador = entrenador;
        this.golesFavor = golesFavor;
        this.golesContra = golesContra;
        this.puntos = puntos;
        this.partidosJugados = partidosJugados;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }
    public int getAnioFundacion() { return anioFundacion; }
    public void setAnioFundacion(int anioFundacion) { this.anioFundacion = anioFundacion; }
    public String getEntrenador() { return entrenador; }
    public void setEntrenador(String entrenador) { this.entrenador = entrenador; }
    public int getGolesFavor() { return golesFavor; }
    public void setGolesFavor(int golesFavor) { this.golesFavor = golesFavor; }
    public int getGolesContra() { return golesContra; }
    public void setGolesContra(int golesContra) { this.golesContra = golesContra; }
    public int getPuntos() { return puntos; }
    public void setPuntos(int puntos) { this.puntos = puntos; }
    public int getPartidosJugados() { return partidosJugados; }
    public void setPartidosJugados(int partidosJugados) { this.partidosJugados = partidosJugados; }

    @Override
    public String toString() {
        return "Equipo{" +
                "id='" + id + '\'' +
                ", nombre='" + nombre + '\'' +
                ", ciudad='" + ciudad + '\'' +
                ", anioFundacion=" + anioFundacion +
                ", entrenador='" + entrenador + '\'' +
                ", golesFavor=" + golesFavor +
                ", golesContra=" + golesContra +
                ", puntos=" + puntos +
                ", partidosJugados=" + partidosJugados +
                '}';
    }
}
