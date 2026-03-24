package org.example.service.impl;

import org.example.interfaces.IJugadorService;
import org.example.model.Jugador;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class InMemoryJugadorService implements IJugadorService {
    private final List<Jugador> jugadores = new ArrayList<>();

    @Override
    public Jugador registrarJugador(Jugador jugador) {
        jugador.setId(UUID.randomUUID().toString().substring(0, 8));
        jugadores.add(jugador);
        return jugador;
    }

    @Override
    public boolean asociarJugadorAEquipo(String jugadorId, String equipoId) {
        for (Jugador j : jugadores) {
            if (j.getId().equals(jugadorId)) {
                j.setEquipoId(equipoId);
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Jugador> listarJugadoresPorEquipo(String equipoId) {
        return jugadores.stream()
                .filter(j -> equipoId.equals(j.getEquipoId()))
                .collect(Collectors.toList());
    }

    public Jugador buscarPorId(String id) {
        return jugadores.stream().filter(j -> j.getId().equals(id)).findFirst().orElse(null);
    }

    public List<Jugador> listarTodos() {
        return new ArrayList<>(jugadores);
    }
}
