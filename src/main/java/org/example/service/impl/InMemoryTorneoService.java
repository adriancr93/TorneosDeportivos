package org.example.service.impl;

import org.example.interfaces.ITorneoService;
import org.example.model.Torneo;
import org.example.model.TorneoEstado;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class InMemoryTorneoService implements ITorneoService {
    private final List<Torneo> torneos = new ArrayList<>();

    @Override
    public Torneo crearTorneo(Torneo torneo) {
        torneo.setId(UUID.randomUUID().toString().substring(0, 8));
        torneo.setEstado(TorneoEstado.CREADO);
        if (torneo.getEquipoIds() == null) {
            torneo.setEquipoIds(new ArrayList<>());
        }
        torneos.add(torneo);
        return torneo;
    }

    @Override
    public boolean agregarEquipoATorneo(String torneoId, String equipoId) {
        Torneo t = buscarPorId(torneoId);
        if (t != null && t.getEstado() == TorneoEstado.CREADO) {
            if (t.getEquipoIds() == null) t.setEquipoIds(new ArrayList<>());
            t.getEquipoIds().add(equipoId);
            return true;
        }
        return false;
    }

    @Override
    public boolean activarTorneo(String torneoId) {
        Torneo t = buscarPorId(torneoId);
        if (t != null && t.getEstado() == TorneoEstado.CREADO) {
            t.setEstado(TorneoEstado.ACTIVO);
            return true;
        }
        return false;
    }

    @Override
    public boolean finalizarTorneo(String torneoId) {
        Torneo t = buscarPorId(torneoId);
        if (t != null && t.getEstado() == TorneoEstado.ACTIVO) {
            t.setEstado(TorneoEstado.FINALIZADO);
            return true;
        }
        return false;
    }

    @Override
    public List<Torneo> listarTorneos() {
        return new ArrayList<>(torneos);
    }

    public Torneo buscarPorId(String id) {
        return torneos.stream().filter(t -> t.getId().equals(id)).findFirst().orElse(null);
    }
}
