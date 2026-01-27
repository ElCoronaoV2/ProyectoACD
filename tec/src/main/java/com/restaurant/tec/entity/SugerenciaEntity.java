package com.restaurant.tec.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sugerencias")
public class SugerenciaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con el usuario que hace la sugerencia
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private UserEntity usuario;

    @Column(nullable = false, length = 200)
    private String asunto;

    @Column(nullable = false, length = 2000)
    private String mensaje;

    // Estados: PENDIENTE, EN_REVISION, RESUELTA, RECHAZADA
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EstadoSugerencia estado = EstadoSugerencia.PENDIENTE;

    @Column(length = 2000)
    private String respuesta;

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
    public SugerenciaEntity() {}

    public SugerenciaEntity(UserEntity usuario, String asunto, String mensaje) {
        this.usuario = usuario;
        this.asunto = asunto;
        this.mensaje = mensaje;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public UserEntity getUsuario() { return usuario; }
    public void setUsuario(UserEntity usuario) { this.usuario = usuario; }

    public String getAsunto() { return asunto; }
    public void setAsunto(String asunto) { this.asunto = asunto; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public EstadoSugerencia getEstado() { return estado; }
    public void setEstado(EstadoSugerencia estado) { this.estado = estado; }

    public String getRespuesta() { return respuesta; }
    public void setRespuesta(String respuesta) { this.respuesta = respuesta; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }

    // Enum para estados de sugerencia
    public enum EstadoSugerencia {
        PENDIENTE,
        EN_REVISION,
        RESUELTA,
        RECHAZADA
    }
}
