package com.restaurant.tec.repository;

import com.restaurant.tec.entity.MenuScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MenuScheduleRepository extends JpaRepository<MenuScheduleEntity, Long> {

    // Obtener el menú programado para un local en una fecha específica
    Optional<MenuScheduleEntity> findByLocalIdAndFecha(Long localId, LocalDate fecha);

    // Obtener todas las programaciones de un local (para el calendario)
    List<MenuScheduleEntity> findByLocalIdOrderByFechaAsc(Long localId);

    // Obtener programaciones de un local en un rango de fechas
    List<MenuScheduleEntity> findByLocalIdAndFechaBetween(Long localId, LocalDate startDate, LocalDate endDate);

    // Eliminar programación existente para un local en una fecha
    void deleteByLocalIdAndFecha(Long localId, LocalDate fecha);
}
