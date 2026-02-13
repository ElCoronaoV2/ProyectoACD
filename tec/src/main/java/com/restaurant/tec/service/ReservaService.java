package com.restaurant.tec.service;

import com.restaurant.tec.entity.LocalEntity;
import com.restaurant.tec.entity.ReservaEntity;
import com.restaurant.tec.entity.UserEntity;
import com.restaurant.tec.repository.LocalRepository;
import com.restaurant.tec.repository.ReservaRepository;
import com.restaurant.tec.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio para gestionar reservas de restaurantes.
 * Proporciona funcionalidades para crear, consultar, cancelar y confirmar
 * asistencia a reservas.
 * Integra validación de disponibilidad, pagos con Stripe y notificaciones por
 * email.
 * 
 * @author RestaurantTec Team
 * @version 1.0
 */
@Service
public class ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LocalRepository localRepository;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private N8nService n8nService;

    /**
     * Crea una nueva reserva para un usuario en un restaurante.
     * Valida disponibilidad de aforo y confirma el pago antes de crear la reserva.
     * 
     * @param userId          ID del usuario que hace la reserva
     * @param localId         ID del restaurante
     * @param fechaHora       fecha y hora de la reserva
     * @param personas        número de personas
     * @param observaciones   observaciones adicionales (puede ser null)
     * @param paymentIntentId ID del PaymentIntent de Stripe para validar el pago
     * @return ReservaEntity la reserva creada
     * @throws RuntimeException si no se encuentra el usuario, restaurante o no hay
     *                          disponibilidad
     */
    @Transactional
    public ReservaEntity createReserva(Long userId, Long localId, LocalDateTime fechaHora, Integer personas,
            String observaciones, String paymentIntentId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        LocalEntity local = localRepository.findById(localId)
                .orElseThrow(() -> new RuntimeException("Restaurante no encontrado"));

        // Verificar aforo
        checkAvailability(local, fechaHora, personas);

        // Verificar pago si es necesario (todos pagan 1 euro)
        verifyPayment(paymentIntentId);

        ReservaEntity reserva = new ReservaEntity();
        reserva.setUsuario(user);
        reserva.setLocal(local);
        reserva.setFechaHora(fechaHora);
        reserva.setNumeroPersonas(personas);
        reserva.setObservaciones(observaciones);
        reserva.setEstado(ReservaEntity.EstadoReserva.CONFIRMADA);
        reserva.setStripePaymentIntentId(paymentIntentId);
        reserva.setEstadoPago("PAGADO");

        ReservaEntity savedReserva = reservaRepository.save(reserva);

        // Send Email via n8n
        try {
            String ceoEmail = local.getPropietario() != null ? local.getPropietario().getEmail() : "NO_OWNER";
            System.out.println("Sending confirmation via n8n. User: " + user.getEmail() + ", CEO: " + ceoEmail);

            n8nService.sendBookingConfirmation(
                    user.getEmail(),
                    user.getNombre(),
                    local.getNombre(),
                    fechaHora.toLocalDate().toString(),
                    fechaHora.toLocalTime().toString(),
                    personas,
                    savedReserva.getId().toString(),
                    observaciones,
                    local.getPropietario() != null ? local.getPropietario().getEmail() : null);
        } catch (Exception e) {
            System.err.println("Error sending confirmation via n8n: " + e.getMessage());
        }

        return savedReserva;
    }

    @Transactional
    public ReservaEntity createGuestReservation(String nombre, String email, String telefono, Long localId,
            LocalDateTime fechaHora, Integer personas, String observaciones, String paymentIntentId) {
        LocalEntity local = localRepository.findById(localId)
                .orElseThrow(() -> new RuntimeException("Restaurante no encontrado"));

        // Verificar aforo
        checkAvailability(local, fechaHora, personas);

        // Verificar pago
        verifyPayment(paymentIntentId);

        ReservaEntity reserva = new ReservaEntity();

        // Intentar vincular con usuario existente por email
        userRepository.findByEmail(email).ifPresentOrElse(
                existingUser -> reserva.setUsuario(existingUser),
                () -> reserva.setUsuario(null));

        reserva.setNombreInvitado(nombre);
        reserva.setEmailInvitado(email);
        reserva.setTelefonoInvitado(telefono);
        reserva.setLocal(local);
        reserva.setFechaHora(fechaHora);
        reserva.setNumeroPersonas(personas);
        reserva.setObservaciones(observaciones);
        reserva.setEstado(ReservaEntity.EstadoReserva.CONFIRMADA);
        reserva.setStripePaymentIntentId(paymentIntentId);
        reserva.setEstadoPago("PAGADO");

        ReservaEntity savedReserva = reservaRepository.save(reserva);

        // Send Email via n8n
        try {
            n8nService.sendBookingConfirmation(
                    email,
                    nombre,
                    local.getNombre(),
                    fechaHora.toLocalDate().toString(),
                    fechaHora.toLocalTime().toString(),
                    personas,
                    savedReserva.getId().toString(),
                    observaciones,
                    local.getPropietario() != null ? local.getPropietario().getEmail() : null);
        } catch (Exception e) {
            System.err.println("Error sending confirmation via n8n: " + e.getMessage());
        }

        return savedReserva;
    }

    private void verifyPayment(String paymentIntentId) {
        if (paymentIntentId != null && paymentIntentId.startsWith("mock_")) {
            return; // Bypass for testing
        }
        if (paymentIntentId == null || paymentIntentId.isEmpty()) {
            throw new RuntimeException("El ID del pago es obligatorio.");
        }
        try {
            com.stripe.model.PaymentIntent intent = paymentService.retrievePaymentIntent(paymentIntentId);
            if (!"succeeded".equals(intent.getStatus())) {
                throw new RuntimeException("El pago no se ha completado. Estado: " + intent.getStatus());
            }
        } catch (com.stripe.exception.StripeException e) {
            throw new RuntimeException("Error al verificar el pago con Stripe: " + e.getMessage());
        }
    }

    private int getMaxCapacityForTime(LocalEntity local, java.time.LocalTime time) {
        int maxCapacity = local.getCapacidad(); // Default

        if (local.getCapacidadComida() != null && local.getAperturaComida() != null
                && local.getCierreComida() != null) {
            if (!time.isBefore(local.getAperturaComida()) && !time.isAfter(local.getCierreComida())) {
                maxCapacity = local.getCapacidadComida();
            }
        }

        if (local.getCapacidadCena() != null && local.getAperturaCena() != null && local.getCierreCena() != null) {
            if (!time.isBefore(local.getAperturaCena()) && !time.isAfter(local.getCierreCena())) {
                maxCapacity = local.getCapacidadCena();
            }
        }
        return maxCapacity;
    }

    private void checkAvailability(LocalEntity local, LocalDateTime fechaHora, Integer personas) {
        int maxCapacity = getMaxCapacityForTime(local, fechaHora.toLocalTime());

        LocalDateTime rangeStart = fechaHora.minusHours(2);
        LocalDateTime rangeEnd = fechaHora.plusHours(2);

        Integer occupiedSeats = reservaRepository.countPeopleInFrame(local.getId(), rangeStart, rangeEnd);

        if (occupiedSeats + personas > maxCapacity) {
            throw new RuntimeException("No hay disponibilidad para " + personas
                    + " personas en este horario. Capacidad restante aproximada: "
                    + (maxCapacity - occupiedSeats));
        }
    }

    public Integer getRemainingCapacity(Long localId, LocalDateTime fechaHora) {
        LocalEntity local = localRepository.findById(localId)
                .orElseThrow(() -> new RuntimeException("Local no encontrado"));

        int maxCapacity = getMaxCapacityForTime(local, fechaHora.toLocalTime());

        LocalDateTime rangeStart = fechaHora.minusHours(2);
        LocalDateTime rangeEnd = fechaHora.plusHours(2);
        Integer occupied = reservaRepository.countPeopleInFrame(localId, rangeStart, rangeEnd);

        return Math.max(0, maxCapacity - occupied);
    }

    public List<ReservaEntity> getConfirmedReservasInNextMinutes(int minutes) {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusMinutes(minutes);
        return reservaRepository.findConfirmedReservasInWindow(start, end);
    }

    public List<ReservaEntity> getPendingReservasStartingIn(int minutes) {
        // Reservas pendientes que empiezan entre YA y YA + minutes
        // Útil para cancelar si queda poco tiempo y sigue PENDIENTE
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusMinutes(minutes);
        return reservaRepository.findPendingReservasInWindow(start, end);
    }

    public List<ReservaEntity> getReservasByLocal(Long localId) {
        return reservaRepository.findByLocalId(localId);
    }

    public List<ReservaEntity> getReservasByUser(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return reservaRepository.findByUsuarioIdOrEmailInvitado(userId, user.getEmail());
    }

    public List<ReservaEntity> getReservasByLocalAndDateRange(Long localId, LocalDateTime start, LocalDateTime end) {
        return reservaRepository.findReservasByLocalAndFechaRange(localId, start, end);
    }

    @Transactional
    public ReservaEntity updateEstado(Long reservaId, ReservaEntity.EstadoReserva nuevoEstado) {
        ReservaEntity reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        reserva.setEstado(nuevoEstado);
        return reservaRepository.save(reserva);
    }

    @Transactional
    public void confirmAttendance(Long reservaId) {
        ReservaEntity reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
        reserva.setAsistenciaConfirmada(true);
        reservaRepository.save(reserva);
    }
}
