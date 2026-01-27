package com.restaurant.tec.dto;

public class LocalResponse {

    private Long id;
    private String nombre;
    private String direccion;
    private Integer capacidad;
    private String horario;
    private Double latitud;
    private Double longitud;

    // Constructores
    public LocalResponse() {}

    public LocalResponse(Long id, String nombre, String direccion, Integer capacidad, 
                        String horario, Double latitud, Double longitud) {
        this.id = id;
        this.nombre = nombre;
        this.direccion = direccion;
        this.capacidad = capacidad;
        this.horario = horario;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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
