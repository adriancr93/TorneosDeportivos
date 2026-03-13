package org.example.model;

public class TablaPosicionItem {
    private String equipoId;
    private int puntos;
    private int golesFavor;
    private int golesContra;
    private int partidosJugados;

    public TablaPosicionItem() {}

    public TablaPosicionItem(String equipoId, int puntos, int golesFavor, int golesContra, int partidosJugados) {
        this.equipoId = equipoId;
        this.puntos = puntos;
        this.golesFavor = golesFavor;
        this.golesContra = golesContra;
        this.partidosJugados = partidosJugados;
    }

    public String getEquipoId() { return equipoId; }
    public void setEquipoId(String equipoId) { this.equipoId = equipoId; }
    public int getPuntos() { return puntos; }
    public void setPuntos(int puntos) { this.puntos = puntos; }
    public int getGolesFavor() { return golesFavor; }
    public void setGolesFavor(int golesFavor) { this.golesFavor = golesFavor; }
    public int getGolesContra() { return golesContra; }
    public void setGolesContra(int golesContra) { this.golesContra = golesContra; }
    public int getPartidosJugados() { return partidosJugados; }
    public void setPartidosJugados(int partidosJugados) { this.partidosJugados = partidosJugados; }

    @Override
    public String toString() {
        return "TablaPosicionItem{" +
                "equipoId='" + equipoId + '\'' +
                ", puntos=" + puntos +
                ", golesFavor=" + golesFavor +
                ", golesContra=" + golesContra +
                ", partidosJugados=" + partidosJugados +
                '}';
    }
}
