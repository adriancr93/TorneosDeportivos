package org.example.service.impl;

import org.example.interfaces.IPartidoService;
import org.example.model.Partido;
import org.example.model.PartidoEstado;
import org.example.model.Torneo;
import org.example.repository.PartidoRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MongoPartidoService implements IPartidoService {
    private final PartidoRepository repo;
    private final MongoTorneoService torneoService;

    public MongoPartidoService(PartidoRepository repo, MongoTorneoService torneoService) {
        this.repo = repo;
        this.torneoService = torneoService;
    }

    @Override
    public List<Partido> generarPartidos(String torneoId) {
        Torneo torneo = torneoService.buscarPorId(torneoId);
        if (torneo == null || torneo.getEquipoIds() == null || torneo.getEquipoIds().size() < 2) {
            return new ArrayList<>();
        }
        List<String> equipos = torneo.getEquipoIds();
        List<Partido> generados = new ArrayList<>();
        int jornada = 1;
        for (int i = 0; i < equipos.size(); i++) {
            for (int j = i + 1; j < equipos.size(); j++) {
                Partido p = new Partido();
                p.setId(UUID.randomUUID().toString().substring(0, 8));
                p.setTorneoId(torneoId);
                p.setEquipoLocalId(equipos.get(i));
                p.setEquipoVisitanteId(equipos.get(j));
                p.setEstado(PartidoEstado.PENDIENTE);
                p.setFecha("Jornada " + jornada++);
                repo.save(p);
                generados.add(p);
            }
        }
        return generados;
    }

    @Override
    public boolean registrarResultado(String partidoId, int golesLocal, int golesVisitante) {
        Partido p = repo.findById(partidoId);
        if (p == null) return false;
        p.setGolesLocal(golesLocal);
        p.setGolesVisitante(golesVisitante);
        p.setEstado(PartidoEstado.JUGADO);
        repo.save(p);
        return true;
    }

    @Override
    public List<Partido> listarPartidosPorTorneo(String torneoId) {
        return repo.findByTorneoId(torneoId);
    }
}
