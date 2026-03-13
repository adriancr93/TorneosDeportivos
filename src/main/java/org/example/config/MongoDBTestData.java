package org.example.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

public class MongoDBTestData {
    public static void main(String[] args) {
        MongoClient client = MongoDBUtil.getClient();
        MongoDatabase db = client.getDatabase("TorneosDeportivos");

        // Crear colecciones y datos de prueba
        MongoCollection<Document> equipos = db.getCollection("equipos");
        equipos.insertOne(new Document("nombre", "Ticos FC").append("ciudad", "San José").append("anioFundacion", 2000));

        MongoCollection<Document> jugadores = db.getCollection("jugadores");
        jugadores.insertOne(new Document("nombre", "Juan Perez").append("edad", 25).append("equipoId", "Ticos FC"));

        // Validar conexión
        System.out.println("Equipos count: " + equipos.countDocuments());
        System.out.println("Jugadores count: " + jugadores.countDocuments());

        // Cerrar conexión
        MongoDBUtil.close();
        System.out.println("Conexión cerrada.");
    }
}
