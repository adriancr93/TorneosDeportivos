package org.example.interfaces;

import org.example.model.Equipo;
import java.util.List;

public interface IEquipoService {
    Equipo crearEquipo(Equipo equipo);
    Equipo editarEquipo(String id, Equipo equipo);
    boolean eliminarEquipo(String id);
    List<Equipo> listarEquipos();
}
