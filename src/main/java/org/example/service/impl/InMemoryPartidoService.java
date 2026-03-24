package org.example.service.impl;

import org.example.interfaces.IPartidoService;
import org.example.model.Partido;
import org.example.model.PartidoEstado;
import org.example.model.Torneo;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class InMemoryPartidoService implements IPartidoService {
    private final List<Partido> partidos = new ArrayList<>();
    private final InMemoryTorneoService torneoService;

    public InMemoryPartidoService(InMemoryTorneoService torneoService) {
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
                partidos.add(p);
                generados.add(p);
            }
        }
        return generados;
    }

    @Override
    public boolean registrarResultado(String partidoId, int golesLocal, int golesVisitante) {
        for (Partido p : partidos) {
            if (p.getId().equals(partidoId)) {
                p.setGolesLocal(golesLocal);
                p.setGolesVisitante(golesVisitante);
                p.setEstado(PartidoEstado.JUGADO);
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Partido> listarPartidosPorTorneo(String torneoId) {
        return partidos.stream()
                .filter(p -> torneoId.equals(p.getTorneoId()))
                .collect(Collectors.toList());
    }
}
