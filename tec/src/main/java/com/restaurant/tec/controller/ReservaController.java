package com.restaurant.tec.controller;

import com.restaurant.tec.entity.ReservaEntity;
import com.restaurant.tec.entity.UserEntity;
import com.restaurant.tec.repository.UserRepository;
import com.restaurant.tec.service.PaymentService;
import com.restaurant.tec.service.ReservaService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para gestionar reservas de restaurantes.
 * Proporciona endpoints para crear, consultar y gestionar reservas, incluyendo integración con pagos.
 * 
 * @author RestaurantTec Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    @Autowired
    private ReservaService reservaService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private UserRepository userRepository;

    /**
     * Crea un PaymentIntent de Stripe para procesar el pago de una reserva.
     * Monto fijo de 1 euro (100 céntimos).
     * 
     * @param payload datos del pago (puede estar vacío, monto es fijo)
     * @return ResponseEntity con el clientSecret para completar el pago
     */
    @PostMapping("/create-payment-intent")
    public ResponseEntity<Map<String, String>> createPaymentIntent(@RequestBody Map<String, Object> payload) {
        try {
            // Monto fijo de 1 euro (100 centavos)
            PaymentIntent intent = paymentService.createPaymentIntent(100L, "eur", "Reserva Restaurante");
            return ResponseEntity.ok(Map.of("clientSecret", intent.getClientSecret()));
        } catch (StripeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Helper para convertir a DTO
    private com.restaurant.tec.dto.ReservaDTO mapToDTO(ReservaEntity entity) {
        return new com.restaurant.tec.dto.ReservaDTO(
                entity.getId(),
                entity.getUsuario() != null ? entity.getUsuario().getId() : null,
                entity.getUsuario() != null ? entity.getUsuario().getNombre() : null,
                entity.getLocal() != null ? entity.getLocal().getId() : null,
                entity.getLocal() != null ? entity.getLocal().getNombre() : null,
                entity.getFechaHora(),
                entity.getNumeroPersonas(),
                entity.getObservaciones(),
                entity.getEstado().name(),
                entity.getEstadoPago(),
                entity.getNombreInvitado(),
                entity.getEmailInvitado(),
                entity.getTelefonoInvitado(),
                entity.getAsistenciaConfirmada());
    }

    // Crear reserva (Cliente)
    @PostMapping
    public ResponseEntity<com.restaurant.tec.dto.ReservaDTO> createReserva(@RequestBody Map<String, Object> payload,
            Authentication authentication) {
        UserEntity user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        Long localId = Long.valueOf(payload.get("localId").toString());
        String fechaStr = payload.get("fechaHora").toString(); // ISO format presumed
        LocalDateTime fechaHora = LocalDateTime.parse(fechaStr);
        Integer personas = Integer.valueOf(payload.get("numeroPersonas").toString());
        String observaciones = (String) payload.get("observaciones");
        String paymentIntentId = (String) payload.get("paymentIntentId");

        ReservaEntity created = reservaService.createReserva(user.getId(), localId, fechaHora, personas, observaciones,
                paymentIntentId);
        return ResponseEntity.ok(mapToDTO(created));
    }

    // Crear reserva (Invitado - Sin Login)
    @PostMapping("/guest")
    public ResponseEntity<com.restaurant.tec.dto.ReservaDTO> createGuestReserva(
            @RequestBody com.restaurant.tec.dto.GuestReservationRequest request) {
        ReservaEntity created = reservaService.createGuestReservation(
                request.getNombre(),
                request.getEmail(),
                request.getTelefono(),
                request.getLocalId(),
                request.getFechaHora(),
                request.getNumeroPersonas(),
                request.getObservaciones(),
                request.getPaymentIntentId());
        return ResponseEntity.ok(mapToDTO(created));
    }

    // Mis Reservas (Cliente)
    @GetMapping("/mis-reservas")
    public ResponseEntity<List<com.restaurant.tec.dto.ReservaDTO>> getMyReservas(Authentication authentication) {
        UserEntity user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        List<ReservaEntity> entities = reservaService.getReservasByUser(user.getId());
        List<com.restaurant.tec.dto.ReservaDTO> dtos = entities.stream()
                .map(this::mapToDTO)
                .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // Reservas de un local (Empleado/CEO)
    @GetMapping("/local/{localId}")
    public ResponseEntity<List<com.restaurant.tec.dto.ReservaDTO>> getReservasByLocal(@PathVariable Long localId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        List<ReservaEntity> entities;
        if (start != null && end != null) {
            entities = reservaService.getReservasByLocalAndDateRange(localId, start, end);
        } else {
            entities = reservaService.getReservasByLocal(localId);
        }

        List<com.restaurant.tec.dto.ReservaDTO> dtos = entities.stream()
                .map(this::mapToDTO)
                .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // Verificar disponibilidad (Público/Frontend)
    @GetMapping("/availability")
    public ResponseEntity<Integer> checkAvailability(
            @RequestParam Long localId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaHora) {
        return ResponseEntity.ok(reservaService.getRemainingCapacity(localId, fechaHora));
    }

    // Endpoint para n8n: Reservas confirmadas próximas
    @GetMapping("/upcoming")
    public ResponseEntity<List<com.restaurant.tec.dto.ReservaDTO>> getUpcomingReservas(
            @RequestParam(defaultValue = "120") int minutes) {
        List<ReservaEntity> entities = reservaService.getConfirmedReservasInNextMinutes(minutes);
        return ResponseEntity.ok(entities.stream().map(this::mapToDTO).collect(java.util.stream.Collectors.toList()));
    }

    // Endpoint para n8n: Reservas pendientes urgentes
    @GetMapping("/unconfirmed-urgent")
    public ResponseEntity<List<com.restaurant.tec.dto.ReservaDTO>> getUrgentPendingReservas(
            @RequestParam(defaultValue = "30") int minutes) {
        List<ReservaEntity> entities = reservaService.getPendingReservasStartingIn(minutes);
        return ResponseEntity.ok(entities.stream().map(this::mapToDTO).collect(java.util.stream.Collectors.toList()));
    }

    // Actualizar estado (Empleado/CEO)
    @PutMapping("/{id}/estado")
    public ResponseEntity<com.restaurant.tec.dto.ReservaDTO> updateEstado(@PathVariable Long id,
            @RequestBody Map<String, String> payload) {
        ReservaEntity.EstadoReserva estado = ReservaEntity.EstadoReserva.valueOf(payload.get("estado"));
        ReservaEntity updated = reservaService.updateEstado(id, estado);
        return ResponseEntity.ok(mapToDTO(updated));
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<Void> confirmAttendance(@PathVariable Long id) {
        reservaService.confirmAttendance(id);
        return ResponseEntity.ok().build();
    }
}
