package org.example.service;

import org.example.model.Equipo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

class EquipoServiceTest {
    private EquipoService equipoService;
    private List<Equipo> equipos;

    @BeforeEach
    void setUp() {
        equipos = new ArrayList<>();
        equipoService = new EquipoService() {
            @Override
            public Equipo crearEquipo(Equipo equipo) {
                equipo.setId("E1");
                equipos.add(equipo);
                return equipo;
            }
            @Override
            public Equipo editarEquipo(String id, Equipo equipo) {
                for (Equipo e : equipos) {
                    if (e.getId().equals(id)) {
                        e.setNombre(equipo.getNombre());
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
                return equipos;
            }
        };
    }

    @Test
    void testCrearEquipo() {
        Equipo equipo = new Equipo();
        equipo.setNombre("Ticos FC");
        Equipo creado = equipoService.crearEquipo(equipo);
        Assertions.assertNotNull(creado);
        Assertions.assertEquals("Ticos FC", creado.getNombre());
        Assertions.assertEquals(1, equipoService.listarEquipos().size());
    }

    @Test
    void testEditarEquipo() {
        Equipo equipo = new Equipo();
        equipo.setNombre("Ticos FC");
        equipoService.crearEquipo(equipo);
        Equipo editado = equipoService.editarEquipo("E1", new Equipo(null, "Ticos Pro", null, 0, null, 0, 0, 0, 0));
        Assertions.assertEquals("Ticos Pro", editado.getNombre());
    }

    @Test
    void testEliminarEquipo() {
        Equipo equipo = new Equipo();
        equipo.setNombre("Ticos FC");
        equipoService.crearEquipo(equipo);
        boolean eliminado = equipoService.eliminarEquipo("E1");
        Assertions.assertTrue(eliminado);
        Assertions.assertEquals(0, equipoService.listarEquipos().size());
    }

    @Test
    void testListarEquipos() {
        equipoService.crearEquipo(new Equipo(null, "Ticos FC", null, 0, null, 0, 0, 0, 0));
        equipoService.crearEquipo(new Equipo(null, "Leones", null, 0, null, 0, 0, 0, 0));
        List<Equipo> lista = equipoService.listarEquipos();
        Assertions.assertEquals(2, lista.size());
    }
}
