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

@Service
public class ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LocalRepository localRepository;

    @Transactional
    public ReservaEntity createReserva(Long userId, Long localId, LocalDateTime fechaHora, Integer personas,
            String observaciones) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        LocalEntity local = localRepository.findById(localId)
                .orElseThrow(() -> new RuntimeException("Restaurante no encontrado"));

        // Verificar aforo
        checkAvailability(local, fechaHora, personas);

        ReservaEntity reserva = new ReservaEntity();
        reserva.setUsuario(user);
        reserva.setLocal(local);
        reserva.setFechaHora(fechaHora);
        reserva.setNumeroPersonas(personas);
        reserva.setObservaciones(observaciones);
        reserva.setEstado(ReservaEntity.EstadoReserva.PENDIENTE);

        return reservaRepository.save(reserva);
    }

    private void checkAvailability(LocalEntity local, LocalDateTime fechaHora, Integer personas) {
        // Asumimos duración de 2 horas. Revisamos conflicto en ventana de +/- 2h
        // (simple)
        // O mejor: Consideramos ocupación en el momento de inicio.
        // Contamos reservas que inician entre T-2h y T+2h para ver "pico" de ocupación?
        // Simplificación: Sumamos gente en reservas que solapen.
        // R.start en (Start - 2h, Start + 2h).
        LocalDateTime rangeStart = fechaHora.minusHours(2);
        LocalDateTime rangeEnd = fechaHora.plusHours(2);

        Integer occupiedSeats = reservaRepository.countPeopleInFrame(local.getId(), rangeStart, rangeEnd);

        // Esta logica es muy conservadora (suma todo lo que toque el rango).
        // Para hacerlo exacto necesitariamos ver la ocupacion minuto a minuto o slots,
        // pero para MVP esto evita overbooking masivo.
        // Un ajuste mejor: Solo contar reservas activas en el momento T?
        // No, porque si yo reservo a las 14:00, ocupo hasta las 16:00.
        // Si alguien reservó a las 13:00, ocupa hasta las 15:00. (Solapa).

        if (occupiedSeats + personas > local.getCapacidad()) {
            throw new RuntimeException("No hay disponibilidad para " + personas
                    + " personas en este horario. Capacidad restante aproximada: "
                    + (local.getCapacidad() - occupiedSeats));
        }
    }

    public Integer getRemainingCapacity(Long localId, LocalDateTime fechaHora) {
        LocalEntity local = localRepository.findById(localId)
                .orElseThrow(() -> new RuntimeException("Local no encontrado"));

        LocalDateTime rangeStart = fechaHora.minusHours(2);
        LocalDateTime rangeEnd = fechaHora.plusHours(2);
        Integer occupied = reservaRepository.countPeopleInFrame(localId, rangeStart, rangeEnd);

        return Math.max(0, local.getCapacidad() - occupied);
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
        return reservaRepository.findByUsuarioId(userId);
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
}
