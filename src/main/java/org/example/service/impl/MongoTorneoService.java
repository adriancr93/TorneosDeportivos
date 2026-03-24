package org.example.service.impl;

import org.example.interfaces.ITorneoService;
import org.example.model.Torneo;
import org.example.model.TorneoEstado;
import org.example.repository.TorneoRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MongoTorneoService implements ITorneoService {
    private final TorneoRepository repo;

    public MongoTorneoService(TorneoRepository repo) {
        this.repo = repo;
    }

    @Override
    public Torneo crearTorneo(Torneo torneo) {
        torneo.setId(UUID.randomUUID().toString().substring(0, 8));
        torneo.setEstado(TorneoEstado.CREADO);
        if (torneo.getEquipoIds() == null) torneo.setEquipoIds(new ArrayList<>());
        return repo.save(torneo);
    }

    @Override
    public boolean agregarEquipoATorneo(String torneoId, String equipoId) {
        Torneo t = repo.findById(torneoId);
        if (t == null || t.getEstado() != TorneoEstado.CREADO) return false;
        if (t.getEquipoIds() == null) t.setEquipoIds(new ArrayList<>());
        t.getEquipoIds().add(equipoId);
        repo.save(t);
        return true;
    }

    @Override
    public boolean activarTorneo(String torneoId) {
        Torneo t = repo.findById(torneoId);
        if (t == null || t.getEstado() != TorneoEstado.CREADO) return false;
        t.setEstado(TorneoEstado.ACTIVO);
        repo.save(t);
        return true;
    }

    @Override
    public boolean finalizarTorneo(String torneoId) {
        Torneo t = repo.findById(torneoId);
        if (t == null || t.getEstado() != TorneoEstado.ACTIVO) return false;
        t.setEstado(TorneoEstado.FINALIZADO);
        repo.save(t);
        return true;
    }

    @Override
    public List<Torneo> listarTorneos() {
        return repo.findAll();
    }

    public Torneo buscarPorId(String id) {
        return repo.findById(id);
    }
}
