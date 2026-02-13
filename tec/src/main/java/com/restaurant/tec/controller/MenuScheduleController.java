package com.restaurant.tec.controller;

import com.restaurant.tec.dto.ScheduleMenuRequest;
import com.restaurant.tec.entity.MenuScheduleEntity;
import com.restaurant.tec.service.MenuScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/locales/{localId}/schedule")
public class MenuScheduleController {

    @Autowired
    private MenuScheduleService scheduleService;

    // Programar un menú para una fecha
    @PostMapping
    public ResponseEntity<?> scheduleMenu(
            @PathVariable Long localId,
            @RequestBody ScheduleMenuRequest request) {
        try {
            MenuScheduleEntity schedule = scheduleService.scheduleMenu(
                    localId, request.getMenuId(), request.getFecha());

            Map<String, Object> response = new HashMap<>();
            response.put("id", schedule.getId());
            response.put("menuId", schedule.getMenu().getId());
            response.put("menuNombre", schedule.getMenu().getNombre());
            response.put("fecha", schedule.getFecha());
            response.put("message", "Menú programado correctamente");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Obtener programación del mes (para calendario)
    @GetMapping
    public ResponseEntity<?> getSchedule(
            @PathVariable Long localId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {

        List<MenuScheduleEntity> schedules;

        if (year != null && month != null) {
            schedules = scheduleService.getScheduleForMonth(localId, year, month);
        } else {
            schedules = scheduleService.getScheduleForLocal(localId);
        }

        List<Map<String, Object>> result = schedules.stream().map(s -> {
            Map<String, Object> item = new HashMap<>();
            item.put("id", s.getId());
            item.put("fecha", s.getFecha());
            item.put("menuId", s.getMenu().getId());
            item.put("menuNombre", s.getMenu().getNombre());
            return item;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    // Eliminar programación de una fecha
    @DeleteMapping("/{fecha}")
    public ResponseEntity<?> deleteSchedule(
            @PathVariable Long localId,
            @PathVariable String fecha) {
        try {
            LocalDate date = LocalDate.parse(fecha);
            scheduleService.deleteSchedule(localId, date);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Programación eliminada");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
