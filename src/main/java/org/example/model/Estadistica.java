package org.example.model;

import java.util.List;

public class Estadistica {
    private String id;
    private String torneoId;
    private String fechaGeneracion;
    private List<TablaPosicionItem> tabla;
    private List<GoleadorItem> goleadores;

    public Estadistica() {}

    public Estadistica(String id, String torneoId, String fechaGeneracion, List<TablaPosicionItem> tabla, List<GoleadorItem> goleadores) {
        this.id = id;
        this.torneoId = torneoId;
        this.fechaGeneracion = fechaGeneracion;
        this.tabla = tabla;
        this.goleadores = goleadores;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTorneoId() { return torneoId; }
    public void setTorneoId(String torneoId) { this.torneoId = torneoId; }
    public String getFechaGeneracion() { return fechaGeneracion; }
    public void setFechaGeneracion(String fechaGeneracion) { this.fechaGeneracion = fechaGeneracion; }
    public List<TablaPosicionItem> getTabla() { return tabla; }
    public void setTabla(List<TablaPosicionItem> tabla) { this.tabla = tabla; }
    public List<GoleadorItem> getGoleadores() { return goleadores; }
    public void setGoleadores(List<GoleadorItem> goleadores) { this.goleadores = goleadores; }

    @Override
    public String toString() {
        return "Estadistica{" +
                "id='" + id + '\'' +
                ", torneoId='" + torneoId + '\'' +
                ", fechaGeneracion='" + fechaGeneracion + '\'' +
                ", tabla=" + tabla +
                ", goleadores=" + goleadores +
                '}';
    }
}
