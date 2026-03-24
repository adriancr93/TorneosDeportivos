package org.example.config;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.example.repository.*;
import org.example.service.impl.*;

public class SeedDemoDataMain {
    public static void main(String[] args) {
        MongoDatabase db = MongoDBUtil.getClient().getDatabase("TorneosDeportivos");

        boolean reset = args != null && args.length > 0 && "--reset".equalsIgnoreCase(args[0]);
        if (reset) {
            db.getCollection("equipos").deleteMany(new Document());
            db.getCollection("jugadores").deleteMany(new Document());
            db.getCollection("torneos").deleteMany(new Document());
            db.getCollection("partidos").deleteMany(new Document());
            db.getCollection("estadisticas").deleteMany(new Document());
            System.out.println("Colecciones limpiadas.");
        }

        MongoEquipoService equipoService = new MongoEquipoService(new EquipoRepository());
        MongoJugadorService jugadorService = new MongoJugadorService(new JugadorRepository());
        MongoTorneoService torneoService = new MongoTorneoService(new TorneoRepository());
        MongoPartidoService partidoService = new MongoPartidoService(new PartidoRepository(), torneoService);
        MongoEstadisticaService estadisticaService = new MongoEstadisticaService(
                new EstadisticaRepository(), partidoService, jugadorService
        );

        DataSeeder seeder = new DataSeeder(
                equipoService,
                jugadorService,
                torneoService,
                partidoService,
                estadisticaService
        );

        seeder.cargarDatosDePrueba();

        MongoCollection<Document> equipos = db.getCollection("equipos");
        MongoCollection<Document> jugadores = db.getCollection("jugadores");
        MongoCollection<Document> torneos = db.getCollection("torneos");
        MongoCollection<Document> partidos = db.getCollection("partidos");
        MongoCollection<Document> estadisticas = db.getCollection("estadisticas");

        System.out.println("--- RESUMEN EN MONGODB ---");
        System.out.println("DB: TorneosDeportivos");
        System.out.println("equipos: " + equipos.countDocuments());
        System.out.println("jugadores: " + jugadores.countDocuments());
        System.out.println("torneos: " + torneos.countDocuments());
        System.out.println("partidos: " + partidos.countDocuments());
        System.out.println("estadisticas: " + estadisticas.countDocuments());

        MongoDBUtil.close();
    }
}
