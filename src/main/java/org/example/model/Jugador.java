package org.example.model;

public class Jugador {
    private String id;
    private String nombre;
    private int edad;
    private String posicion;
    private int dorsal;
    private int goles;
    private String equipoId;

    public Jugador() {}

    public Jugador(String id, String nombre, int edad, String posicion, int dorsal, int goles, String equipoId) {
        this.id = id;
        this.nombre = nombre;
        this.edad = edad;
        this.posicion = posicion;
        this.dorsal = dorsal;
        this.goles = goles;
        this.equipoId = equipoId;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public int getEdad() { return edad; }
    public void setEdad(int edad) { this.edad = edad; }
    public String getPosicion() { return posicion; }
    public void setPosicion(String posicion) { this.posicion = posicion; }
    public int getDorsal() { return dorsal; }
    public void setDorsal(int dorsal) { this.dorsal = dorsal; }
    public int getGoles() { return goles; }
    public void setGoles(int goles) { this.goles = goles; }
    public String getEquipoId() { return equipoId; }
    public void setEquipoId(String equipoId) { this.equipoId = equipoId; }

    @Override
    public String toString() {
        return "Jugador{" +
                "id='" + id + '\'' +
                ", nombre='" + nombre + '\'' +
                ", edad=" + edad +
                ", posicion='" + posicion + '\'' +
                ", dorsal=" + dorsal +
                ", goles=" + goles +
                ", equipoId='" + equipoId + '\'' +
                '}';
    }
}
