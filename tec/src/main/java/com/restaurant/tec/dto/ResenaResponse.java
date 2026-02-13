package com.restaurant.tec.dto;

import java.time.LocalDateTime;

public class ResenaResponse {
    private Long id;
    private String usuarioNombre;
    private Integer puntuacion;
    private String comentario;
    private LocalDateTime fecha;

    public ResenaResponse(Long id, String usuarioNombre, Integer puntuacion, String comentario, LocalDateTime fecha) {
        this.id = id;
        this.usuarioNombre = usuarioNombre;
        this.puntuacion = puntuacion;
        this.comentario = comentario;
        this.fecha = fecha;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getUsuarioNombre() {
        return usuarioNombre;
    }

    public Integer getPuntuacion() {
        return puntuacion;
    }

    public String getComentario() {
        return comentario;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }
}
