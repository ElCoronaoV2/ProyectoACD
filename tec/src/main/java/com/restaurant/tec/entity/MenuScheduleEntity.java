package com.restaurant.tec.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "menu_programacion", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "local_id", "fecha" }) // Solo 1 menú por día por local
})
public class MenuScheduleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    private MenuEntity menu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "local_id", nullable = false)
    private LocalEntity local;

    @Column(nullable = false)
    private LocalDate fecha;

    // Constructors
    public MenuScheduleEntity() {
    }

    public MenuScheduleEntity(MenuEntity menu, LocalEntity local, LocalDate fecha) {
        this.menu = menu;
        this.local = local;
        this.fecha = fecha;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MenuEntity getMenu() {
        return menu;
    }

    public void setMenu(MenuEntity menu) {
        this.menu = menu;
    }

    public LocalEntity getLocal() {
        return local;
    }

    public void setLocal(LocalEntity local) {
        this.local = local;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }
}
