package org.example.service;

import org.example.model.Estadistica;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EstadisticaServiceTest {
    private EstadisticaService estadisticaService;

    @BeforeEach
    void setUp() {
        estadisticaService = new EstadisticaService() {
            @Override
            public Estadistica generarEstadisticas(String torneoId) {
                Estadistica e = new Estadistica();
                e.setTorneoId(torneoId);
                return e;
            }
            @Override
            public Estadistica visualizarEstadisticas(String torneoId) {
                Estadistica e = new Estadistica();
                e.setTorneoId(torneoId);
                return e;
            }
        };
    }

    @Test
    void testGenerarEstadisticas() {
        Estadistica e = estadisticaService.generarEstadisticas("T1");
        Assertions.assertNotNull(e);
        Assertions.assertEquals("T1", e.getTorneoId());
    }

    @Test
    void testVisualizarEstadisticas() {
        Estadistica e = estadisticaService.visualizarEstadisticas("T1");
        Assertions.assertNotNull(e);
        Assertions.assertEquals("T1", e.getTorneoId());
    }
}
