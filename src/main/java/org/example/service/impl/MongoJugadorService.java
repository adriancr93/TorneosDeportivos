package org.example.service.impl;

import org.example.interfaces.IJugadorService;
import org.example.model.Jugador;
import org.example.repository.JugadorRepository;

import java.util.List;
import java.util.UUID;

public class MongoJugadorService implements IJugadorService {
    private final JugadorRepository repo;

    public MongoJugadorService(JugadorRepository repo) {
        this.repo = repo;
    }

    @Override
    public Jugador registrarJugador(Jugador jugador) {
        jugador.setId(UUID.randomUUID().toString().substring(0, 8));
        return repo.save(jugador);
    }

    @Override
    public boolean asociarJugadorAEquipo(String jugadorId, String equipoId) {
        Jugador j = repo.findById(jugadorId);
        if (j == null) return false;
        j.setEquipoId(equipoId);
        repo.save(j);
        return true;
    }

    @Override
    public List<Jugador> listarJugadoresPorEquipo(String equipoId) {
        return repo.findByEquipoId(equipoId);
    }

    public Jugador buscarPorId(String id) {
        return repo.findById(id);
    }

    public List<Jugador> listarTodos() {
        return repo.findAll();
    }
}
