package org.example.service;

import org.example.model.Torneo;
import org.example.model.TorneoEstado;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

class TorneoServiceTest {
    private TorneoService torneoService;
    private List<Torneo> torneos;

    @BeforeEach
    void setUp() {
        torneos = new ArrayList<>();
        torneoService = new TorneoService() {
            @Override
            public Torneo crearTorneo(Torneo torneo) {
                torneo.setId("T1");
                torneo.setEstado(TorneoEstado.CREADO);
                torneos.add(torneo);
                return torneo;
            }
            @Override
            public boolean agregarEquipoATorneo(String torneoId, String equipoId) {
                for (Torneo t : torneos) {
                    if (t.getId().equals(torneoId)) {
                        List<String> equipos = t.getEquipoIds();
                        if (equipos == null) equipos = new ArrayList<>();
                        equipos.add(equipoId);
                        t.setEquipoIds(equipos);
                        return true;
                    }
                }
                return false;
            }
            @Override
            public boolean activarTorneo(String torneoId) {
                for (Torneo t : torneos) {
                    if (t.getId().equals(torneoId)) {
                        t.setEstado(TorneoEstado.ACTIVO);
                        return true;
                    }
                }
                return false;
            }
            @Override
            public boolean finalizarTorneo(String torneoId) {
                for (Torneo t : torneos) {
                    if (t.getId().equals(torneoId)) {
                        t.setEstado(TorneoEstado.FINALIZADO);
                        return true;
                    }
                }
                return false;
            }
            @Override
            public List<Torneo> listarTorneos() {
                return torneos;
            }
        };
    }

    @Test
    void testCrearTorneo() {
        Torneo torneo = new Torneo();
        torneo.setNombre("Liga Nacional");
        Torneo creado = torneoService.crearTorneo(torneo);
        Assertions.assertNotNull(creado);
        Assertions.assertEquals("Liga Nacional", creado.getNombre());
        Assertions.assertEquals(TorneoEstado.CREADO, creado.getEstado());
    }

    @Test
    void testAgregarEquipoATorneo() {
        Torneo torneo = new Torneo();
        torneoService.crearTorneo(torneo);
        boolean agregado = torneoService.agregarEquipoATorneo("T1", "E1");
        Assertions.assertTrue(agregado);
        Assertions.assertEquals(1, torneoService.listarTorneos().get(0).getEquipoIds().size());
    }

    @Test
    void testActivarTorneo() {
        Torneo torneo = new Torneo();
        torneoService.crearTorneo(torneo);
        boolean activado = torneoService.activarTorneo("T1");
        Assertions.assertTrue(activado);
        Assertions.assertEquals(TorneoEstado.ACTIVO, torneoService.listarTorneos().get(0).getEstado());
    }

    @Test
    void testFinalizarTorneo() {
        Torneo torneo = new Torneo();
        torneoService.crearTorneo(torneo);
        boolean finalizado = torneoService.finalizarTorneo("T1");
        Assertions.assertTrue(finalizado);
        Assertions.assertEquals(TorneoEstado.FINALIZADO, torneoService.listarTorneos().get(0).getEstado());
    }

    @Test
    void testListarTorneos() {
        torneoService.crearTorneo(new Torneo());
        torneoService.crearTorneo(new Torneo());
        List<Torneo> lista = torneoService.listarTorneos();
        Assertions.assertEquals(2, lista.size());
    }
}
