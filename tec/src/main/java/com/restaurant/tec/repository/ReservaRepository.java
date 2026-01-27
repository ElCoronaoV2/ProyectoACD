package com.restaurant.tec.repository;

import com.restaurant.tec.entity.LocalEntity;
import com.restaurant.tec.entity.ReservaEntity;
import com.restaurant.tec.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<ReservaEntity, Long> {
    
    // Buscar reservas por usuario
    List<ReservaEntity> findByUsuario(UserEntity usuario);
    
    // Buscar reservas por usuario ID
    List<ReservaEntity> findByUsuarioId(Long usuarioId);
    
    // Buscar reservas por local
    List<ReservaEntity> findByLocal(LocalEntity local);
    
    // Buscar reservas por local ID
    List<ReservaEntity> findByLocalId(Long localId);
    
    // Buscar reservas por estado
    List<ReservaEntity> findByEstado(ReservaEntity.EstadoReserva estado);
    
    // Buscar reservas por local y estado
    List<ReservaEntity> findByLocalIdAndEstado(Long localId, ReservaEntity.EstadoReserva estado);
    
    // Buscar reservas por usuario y estado
    List<ReservaEntity> findByUsuarioIdAndEstado(Long usuarioId, ReservaEntity.EstadoReserva estado);
    
    // Buscar reservas en un rango de fechas para un local
    @Query("SELECT r FROM ReservaEntity r WHERE r.local.id = :localId AND r.fechaHora BETWEEN :fechaInicio AND :fechaFin")
    List<ReservaEntity> findReservasByLocalAndFechaRange(
        @Param("localId") Long localId,
        @Param("fechaInicio") LocalDateTime fechaInicio,
        @Param("fechaFin") LocalDateTime fechaFin
    );
    
    // Contar reservas confirmadas para un local en una fecha específica
    @Query("SELECT COUNT(r) FROM ReservaEntity r WHERE r.local.id = :localId AND r.fechaHora BETWEEN :fechaInicio AND :fechaFin AND r.estado = 'CONFIRMADA'")
    Long countReservasConfirmadasByLocalAndFecha(
        @Param("localId") Long localId,
        @Param("fechaInicio") LocalDateTime fechaInicio,
        @Param("fechaFin") LocalDateTime fechaFin
    );
}
