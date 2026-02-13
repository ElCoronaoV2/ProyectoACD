package com.restaurant.tec.dto;

import java.time.LocalDateTime;

public class ReservaDTO {
    private Long id;
    private Long usuarioId;
    private String nombreUsuario; // Opcional, si es util
    private Long localId;
    private String nombreLocal;
    private LocalDateTime fechaHora;
    private Integer numeroPersonas;
    private String observaciones;
    private String estado;
    private String estadoPago;
    private String nombreInvitado;
    private String emailInvitado;
    private String telefonoInvitado;
    private boolean asistenciaConfirmada;

    // Constructor
    public ReservaDTO(Long id, Long usuarioId, String nombreUsuario, Long localId, String nombreLocal, 
                      LocalDateTime fechaHora, Integer numeroPersonas, String observaciones, 
                      String estado, String estadoPago, String nombreInvitado, String emailInvitado, 
                      String telefonoInvitado, boolean asistenciaConfirmada) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.nombreUsuario = nombreUsuario;
        this.localId = localId;
        this.nombreLocal = nombreLocal;
        this.fechaHora = fechaHora;
        this.numeroPersonas = numeroPersonas;
        this.observaciones = observaciones;
        this.estado = estado;
        this.estadoPago = estadoPago;
        this.nombreInvitado = nombreInvitado;
        this.emailInvitado = emailInvitado;
        this.telefonoInvitado = telefonoInvitado;
        this.asistenciaConfirmada = asistenciaConfirmada;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

    public Long getLocalId() { return localId; }
    public void setLocalId(Long localId) { this.localId = localId; }

    public String getNombreLocal() { return nombreLocal; }
    public void setNombreLocal(String nombreLocal) { this.nombreLocal = nombreLocal; }

    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }

    public Integer getNumeroPersonas() { return numeroPersonas; }
    public void setNumeroPersonas(Integer numeroPersonas) { this.numeroPersonas = numeroPersonas; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getEstadoPago() { return estadoPago; }
    public void setEstadoPago(String estadoPago) { this.estadoPago = estadoPago; }

    public String getNombreInvitado() { return nombreInvitado; }
    public void setNombreInvitado(String nombreInvitado) { this.nombreInvitado = nombreInvitado; }

    public String getEmailInvitado() { return emailInvitado; }
    public void setEmailInvitado(String emailInvitado) { this.emailInvitado = emailInvitado; }

    public String getTelefonoInvitado() { return telefonoInvitado; }
    public void setTelefonoInvitado(String telefonoInvitado) { this.telefonoInvitado = telefonoInvitado; }

    public boolean isAsistenciaConfirmada() { return asistenciaConfirmada; }
    public void setAsistenciaConfirmada(boolean asistenciaConfirmada) { this.asistenciaConfirmada = asistenciaConfirmada; }
}
