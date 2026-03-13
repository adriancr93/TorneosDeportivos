package org.example.model;

import java.util.List;

public class Torneo {
    private String id;
    private String nombre;
    private String sede;
    private String fechaInicio;
    private String fechaFin;
    private TorneoEstado estado;
    private List<String> equipoIds;

    public Torneo() {}

    public Torneo(String id, String nombre, String sede, String fechaInicio, String fechaFin, TorneoEstado estado, List<String> equipoIds) {
        this.id = id;
        this.nombre = nombre;
        this.sede = sede;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.estado = estado;
        this.equipoIds = equipoIds;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getSede() { return sede; }
    public void setSede(String sede) { this.sede = sede; }
    public String getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(String fechaInicio) { this.fechaInicio = fechaInicio; }
    public String getFechaFin() { return fechaFin; }
    public void setFechaFin(String fechaFin) { this.fechaFin = fechaFin; }
    public TorneoEstado getEstado() { return estado; }
    public void setEstado(TorneoEstado estado) { this.estado = estado; }
    public List<String> getEquipoIds() { return equipoIds; }
    public void setEquipoIds(List<String> equipoIds) { this.equipoIds = equipoIds; }

    @Override
    public String toString() {
        return "Torneo{" +
                "id='" + id + '\'' +
                ", nombre='" + nombre + '\'' +
                ", sede='" + sede + '\'' +
                ", fechaInicio='" + fechaInicio + '\'' +
                ", fechaFin='" + fechaFin + '\'' +
                ", estado=" + estado +
                ", equipoIds=" + equipoIds +
                '}';
    }
}
