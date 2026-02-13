package com.restaurant.tec.scheduler;

import com.restaurant.tec.entity.ReservaEntity;
import com.restaurant.tec.repository.ReservaRepository;
import com.restaurant.tec.service.N8nService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ReservationScheduler {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private N8nService n8nService;

    // Ejecutar cada 15 minutos
    @Scheduled(cron = "0 */15 * * * *")
    @Transactional
    public void sendReminders() {
        LocalDateTime now = LocalDateTime.now();
        // Buscar reservas que sean mañana entre ahora y ahora + 15 min
        LocalDateTime startWindow = now.plusHours(24);
        LocalDateTime endWindow = startWindow.plusMinutes(15);

        List<ReservaEntity> upcomingReservations = reservaRepository.findConfirmedReservasInWindow(startWindow,
                endWindow);

        for (ReservaEntity reservation : upcomingReservations) {
            System.out.println("Processing reminder for reservation: " + reservation.getId());

            String email = reservation.getEmailInvitado();
            if (email == null && reservation.getUsuario() != null) {
                email = reservation.getUsuario().getEmail();
            }

            if (email != null) {
                n8nService.sendBookingReminder(
                        email,
                        reservation.getNombreInvitado() != null ? reservation.getNombreInvitado()
                                : (reservation.getUsuario() != null ? reservation.getUsuario().getNombre() : "Cliente"),
                        reservation.getLocal().getNombre(),
                        reservation.getFechaHora().toLocalDate().toString(),
                        reservation.getFechaHora().toLocalTime().toString(),
                        reservation.getNumeroPersonas(),
                        reservation.getId().toString());
            }
        }
    }
}
