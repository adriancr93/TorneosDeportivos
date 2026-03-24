package org.example.repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.Document;
import org.example.config.MongoDBUtil;
import org.example.model.Jugador;

import java.util.ArrayList;
import java.util.List;

public class JugadorRepository {
    private final MongoCollection<Document> collection;

    public JugadorRepository() {
        MongoDatabase db = MongoDBUtil.getClient().getDatabase("TorneosDeportivos");
        this.collection = db.getCollection("jugadores");
    }

    public Jugador save(Jugador j) {
        Document doc = toDocument(j);
        collection.replaceOne(Filters.eq("_id", j.getId()), doc, new ReplaceOptions().upsert(true));
        return j;
    }

    public Jugador findById(String id) {
        Document doc = collection.find(Filters.eq("_id", id)).first();
        return doc != null ? fromDocument(doc) : null;
    }

    public List<Jugador> findByEquipoId(String equipoId) {
        List<Jugador> list = new ArrayList<>();
        for (Document doc : collection.find(Filters.eq("equipoId", equipoId))) {
            list.add(fromDocument(doc));
        }
        return list;
    }

    public List<Jugador> findAll() {
        List<Jugador> list = new ArrayList<>();
        for (Document doc : collection.find()) {
            list.add(fromDocument(doc));
        }
        return list;
    }

    private Document toDocument(Jugador j) {
        return new Document("_id", j.getId())
                .append("nombre", j.getNombre())
                .append("edad", j.getEdad())
                .append("posicion", j.getPosicion())
                .append("dorsal", j.getDorsal())
                .append("goles", j.getGoles())
                .append("equipoId", j.getEquipoId());
    }

    private Jugador fromDocument(Document doc) {
        Jugador j = new Jugador();
        j.setId(doc.getString("_id"));
        j.setNombre(doc.getString("nombre"));
        j.setEdad(doc.getInteger("edad", 0));
        j.setPosicion(doc.getString("posicion"));
        j.setDorsal(doc.getInteger("dorsal", 0));
        j.setGoles(doc.getInteger("goles", 0));
        j.setEquipoId(doc.getString("equipoId"));
        return j;
    }
}
