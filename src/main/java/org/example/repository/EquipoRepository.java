package org.example.repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.Document;
import org.example.config.MongoDBUtil;
import org.example.model.Equipo;

import java.util.ArrayList;
import java.util.List;

public class EquipoRepository {
    private final MongoCollection<Document> collection;

    public EquipoRepository() {
        MongoDatabase db = MongoDBUtil.getClient().getDatabase("TorneosDeportivos");
        this.collection = db.getCollection("equipos");
    }

    public Equipo save(Equipo e) {
        Document doc = toDocument(e);
        collection.replaceOne(Filters.eq("_id", e.getId()), doc, new ReplaceOptions().upsert(true));
        return e;
    }

    public Equipo findById(String id) {
        Document doc = collection.find(Filters.eq("_id", id)).first();
        return doc != null ? fromDocument(doc) : null;
    }

    public List<Equipo> findAll() {
        List<Equipo> list = new ArrayList<>();
        for (Document doc : collection.find()) {
            list.add(fromDocument(doc));
        }
        return list;
    }

    public boolean deleteById(String id) {
        return collection.deleteOne(Filters.eq("_id", id)).getDeletedCount() > 0;
    }

    private Document toDocument(Equipo e) {
        return new Document("_id", e.getId())
                .append("nombre", e.getNombre())
                .append("ciudad", e.getCiudad())
                .append("anioFundacion", e.getAnioFundacion())
                .append("entrenador", e.getEntrenador())
                .append("golesFavor", e.getGolesFavor())
                .append("golesContra", e.getGolesContra())
                .append("puntos", e.getPuntos())
                .append("partidosJugados", e.getPartidosJugados());
    }

    private Equipo fromDocument(Document doc) {
        Equipo e = new Equipo();
        e.setId(doc.getString("_id"));
        e.setNombre(doc.getString("nombre"));
        e.setCiudad(doc.getString("ciudad"));
        e.setAnioFundacion(doc.getInteger("anioFundacion", 0));
        e.setEntrenador(doc.getString("entrenador"));
        e.setGolesFavor(doc.getInteger("golesFavor", 0));
        e.setGolesContra(doc.getInteger("golesContra", 0));
        e.setPuntos(doc.getInteger("puntos", 0));
        e.setPartidosJugados(doc.getInteger("partidosJugados", 0));
        return e;
    }
}
