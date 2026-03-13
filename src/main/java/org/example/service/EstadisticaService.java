package org.example.service;

import org.example.model.Estadistica;

public interface EstadisticaService {
    Estadistica generarEstadisticas(String torneoId);
    Estadistica visualizarEstadisticas(String torneoId);
}
