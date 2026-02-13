package com.restaurant.tec.dto;

public class LocalResponse {

    private Long id;
    private String nombre;
    private String direccion;
    private Integer capacidad;
    private String horario;
    private Double latitud;
    private Double longitud;
    private Double posX;
    private Double posY;
    private String imagenUrl;
    private Double valoracion;

    private java.time.LocalTime aperturaComida;
    private java.time.LocalTime cierreComida;
    private java.time.LocalTime aperturaCena;
    private java.time.LocalTime cierreCena;

    // Constructores
    public LocalResponse() {
    }

    public LocalResponse(Long id, String nombre, String direccion, Integer capacidad,
            String horario, Double latitud, Double longitud,
            Double posX, Double posY, String imagenUrl, Double valoracion) {
        this.id = id;
        this.nombre = nombre;
        this.direccion = direccion;
        this.capacidad = capacidad;
        this.horario = horario;
        this.latitud = latitud;
        this.longitud = longitud;
        this.posX = posX;
        this.posY = posY;
        this.imagenUrl = imagenUrl;
        this.valoracion = valoracion;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Integer getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(Integer capacidad) {
        this.capacidad = capacidad;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public Double getPosX() {
        return posX;
    }

    public void setPosX(Double posX) {
        this.posX = posX;
    }

    public Double getPosY() {
        return posY;
    }

    public void setPosY(Double posY) {
        this.posY = posY;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public Double getValoracion() {
        return valoracion;
    }

    public void setValoracion(Double valoracion) {
        this.valoracion = valoracion;
    }

    public java.time.LocalTime getAperturaComida() {
        return aperturaComida;
    }

    public void setAperturaComida(java.time.LocalTime aperturaComida) {
        this.aperturaComida = aperturaComida;
    }

    public java.time.LocalTime getCierreComida() {
        return cierreComida;
    }

    public void setCierreComida(java.time.LocalTime cierreComida) {
        this.cierreComida = cierreComida;
    }

    public java.time.LocalTime getAperturaCena() {
        return aperturaCena;
    }

    public void setAperturaCena(java.time.LocalTime aperturaCena) {
        this.aperturaCena = aperturaCena;
    }

    public java.time.LocalTime getCierreCena() {
        return cierreCena;
    }

    public void setCierreCena(java.time.LocalTime cierreCena) {
        this.cierreCena = cierreCena;
    }
}
