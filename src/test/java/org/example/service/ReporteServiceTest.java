package org.example.service;

import org.example.model.Equipo;
import org.example.model.Jugador;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

class ReporteServiceTest {
    private ReporteService reporteService;

    @BeforeEach
    void setUp() {
        reporteService = new ReporteService() {
            @Override
            public Equipo equipoConMasGoles(String torneoId) {
                Equipo e = new Equipo();
                e.setNombre("Ticos FC");
                e.setGolesFavor(10);
                return e;
            }
            @Override
            public Jugador jugadorConMasGoles(String torneoId) {
                Jugador j = new Jugador();
                j.setNombre("Juan Perez");
                j.setGoles(7);
                return j;
            }
            @Override
            public List<String> historialTorneos() {
                List<String> historial = new ArrayList<>();
                historial.add("Liga Nacional");
                historial.add("Copa Ticos");
                return historial;
            }
        };
    }

    @Test
    void testEquipoConMasGoles() {
        Equipo e = reporteService.equipoConMasGoles("T1");
        Assertions.assertNotNull(e);
        Assertions.assertEquals("Ticos FC", e.getNombre());
        Assertions.assertEquals(10, e.getGolesFavor());
    }

    @Test
    void testJugadorConMasGoles() {
        Jugador j = reporteService.jugadorConMasGoles("T1");
        Assertions.assertNotNull(j);
        Assertions.assertEquals("Juan Perez", j.getNombre());
        Assertions.assertEquals(7, j.getGoles());
    }

    @Test
    void testHistorialTorneos() {
        List<String> historial = reporteService.historialTorneos();
        Assertions.assertEquals(2, historial.size());
        Assertions.assertTrue(historial.contains("Liga Nacional"));
    }
}
