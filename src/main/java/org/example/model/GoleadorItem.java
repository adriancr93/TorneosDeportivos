package org.example.model;

public class GoleadorItem {
    private String jugadorId;
    private int goles;

    public GoleadorItem() {}

    public GoleadorItem(String jugadorId, int goles) {
        this.jugadorId = jugadorId;
        this.goles = goles;
    }

    public String getJugadorId() { return jugadorId; }
    public void setJugadorId(String jugadorId) { this.jugadorId = jugadorId; }
    public int getGoles() { return goles; }
    public void setGoles(int goles) { this.goles = goles; }

    @Override
    public String toString() {
        return "GoleadorItem{" +
                "jugadorId='" + jugadorId + '\'' +
                ", goles=" + goles +
                '}';
    }
}
