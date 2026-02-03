package com.restaurant.tec.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador para el endpoint raíz /api
 * Requiere autenticación (Basic Auth o JWT)
 */
@RestController
@RequestMapping("/api")
public class ApiInfoController {

    @GetMapping({ "", "/" })
    public ResponseEntity<Map<String, Object>> getApiInfo(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();

        response.put("api", "Restaurant-Tec API");
        response.put("version", "1.0.0");
        response.put("status", "online");
        response.put("authenticated_as", authentication != null ? authentication.getName() : "anonymous");

        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("GET /api/locales", "Lista de restaurantes (público)");
        endpoints.put("POST /api/auth/login", "Iniciar sesión");
        endpoints.put("POST /api/auth/register", "Registrar usuario");
        endpoints.put("GET /api/reservas", "Mis reservas (autenticado)");
        endpoints.put("GET /api/menus", "Menús disponibles (autenticado)");

        response.put("endpoints", endpoints);

        return ResponseEntity.ok(response);
    }
}
