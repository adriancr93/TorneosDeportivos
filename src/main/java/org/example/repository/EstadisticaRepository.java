package org.example.repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.Document;
import org.example.config.MongoDBUtil;
import org.example.model.Estadistica;
import org.example.model.GoleadorItem;
import org.example.model.TablaPosicionItem;

import java.util.ArrayList;
import java.util.List;

public class EstadisticaRepository {
    private final MongoCollection<Document> collection;

    public EstadisticaRepository() {
        MongoDatabase db = MongoDBUtil.getClient().getDatabase("TorneosDeportivos");
        this.collection = db.getCollection("estadisticas");
    }

    public Estadistica save(Estadistica e) {
        Document doc = toDocument(e);
        collection.replaceOne(Filters.eq("torneoId", e.getTorneoId()), doc, new ReplaceOptions().upsert(true));
        return e;
    }

    public Estadistica findByTorneoId(String torneoId) {
        Document doc = collection.find(Filters.eq("torneoId", torneoId)).first();
        return doc != null ? fromDocument(doc) : null;
    }

    private Document toDocument(Estadistica e) {
        List<Document> tablaDoc = new ArrayList<>();
        if (e.getTabla() != null) {
            for (TablaPosicionItem t : e.getTabla()) {
                tablaDoc.add(new Document("equipoId", t.getEquipoId())
                        .append("puntos", t.getPuntos())
                        .append("golesFavor", t.getGolesFavor())
                        .append("golesContra", t.getGolesContra())
                        .append("partidosJugados", t.getPartidosJugados()));
            }
        }
        List<Document> golDoc = new ArrayList<>();
        if (e.getGoleadores() != null) {
            for (GoleadorItem g : e.getGoleadores()) {
                golDoc.add(new Document("jugadorId", g.getJugadorId()).append("goles", g.getGoles()));
            }
        }
        return new Document("_id", e.getId())
                .append("torneoId", e.getTorneoId())
                .append("fechaGeneracion", e.getFechaGeneracion())
                .append("tabla", tablaDoc)
                .append("goleadores", golDoc);
    }

    @SuppressWarnings("unchecked")
    private Estadistica fromDocument(Document doc) {
        Estadistica e = new Estadistica();
        e.setId(doc.getString("_id"));
        e.setTorneoId(doc.getString("torneoId"));
        e.setFechaGeneracion(doc.getString("fechaGeneracion"));

        List<Document> tablaDoc = (List<Document>) doc.get("tabla");
        List<TablaPosicionItem> tabla = new ArrayList<>();
        if (tablaDoc != null) {
            for (Document d : tablaDoc) {
                tabla.add(new TablaPosicionItem(
                        d.getString("equipoId"),
                        d.getInteger("puntos", 0),
                        d.getInteger("golesFavor", 0),
                        d.getInteger("golesContra", 0),
                        d.getInteger("partidosJugados", 0)));
            }
        }
        e.setTabla(tabla);

        List<Document> golDoc = (List<Document>) doc.get("goleadores");
        List<GoleadorItem> goleadores = new ArrayList<>();
        if (golDoc != null) {
            for (Document d : golDoc) {
                goleadores.add(new GoleadorItem(d.getString("jugadorId"), d.getInteger("goles", 0)));
            }
        }
        e.setGoleadores(goleadores);
        return e;
    }
}
