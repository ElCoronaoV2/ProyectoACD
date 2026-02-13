package com.restaurant.tec.service;

import com.restaurant.tec.entity.LocalEntity;
import com.restaurant.tec.entity.MenuEntity;
import com.restaurant.tec.entity.MenuScheduleEntity;
import com.restaurant.tec.repository.LocalRepository;
import com.restaurant.tec.repository.MenuRepository;
import com.restaurant.tec.repository.MenuScheduleRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class MenuScheduleService {

    @Autowired
    private MenuScheduleRepository scheduleRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private LocalRepository localRepository;

    /**
     * Programa un menú para una fecha específica en un local.
     * Si ya existe una programación para esa fecha, la reemplaza.
     */
    @Transactional
    public MenuScheduleEntity scheduleMenu(Long localId, Long menuId, LocalDate fecha) {
        LocalEntity local = localRepository.findById(localId)
                .orElseThrow(() -> new RuntimeException("Local no encontrado"));

        MenuEntity menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new RuntimeException("Menú no encontrado"));

        // Verificar que el menú pertenece al local
        if (!menu.getLocal().getId().equals(localId)) {
            throw new RuntimeException("El menú no pertenece a este local");
        }

        // Eliminar programación existente para esta fecha (si existe)
        scheduleRepository.deleteByLocalIdAndFecha(localId, fecha);

        // Crear nueva programación
        MenuScheduleEntity schedule = new MenuScheduleEntity(menu, local, fecha);
        return scheduleRepository.save(schedule);
    }

    /**
     * Obtiene el menú programado para hoy en un local.
     */
    @Transactional(readOnly = true)
    public Optional<MenuEntity> getMenuDelDia(Long localId) {
        return getMenuForDate(localId, LocalDate.now());
    }

    /**
     * Obtiene el menú programado para una fecha específica.
     */
    @Transactional(readOnly = true)
    public Optional<MenuEntity> getMenuForDate(Long localId, LocalDate fecha) {
        return scheduleRepository.findByLocalIdAndFecha(localId, fecha)
                .map(schedule -> {
                    MenuEntity menu = schedule.getMenu();
                    // Inicializar los proxies lazy dentro de la transacción con Hibernate.initialize()
                    Hibernate.initialize(menu.getLocal());
                    Hibernate.initialize(menu.getPropietario());
                    return menu;
                });
    }

    /**
     * Obtiene todas las programaciones de un local (para el calendario).
     */
    public List<MenuScheduleEntity> getScheduleForLocal(Long localId) {
        return scheduleRepository.findByLocalIdOrderByFechaAsc(localId);
    }

    /**
     * Obtiene programaciones para un rango de fechas (ej: este mes).
     */
    public List<MenuScheduleEntity> getScheduleForMonth(Long localId, int year, int month) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.plusMonths(1).minusDays(1);
        return scheduleRepository.findByLocalIdAndFechaBetween(localId, start, end);
    }

    /**
     * Elimina una programación.
     */
    @Transactional
    public void deleteSchedule(Long localId, LocalDate fecha) {
        scheduleRepository.deleteByLocalIdAndFecha(localId, fecha);
    }
}
