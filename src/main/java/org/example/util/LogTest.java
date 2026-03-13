package org.example.util;

public class LogTest {
    public static void main(String[] args) {
        LoggerUtil.logInfo("Prueba de integración: log enviado a ElasticSearch desde Java");
        LoggerUtil.logError("Error de prueba", new Exception("Excepción de prueba"));
    }
}
