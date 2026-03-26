package org.example.service.impl;

import org.example.model.Usuario;
import org.example.repository.UsuarioRepository;

import java.util.UUID;

public class MongoUsuarioService {
    private final UsuarioRepository repo;

    public MongoUsuarioService(UsuarioRepository repo) {
        this.repo = repo;
    }

    public Usuario registrarUsuario(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return null;
        }
        String normalizedUsername = username.trim().toLowerCase();
        if (repo.findByUsername(normalizedUsername) != null) {
            return null;
        }

        Usuario u = new Usuario();
        u.setId(UUID.randomUUID().toString().substring(0, 8));
        u.setUsername(normalizedUsername);
        u.setPassword(password);
        return repo.save(u);
    }

    public boolean autenticar(String username, String password) {
        if (username == null || password == null) {
            return false;
        }
        Usuario usuario = repo.findByUsername(username.trim().toLowerCase());
        return usuario != null && password.equals(usuario.getPassword());
    }

    public void asegurarUsuarioDemo(String username, String password) {
        if (repo.findByUsername(username.trim().toLowerCase()) == null) {
            registrarUsuario(username, password);
        }
    }
}
