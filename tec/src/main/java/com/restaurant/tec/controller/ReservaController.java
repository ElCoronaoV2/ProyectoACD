package com.restaurant.tec.controller;

import com.restaurant.tec.entity.ReservaEntity;
import com.restaurant.tec.entity.UserEntity;
import com.restaurant.tec.repository.UserRepository;
import com.restaurant.tec.service.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    @Autowired
    private ReservaService reservaService;

    @Autowired
    private UserRepository userRepository;

    // Crear reserva (Cliente)
    @PostMapping
    public ResponseEntity<ReservaEntity> createReserva(@RequestBody Map<String, Object> payload,
            Authentication authentication) {
        UserEntity user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        Long localId = Long.valueOf(payload.get("localId").toString());
        String fechaStr = payload.get("fechaHora").toString(); // ISO format presumed
        LocalDateTime fechaHora = LocalDateTime.parse(fechaStr);
        Integer personas = Integer.valueOf(payload.get("numeroPersonas").toString());
        String observaciones = (String) payload.get("observaciones");

        return ResponseEntity
                .ok(reservaService.createReserva(user.getId(), localId, fechaHora, personas, observaciones));
    }

    // Mis Reservas (Cliente)
    @GetMapping("/mis-reservas")
    public ResponseEntity<List<ReservaEntity>> getMyReservas(Authentication authentication) {
        UserEntity user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        return ResponseEntity.ok(reservaService.getReservasByUser(user.getId()));
    }

    // Reservas de un local (Empleado/CEO)
    @GetMapping("/local/{localId}")
    public ResponseEntity<List<ReservaEntity>> getReservasByLocal(@PathVariable Long localId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        // TODO: Agregar validación de permisos (si el usuario trabaja en este local o
        // es CEO)
        if (start != null && end != null) {
            return ResponseEntity.ok(reservaService.getReservasByLocalAndDateRange(localId, start, end));
        }
        return ResponseEntity.ok(reservaService.getReservasByLocal(localId));
    }

    // Verificar disponibilidad (Público/Frontend)
    @GetMapping("/availability")
    public ResponseEntity<Integer> checkAvailability(
            @RequestParam Long localId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaHora) {
        return ResponseEntity.ok(reservaService.getRemainingCapacity(localId, fechaHora));
    }

    // Endpoint para n8n: Reservas confirmadas próximas (para recordatorios)
    @GetMapping("/upcoming")
    public ResponseEntity<List<ReservaEntity>> getUpcomingReservas(@RequestParam(defaultValue = "120") int minutes) {
        return ResponseEntity.ok(reservaService.getConfirmedReservasInNextMinutes(minutes));
    }

    // Endpoint para n8n: Reservas pendientes urgentes (para cancelación automática)
    @GetMapping("/unconfirmed-urgent")
    public ResponseEntity<List<ReservaEntity>> getUrgentPendingReservas(
            @RequestParam(defaultValue = "30") int minutes) {
        return ResponseEntity.ok(reservaService.getPendingReservasStartingIn(minutes));
    }

    // Actualizar estado (Empleado/CEO)
    @PutMapping("/{id}/estado")
    public ResponseEntity<ReservaEntity> updateEstado(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        ReservaEntity.EstadoReserva estado = ReservaEntity.EstadoReserva.valueOf(payload.get("estado"));
        return ResponseEntity.ok(reservaService.updateEstado(id, estado));
    }
}
