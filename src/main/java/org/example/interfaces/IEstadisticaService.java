package org.example.interfaces;

import org.example.model.Estadistica;

public interface IEstadisticaService {
    Estadistica generarEstadisticas(String torneoId);
    Estadistica visualizarEstadisticas(String torneoId);
}
