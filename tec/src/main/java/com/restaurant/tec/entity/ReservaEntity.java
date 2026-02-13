package com.restaurant.tec.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad que representa una reserva en un restaurante.
 * Soporta reservas de usuarios registrados y de invitados (sin cuenta).
 * Incluye integración con pagos de Stripe y confirmación de asistencia.
 * 
 * @author RestaurantTec Team
 * @version 1.0
 */
@Entity
@Table(name = "reservas")
public class ReservaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con el usuario que hace la reserva (nullable para invitados)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = true)
    private UserEntity usuario;

    // Campos para reservas de invitados (sin cuenta)
    @Column(name = "nombre_invitado")
    private String nombreInvitado;

    @Column(name = "email_invitado")
    private String emailInvitado;

    @Column(name = "telefono_invitado")
    private String telefonoInvitado;

    @Column(name = "stripe_payment_intent_id")
    private String stripePaymentIntentId;

    @Column(name = "estado_pago")
    private String estadoPago; // PENDIENTE, PAGADO

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

    @Column(name = "asistencia_confirmada", nullable = false)
    private Boolean asistenciaConfirmada = false;

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
    public ReservaEntity() {
    }

    public ReservaEntity(UserEntity usuario, LocalEntity local, LocalDateTime fechaHora, Integer numeroPersonas) {
        this.usuario = usuario;
        this.local = local;
        this.fechaHora = fechaHora;
        this.numeroPersonas = numeroPersonas;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserEntity getUsuario() {
        return usuario;
    }

    public void setUsuario(UserEntity usuario) {
        this.usuario = usuario;
    }

    public LocalEntity getLocal() {
        return local;
    }

    public void setLocal(LocalEntity local) {
        this.local = local;
    }

    public MenuEntity getMenu() {
        return menu;
    }

    public void setMenu(MenuEntity menu) {
        this.menu = menu;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public Integer getNumeroPersonas() {
        return numeroPersonas;
    }

    public void setNumeroPersonas(Integer numeroPersonas) {
        this.numeroPersonas = numeroPersonas;
    }

    public EstadoReserva getEstado() {
        return estado;
    }

    public void setEstado(EstadoReserva estado) {
        this.estado = estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Boolean getAsistenciaConfirmada() {
        return asistenciaConfirmada;
    }

    public void setAsistenciaConfirmada(Boolean asistenciaConfirmada) {
        this.asistenciaConfirmada = asistenciaConfirmada;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    // Getters y Setters para campos de invitado
    public String getNombreInvitado() {
        return nombreInvitado;
    }

    public void setNombreInvitado(String nombreInvitado) {
        this.nombreInvitado = nombreInvitado;
    }

    public String getEmailInvitado() {
        return emailInvitado;
    }

    public void setEmailInvitado(String emailInvitado) {
        this.emailInvitado = emailInvitado;
    }

    public String getTelefonoInvitado() {
        return telefonoInvitado;
    }

    public void setTelefonoInvitado(String telefonoInvitado) {
        this.telefonoInvitado = telefonoInvitado;
    }

    public String getStripePaymentIntentId() {
        return stripePaymentIntentId;
    }

    public void setStripePaymentIntentId(String stripePaymentIntentId) {
        this.stripePaymentIntentId = stripePaymentIntentId;
    }

    public String getEstadoPago() {
        return estadoPago;
    }

    public void setEstadoPago(String estadoPago) {
        this.estadoPago = estadoPago;
    }

    // Helper para verificar si es reserva de invitado
    public boolean isGuestReservation() {
        return usuario == null && emailInvitado != null;
    }

    // Enum para estados de reserva
    public enum EstadoReserva {
        PENDIENTE,
        CONFIRMADA,
        CANCELADA,
        COMPLETADA,
        ASISTIDO,
        NO_ASISTIDO
    }
}
