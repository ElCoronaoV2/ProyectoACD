package com.restaurant.tec.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class CreateLocalRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "La dirección es obligatoria")
    private String direccion;

    @NotNull(message = "La capacidad es obligatoria")
    @Positive(message = "La capacidad debe ser mayor a 0")
    private Integer capacidad;

    private String horario;
    private Double latitud;
    private Double longitud;

    // Constructores
    public CreateLocalRequest() {}

    public CreateLocalRequest(String nombre, String direccion, Integer capacidad) {
        this.nombre = nombre;
        this.direccion = direccion;
        this.capacidad = capacidad;
    }

    // Getters y Setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public Integer getCapacidad() { return capacidad; }
    public void setCapacidad(Integer capacidad) { this.capacidad = capacidad; }

    public String getHorario() { return horario; }
    public void setHorario(String horario) { this.horario = horario; }

    public Double getLatitud() { return latitud; }
    public void setLatitud(Double latitud) { this.latitud = latitud; }

    public Double getLongitud() { return longitud; }
    public void setLongitud(Double longitud) { this.longitud = longitud; }
}
