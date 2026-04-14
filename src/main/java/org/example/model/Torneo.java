package org.example.model;

import java.util.List;

public class Torneo {
    private String id;
    private String nombre;
    private String sede;
    private String fechaInicio;
    private String fechaFin;
    private ModalidadTorneo modalidad;
    private TorneoEstado estado;
    private List<String> equipoIds;
    private String campeonId;
    private String subcampeonId;
    private String tercerLugarId;

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
    public ModalidadTorneo getModalidad() { return modalidad; }
    public void setModalidad(ModalidadTorneo modalidad) { this.modalidad = modalidad; }
    public TorneoEstado getEstado() { return estado; }
    public void setEstado(TorneoEstado estado) { this.estado = estado; }
    public List<String> getEquipoIds() { return equipoIds; }
    public void setEquipoIds(List<String> equipoIds) { this.equipoIds = equipoIds; }
    public String getCampeonId() { return campeonId; }
    public void setCampeonId(String campeonId) { this.campeonId = campeonId; }
    public String getSubcampeonId() { return subcampeonId; }
    public void setSubcampeonId(String subcampeonId) { this.subcampeonId = subcampeonId; }
    public String getTercerLugarId() { return tercerLugarId; }
    public void setTercerLugarId(String tercerLugarId) { this.tercerLugarId = tercerLugarId; }

    @Override
    public String toString() {
        return "Torneo{" +
                "id='" + id + '\'' +
                ", nombre='" + nombre + '\'' +
                ", sede='" + sede + '\'' +
                ", fechaInicio='" + fechaInicio + '\'' +
                ", fechaFin='" + fechaFin + '\'' +
                ", modalidad=" + modalidad +
                ", estado=" + estado +
                ", equipoIds=" + equipoIds +
                ", campeonId='" + campeonId + '\'' +
                ", subcampeonId='" + subcampeonId + '\'' +
                ", tercerLugarId='" + tercerLugarId + '\'' +
                '}';
    }
}
