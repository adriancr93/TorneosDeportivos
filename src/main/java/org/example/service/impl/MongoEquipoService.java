package org.example.service.impl;

import org.example.interfaces.IEquipoService;
import org.example.model.Equipo;
import org.example.repository.EquipoRepository;

import java.util.List;
import java.util.UUID;

public class MongoEquipoService implements IEquipoService {
    private final EquipoRepository repo;

    public MongoEquipoService(EquipoRepository repo) {
        this.repo = repo;
    }

    @Override
    public Equipo crearEquipo(Equipo equipo) {
        equipo.setId(UUID.randomUUID().toString().substring(0, 8));
        return repo.save(equipo);
    }

    @Override
    public Equipo editarEquipo(String id, Equipo equipo) {
        Equipo existente = repo.findById(id);
        if (existente == null) return null;
        if (equipo.getNombre() != null) existente.setNombre(equipo.getNombre());
        if (equipo.getCiudad() != null) existente.setCiudad(equipo.getCiudad());
        if (equipo.getEntrenador() != null) existente.setEntrenador(equipo.getEntrenador());
        if (equipo.getAnioFundacion() != 0) existente.setAnioFundacion(equipo.getAnioFundacion());
        return repo.save(existente);
    }

    @Override
    public boolean eliminarEquipo(String id) {
        return repo.deleteById(id);
    }

    @Override
    public List<Equipo> listarEquipos() {
        return repo.findAll();
    }

    public Equipo buscarPorId(String id) {
        return repo.findById(id);
    }
}
