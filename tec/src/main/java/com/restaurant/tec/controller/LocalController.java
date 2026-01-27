package com.restaurant.tec.controller;

import com.restaurant.tec.dto.CreateLocalRequest;
import com.restaurant.tec.dto.LocalResponse;
import com.restaurant.tec.service.LocalService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
// @CrossOrigin - Configurado globalmente en CorsConfig
public class LocalController {

    @Autowired
    private LocalService localService;

    // Endpoint público: Obtener todos los locales
    @GetMapping("/locales")
    public ResponseEntity<List<LocalResponse>> obtenerTodosLosLocales() {
        List<LocalResponse> locales = localService.obtenerTodosLosLocales();
        return ResponseEntity.ok(locales);
    }

    // Endpoint público: Obtener un local por ID
    @GetMapping("/locales/{id}")
    public ResponseEntity<?> obtenerLocalPorId(@PathVariable Long id) {
        try {
            LocalResponse local = localService.obtenerLocalPorId(id);
            return ResponseEntity.ok(local);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    // Endpoint ADMIN: Crear un nuevo local (temporalmente sin seguridad)
    @PostMapping("/admin/locales")
    public ResponseEntity<?> crearLocal(@Valid @RequestBody CreateLocalRequest request) {
        try {
            LocalResponse local = localService.crearLocal(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(local);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    // Endpoint ADMIN: Actualizar un local (temporalmente sin seguridad)
    @PutMapping("/admin/locales/{id}")
    public ResponseEntity<?> actualizarLocal(@PathVariable Long id, 
                                             @Valid @RequestBody CreateLocalRequest request) {
        try {
            LocalResponse local = localService.actualizarLocal(id, request);
            return ResponseEntity.ok(local);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    // Endpoint ADMIN: Eliminar un local (temporalmente sin seguridad)
    @DeleteMapping("/admin/locales/{id}")
    public ResponseEntity<?> eliminarLocal(@PathVariable Long id) {
        try {
            localService.eliminarLocal(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Local eliminado exitosamente");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }
}
