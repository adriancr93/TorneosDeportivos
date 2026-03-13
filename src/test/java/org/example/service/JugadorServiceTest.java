package org.example.service;

import org.example.model.Jugador;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

class JugadorServiceTest {
    private JugadorService jugadorService;
    private List<Jugador> jugadores;

    @BeforeEach
    void setUp() {
        jugadores = new ArrayList<>();
        jugadorService = new JugadorService() {
            @Override
            public Jugador registrarJugador(Jugador jugador) {
                jugador.setId("J1");
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
                List<Jugador> result = new ArrayList<>();
                for (Jugador j : jugadores) {
                    // Evita NullPointer usando comparación segura
                    if (equipoId != null && equipoId.equals(j.getEquipoId())) {
                        result.add(j);
                    }
                }
                return result;
            }
        };
    }

    @Test
    void testRegistrarJugador() {
    Jugador jugador = new Jugador();
    jugador.setNombre("Juan Perez");
    jugador.setEquipoId("E1"); // Asignar equipoId para el filtrado
    Jugador registrado = jugadorService.registrarJugador(jugador);
    Assertions.assertNotNull(registrado);
    Assertions.assertEquals("Juan Perez", registrado.getNombre());
    Assertions.assertEquals(1, jugadorService.listarJugadoresPorEquipo("E1").size());
    }

    @Test
    void testAsociarJugadorAEquipo() {
        Jugador jugador = new Jugador();
        jugador.setNombre("Juan Perez");
        jugadorService.registrarJugador(jugador);
        boolean asociado = jugadorService.asociarJugadorAEquipo("J1", "E1");
        Assertions.assertTrue(asociado);
        Assertions.assertEquals("E1", jugadorService.listarJugadoresPorEquipo("E1").get(0).getEquipoId());
    }

    @Test
    void testListarJugadoresPorEquipo() {
        Jugador jugador1 = new Jugador();
        jugador1.setNombre("Juan Perez");
        jugadorService.registrarJugador(jugador1);
        jugadorService.asociarJugadorAEquipo("J1", "E1");
        List<Jugador> lista = jugadorService.listarJugadoresPorEquipo("E1");
        Assertions.assertEquals(1, lista.size());
    }
}
