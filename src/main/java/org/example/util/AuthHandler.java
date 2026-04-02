package org.example.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.Usuario;
import org.example.service.impl.MongoUsuarioService;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Cliente interno para manejo de autenticacion en requests POST.
 */
public class AuthHandler {
    private final ObjectMapper mapper;
    private final MongoUsuarioService usuarioService;

    public AuthHandler(ObjectMapper mapper, MongoUsuarioService usuarioService) {
        this.mapper = mapper;
        this.usuarioService = usuarioService;
    }

    public Map<String, Object> handleRegister(InputStream body) throws IOException {
        Map<String, String> payload = mapper.readValue(body, new TypeReference<Map<String, String>>() {});
        String username = payload.get("username");
        String password = payload.get("password");

        Map<String, Object> response = new HashMap<>();

        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            response.put("success", false);
            response.put("message", "Username y password requeridos");
            return response;
        }

        Usuario usuario = usuarioService.registrarUsuario(username, password);
        if (usuario == null) {
            response.put("success", false);
            response.put("message", "Usuario ya existe o datos invalidos");
            return response;
        }

        response.put("success", true);
        response.put("message", "Usuario registrado exitosamente");
        response.put("usuarioId", usuario.getId());
        response.put("username", usuario.getUsername());
        return response;
    }

    public Map<String, Object> handleLogin(InputStream body) throws IOException {
        Map<String, String> payload = mapper.readValue(body, new TypeReference<Map<String, String>>() {});
        String username = payload.get("username");
        String password = payload.get("password");

        Map<String, Object> response = new HashMap<>();

        if (username == null || password == null) {
            response.put("success", false);
            response.put("message", "Username y password requeridos");
            return response;
        }

        boolean autenticado = usuarioService.autenticar(username, password);
        if (!autenticado) {
            response.put("success", false);
            response.put("message", "Credenciales invalidas");
            return response;
        }

        response.put("success", true);
        response.put("message", "Login exitoso");
        response.put("username", username);
        return response;
    }
}
