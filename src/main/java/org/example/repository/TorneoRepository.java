package org.example.repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.Document;
import org.example.config.MongoDBUtil;
import org.example.model.Torneo;
import org.example.model.TorneoEstado;

import java.util.ArrayList;
import java.util.List;

public class TorneoRepository {
    private final MongoCollection<Document> collection;

    public TorneoRepository() {
        MongoDatabase db = MongoDBUtil.getClient().getDatabase("TorneosDeportivos");
        this.collection = db.getCollection("torneos");
    }

    public Torneo save(Torneo t) {
        Document doc = toDocument(t);
        collection.replaceOne(Filters.eq("_id", t.getId()), doc, new ReplaceOptions().upsert(true));
        return t;
    }

    public Torneo findById(String id) {
        Document doc = collection.find(Filters.eq("_id", id)).first();
        return doc != null ? fromDocument(doc) : null;
    }

    public List<Torneo> findAll() {
        List<Torneo> list = new ArrayList<>();
        for (Document doc : collection.find()) {
            list.add(fromDocument(doc));
        }
        return list;
    }

    private Document toDocument(Torneo t) {
        return new Document("_id", t.getId())
                .append("nombre", t.getNombre())
                .append("sede", t.getSede())
                .append("fechaInicio", t.getFechaInicio())
                .append("fechaFin", t.getFechaFin())
                .append("estado", t.getEstado() != null ? t.getEstado().name() : null)
                .append("equipoIds", t.getEquipoIds());
    }

    @SuppressWarnings("unchecked")
    private Torneo fromDocument(Document doc) {
        Torneo t = new Torneo();
        t.setId(doc.getString("_id"));
        t.setNombre(doc.getString("nombre"));
        t.setSede(doc.getString("sede"));
        t.setFechaInicio(doc.getString("fechaInicio"));
        t.setFechaFin(doc.getString("fechaFin"));
        String estado = doc.getString("estado");
        if (estado != null) t.setEstado(TorneoEstado.valueOf(estado));
        List<String> ids = (List<String>) doc.get("equipoIds");
        t.setEquipoIds(ids != null ? new ArrayList<>(ids) : new ArrayList<>());
        return t;
    }
}
