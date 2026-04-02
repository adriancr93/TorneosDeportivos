package org.example.model;

import java.util.HashMap;
import java.util.Map;

public class Partido {
    private String id;
    private String torneoId;
    private String fecha;
    private String equipoLocalId;
    private String equipoVisitanteId;
    private int golesLocal;
    private int golesVisitante;
    private PartidoEstado estado;
    private String ronda;
    private Map<String, Integer> golesPorJugador;
    private Map<String, Integer> asistenciasPorJugador;

    public Partido() {}

    public Partido(String id, String torneoId, String fecha, String equipoLocalId, String equipoVisitanteId, int golesLocal, int golesVisitante, PartidoEstado estado) {
        this.id = id;
        this.torneoId = torneoId;
        this.fecha = fecha;
        this.equipoLocalId = equipoLocalId;
        this.equipoVisitanteId = equipoVisitanteId;
        this.golesLocal = golesLocal;
        this.golesVisitante = golesVisitante;
        this.estado = estado;
        this.golesPorJugador = new HashMap<>();
        this.asistenciasPorJugador = new HashMap<>();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTorneoId() { return torneoId; }
    public void setTorneoId(String torneoId) { this.torneoId = torneoId; }
    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    public String getEquipoLocalId() { return equipoLocalId; }
    public void setEquipoLocalId(String equipoLocalId) { this.equipoLocalId = equipoLocalId; }
    public String getEquipoVisitanteId() { return equipoVisitanteId; }
    public void setEquipoVisitanteId(String equipoVisitanteId) { this.equipoVisitanteId = equipoVisitanteId; }
    public int getGolesLocal() { return golesLocal; }
    public void setGolesLocal(int golesLocal) { this.golesLocal = golesLocal; }
    public int getGolesVisitante() { return golesVisitante; }
    public void setGolesVisitante(int golesVisitante) { this.golesVisitante = golesVisitante; }
    public PartidoEstado getEstado() { return estado; }
    public void setEstado(PartidoEstado estado) { this.estado = estado; }
    public Map<String, Integer> getGolesPorJugador() {
        if (golesPorJugador == null) golesPorJugador = new HashMap<>();
        return golesPorJugador;
    }
    public void setGolesPorJugador(Map<String, Integer> golesPorJugador) { this.golesPorJugador = golesPorJugador; }
    public Map<String, Integer> getAsistenciasPorJugador() {
        if (asistenciasPorJugador == null) asistenciasPorJugador = new HashMap<>();
        return asistenciasPorJugador;
    }
    public void setAsistenciasPorJugador(Map<String, Integer> asistenciasPorJugador) { this.asistenciasPorJugador = asistenciasPorJugador; }
    public String getRonda() { return ronda; }
    public void setRonda(String ronda) { this.ronda = ronda; }

    @Override
    public String toString() {
        return "Partido{" +
                "id='" + id + '\'' +
                ", torneoId='" + torneoId + '\'' +
                ", fecha='" + fecha + '\'' +
                ", ronda='" + ronda + '\'' +
                ", equipoLocalId='" + equipoLocalId + '\'' +
                ", equipoVisitanteId='" + equipoVisitanteId + '\'' +
                ", golesLocal=" + golesLocal +
                ", golesVisitante=" + golesVisitante +
                ", estado=" + estado +
                ", golesPorJugador=" + golesPorJugador +
                ", asistenciasPorJugador=" + asistenciasPorJugador +
                '}';
    }
}
