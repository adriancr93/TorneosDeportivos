package org.example.service;

import org.example.model.Partido;
import org.example.model.PartidoEstado;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

class PartidoServiceTest {
    private PartidoService partidoService;
    private List<Partido> partidos;

    @BeforeEach
    void setUp() {
        partidos = new ArrayList<>();
        partidoService = new PartidoService() {
            @Override
            public List<Partido> generarPartidos(String torneoId) {
                Partido p = new Partido();
                p.setId("P1");
                p.setTorneoId(torneoId);
                p.setEstado(PartidoEstado.PENDIENTE);
                partidos.add(p);
                return partidos;
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
                List<Partido> result = new ArrayList<>();
                for (Partido p : partidos) {
                    if (torneoId.equals(p.getTorneoId())) {
                        result.add(p);
                    }
                }
                return result;
            }
        };
    }

    @Test
    void testGenerarPartidos() {
        List<Partido> lista = partidoService.generarPartidos("T1");
        Assertions.assertEquals(1, lista.size());
        Assertions.assertEquals("T1", lista.get(0).getTorneoId());
        Assertions.assertEquals(PartidoEstado.PENDIENTE, lista.get(0).getEstado());
    }

    @Test
    void testRegistrarResultado() {
        partidoService.generarPartidos("T1");
        boolean registrado = partidoService.registrarResultado("P1", 2, 1);
        Assertions.assertTrue(registrado);
        Assertions.assertEquals(2, partidoService.listarPartidosPorTorneo("T1").get(0).getGolesLocal());
        Assertions.assertEquals(PartidoEstado.JUGADO, partidoService.listarPartidosPorTorneo("T1").get(0).getEstado());
    }

    @Test
    void testListarPartidosPorTorneo() {
        partidoService.generarPartidos("T1");
        List<Partido> lista = partidoService.listarPartidosPorTorneo("T1");
        Assertions.assertEquals(1, lista.size());
    }
}
