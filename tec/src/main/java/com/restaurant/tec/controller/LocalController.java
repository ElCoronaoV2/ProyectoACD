package com.restaurant.tec.controller;

import com.restaurant.tec.dto.CreateLocalRequest;
import com.restaurant.tec.dto.LocalResponse;
import com.restaurant.tec.entity.MenuEntity;
import com.restaurant.tec.service.LocalService;
import com.restaurant.tec.service.MenuService;
import com.restaurant.tec.service.MenuScheduleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controlador REST para gestionar restaurantes (locales).
 * Proporciona endpoints públicos para consultar restaurantes y endpoints
 * administrativos
 * para crear, actualizar y eliminar locales.
 * 
 * @author RestaurantTec Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api")
// @CrossOrigin - Configurado globalmente en CorsConfig
public class LocalController {

    @Autowired
    private LocalService localService;

    @Autowired
    private MenuService menuService;

    @Autowired
    private MenuScheduleService menuScheduleService;

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

    // Endpoint público: Obtener menús de un local (Solo el programado para hoy)
    @GetMapping("/locales/{id}/menus")
    public ResponseEntity<List<MenuEntity>> obtenerMenusDelLocal(@PathVariable Long id) {
        // Obtenemos el menú programado para el día actual
        Optional<MenuEntity> menuDelDia = menuScheduleService.getMenuDelDia(id);

        if (menuDelDia.isPresent()) {
            return ResponseEntity.ok(List.of(menuDelDia.get()));
        } else {
            // Si no hay menú programado, de momento devolvemos lista vacía
            // O podríamos devolver todos si el usuario lo prefiere, pero pidió "solo el
            // programado"
            return ResponseEntity.ok(List.of());
        }
    }

    // Endpoint público: Obtener el MENÚ DEL DÍA de un local
    @GetMapping("/locales/{id}/menu-del-dia")
    public ResponseEntity<?> obtenerMenuDelDia(@PathVariable Long id) {
        Optional<MenuEntity> menu = menuScheduleService.getMenuDelDia(id);
        if (menu.isPresent()) {
            return ResponseEntity.ok(menu.get());
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("message", "No hay menú programado para hoy");
            return ResponseEntity.ok(response);
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

    // Endpoint para añadir una valoración al restaurante
    @PostMapping("/locales/{id}/reviews")
    public ResponseEntity<?> addLocalReview(@PathVariable Long id, @RequestBody Map<String, Object> reviewData) {
        try {
            Integer rating = (Integer) reviewData.get("rating");
            String comment = (String) reviewData.get("comment");

            if (rating == null || rating < 1 || rating > 5) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "La puntuación debe estar entre 1 y 5");
                return ResponseEntity.badRequest().body(error);
            }

            localService.addReview(id, rating, comment);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Valoración añadida correctamente");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    // Endpoint para obtener reseñas de un local
    @GetMapping("/locales/{id}/reviews")
    public ResponseEntity<List<com.restaurant.tec.dto.ResenaResponse>> getLocalReviews(@PathVariable Long id) {
        return ResponseEntity.ok(localService.getReviews(id));
    }
}
