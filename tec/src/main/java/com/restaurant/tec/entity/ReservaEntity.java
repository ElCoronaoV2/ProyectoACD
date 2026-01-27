package com.restaurant.tec.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservas")
public class ReservaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con el usuario que hace la reserva
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private UserEntity usuario;

    // Relación con el local
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "local_id", nullable = false)
    private LocalEntity local;

    // Relación con el menú (opcional)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id")
    private MenuEntity menu;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Column(name = "numero_personas", nullable = false)
    private Integer numeroPersonas;

    // Estados: PENDIENTE, CONFIRMADA, CANCELADA, COMPLETADA
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EstadoReserva estado = EstadoReserva.PENDIENTE;

    @Column(length = 1000)
    private String observaciones;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }

    // Constructores
    public ReservaEntity() {}

    public ReservaEntity(UserEntity usuario, LocalEntity local, LocalDateTime fechaHora, Integer numeroPersonas) {
        this.usuario = usuario;
        this.local = local;
        this.fechaHora = fechaHora;
        this.numeroPersonas = numeroPersonas;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public UserEntity getUsuario() { return usuario; }
    public void setUsuario(UserEntity usuario) { this.usuario = usuario; }

    public LocalEntity getLocal() { return local; }
    public void setLocal(LocalEntity local) { this.local = local; }

    public MenuEntity getMenu() { return menu; }
    public void setMenu(MenuEntity menu) { this.menu = menu; }

    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }

    public Integer getNumeroPersonas() { return numeroPersonas; }
    public void setNumeroPersonas(Integer numeroPersonas) { this.numeroPersonas = numeroPersonas; }

    public EstadoReserva getEstado() { return estado; }
    public void setEstado(EstadoReserva estado) { this.estado = estado; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }

    // Enum para estados de reserva
    public enum EstadoReserva {
        PENDIENTE,
        CONFIRMADA,
        CANCELADA,
        COMPLETADA
    }
}
