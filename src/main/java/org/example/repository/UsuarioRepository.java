package org.example.repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.Document;
import org.example.config.MongoDBUtil;
import org.example.model.Usuario;

import java.util.ArrayList;
import java.util.List;

public class UsuarioRepository {
    private final MongoCollection<Document> collection;

    public UsuarioRepository() {
        MongoDatabase db = MongoDBUtil.getClient().getDatabase("TorneosDeportivos");
        this.collection = db.getCollection("usuarios");
    }

    public Usuario save(Usuario u) {
        Document doc = toDocument(u);
        collection.replaceOne(Filters.eq("_id", u.getId()), doc, new ReplaceOptions().upsert(true));
        return u;
    }

    public Usuario findById(String id) {
        Document doc = collection.find(Filters.eq("_id", id)).first();
        return doc != null ? fromDocument(doc) : null;
    }

    public Usuario findByUsername(String username) {
        Document doc = collection.find(Filters.eq("username", username)).first();
        return doc != null ? fromDocument(doc) : null;
    }

    public List<Usuario> findAll() {
        List<Usuario> list = new ArrayList<>();
        for (Document doc : collection.find()) {
            list.add(fromDocument(doc));
        }
        return list;
    }

    private Document toDocument(Usuario u) {
        return new Document("_id", u.getId())
                .append("username", u.getUsername())
                .append("password", u.getPassword());
    }

    private Usuario fromDocument(Document doc) {
        Usuario u = new Usuario();
        u.setId(doc.getString("_id"));
        u.setUsername(doc.getString("username"));
        u.setPassword(doc.getString("password"));
        return u;
    }
}
