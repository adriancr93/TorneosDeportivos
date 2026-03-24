package org.example.service.impl;

import org.example.interfaces.IEquipoService;
import org.example.model.Equipo;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class InMemoryEquipoService implements IEquipoService {
    private final List<Equipo> equipos = new ArrayList<>();

    @Override
    public Equipo crearEquipo(Equipo equipo) {
        equipo.setId(UUID.randomUUID().toString().substring(0, 8));
        equipos.add(equipo);
        return equipo;
    }

    @Override
    public Equipo editarEquipo(String id, Equipo equipo) {
        for (Equipo e : equipos) {
            if (e.getId().equals(id)) {
                if (equipo.getNombre() != null) e.setNombre(equipo.getNombre());
                if (equipo.getCiudad() != null) e.setCiudad(equipo.getCiudad());
                if (equipo.getEntrenador() != null) e.setEntrenador(equipo.getEntrenador());
                if (equipo.getAnioFundacion() != 0) e.setAnioFundacion(equipo.getAnioFundacion());
                return e;
            }
        }
        return null;
    }

    @Override
    public boolean eliminarEquipo(String id) {
        return equipos.removeIf(e -> e.getId().equals(id));
    }

    @Override
    public List<Equipo> listarEquipos() {
        return new ArrayList<>(equipos);
    }

    public Equipo buscarPorId(String id) {
        return equipos.stream().filter(e -> e.getId().equals(id)).findFirst().orElse(null);
    }
}
