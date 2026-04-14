package org.example.repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.Document;
import org.example.config.MongoDBUtil;
import org.example.model.ModalidadTorneo;
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
            .append("modalidad", t.getModalidad() != null ? t.getModalidad().name() : null)
                .append("estado", t.getEstado() != null ? t.getEstado().name() : null)
            .append("equipoIds", t.getEquipoIds())
            .append("campeonId", t.getCampeonId())
            .append("subcampeonId", t.getSubcampeonId())
            .append("tercerLugarId", t.getTercerLugarId());
    }

    @SuppressWarnings("unchecked")
    private Torneo fromDocument(Document doc) {
        Torneo t = new Torneo();
        t.setId(doc.getString("_id"));
        t.setNombre(doc.getString("nombre"));
        t.setSede(doc.getString("sede"));
        t.setFechaInicio(doc.getString("fechaInicio"));
        t.setFechaFin(doc.getString("fechaFin"));
        String modalidad = doc.getString("modalidad");
        if (modalidad != null) t.setModalidad(ModalidadTorneo.valueOf(modalidad));
        String estado = doc.getString("estado");
        if (estado != null) t.setEstado(TorneoEstado.valueOf(estado));
        List<String> ids = (List<String>) doc.get("equipoIds");
        t.setEquipoIds(ids != null ? new ArrayList<>(ids) : new ArrayList<>());
        t.setCampeonId(doc.getString("campeonId"));
        t.setSubcampeonId(doc.getString("subcampeonId"));
        t.setTercerLugarId(doc.getString("tercerLugarId"));
        return t;
    }
}
