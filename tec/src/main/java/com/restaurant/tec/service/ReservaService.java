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

        ReservaEntity reserva = new ReservaEntity();
        reserva.setUsuario(user);
        reserva.setLocal(local);
        reserva.setFechaHora(fechaHora);
        reserva.setNumeroPersonas(personas);
        reserva.setObservaciones(observaciones);
        reserva.setEstado(ReservaEntity.EstadoReserva.PENDIENTE);

        return reservaRepository.save(reserva);
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
