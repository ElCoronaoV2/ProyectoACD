package com.restaurant.tec.dto;

import java.time.LocalDate;

public class ScheduleMenuRequest {
    private Long menuId;
    private LocalDate fecha;

    // Getters and Setters
    public Long getMenuId() {
        return menuId;
    }

    public void setMenuId(Long menuId) {
        this.menuId = menuId;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }
}
