package org.example.repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.Document;
import org.example.config.MongoDBUtil;
import org.example.model.Partido;
import org.example.model.PartidoEstado;

import java.util.ArrayList;
import java.util.List;

public class PartidoRepository {
    private final MongoCollection<Document> collection;

    public PartidoRepository() {
        MongoDatabase db = MongoDBUtil.getClient().getDatabase("TorneosDeportivos");
        this.collection = db.getCollection("partidos");
    }

    public Partido save(Partido p) {
        Document doc = toDocument(p);
        collection.replaceOne(Filters.eq("_id", p.getId()), doc, new ReplaceOptions().upsert(true));
        return p;
    }

    public Partido findById(String id) {
        Document doc = collection.find(Filters.eq("_id", id)).first();
        return doc != null ? fromDocument(doc) : null;
    }

    public List<Partido> findByTorneoId(String torneoId) {
        List<Partido> list = new ArrayList<>();
        for (Document doc : collection.find(Filters.eq("torneoId", torneoId))) {
            list.add(fromDocument(doc));
        }
        return list;
    }

    public List<Partido> findAll() {
        List<Partido> list = new ArrayList<>();
        for (Document doc : collection.find()) {
            list.add(fromDocument(doc));
        }
        return list;
    }

    private Document toDocument(Partido p) {
        return new Document("_id", p.getId())
                .append("torneoId", p.getTorneoId())
                .append("fecha", p.getFecha())
                .append("equipoLocalId", p.getEquipoLocalId())
                .append("equipoVisitanteId", p.getEquipoVisitanteId())
                .append("golesLocal", p.getGolesLocal())
                .append("golesVisitante", p.getGolesVisitante())
                .append("estado", p.getEstado() != null ? p.getEstado().name() : null);
    }

    private Partido fromDocument(Document doc) {
        Partido p = new Partido();
        p.setId(doc.getString("_id"));
        p.setTorneoId(doc.getString("torneoId"));
        p.setFecha(doc.getString("fecha"));
        p.setEquipoLocalId(doc.getString("equipoLocalId"));
        p.setEquipoVisitanteId(doc.getString("equipoVisitanteId"));
        p.setGolesLocal(doc.getInteger("golesLocal", 0));
        p.setGolesVisitante(doc.getInteger("golesVisitante", 0));
        String estado = doc.getString("estado");
        if (estado != null) p.setEstado(PartidoEstado.valueOf(estado));
        return p;
    }
}
