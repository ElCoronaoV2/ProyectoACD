package com.restaurant.tec.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Entidad que representa un restaurante (local).
 * Incluye información de ubicación, capacidad por turno, horarios y relaciones
 * con propietario.
 * Contiene coordenadas GPS y coordenadas en mapa interactivo.
 * 
 * @author RestaurantTec Team
 * @version 1.0
 */
@Entity
@Table(name = "locales")
public class LocalEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String direccion;

    @Column(nullable = false)
    private Integer capacidad; // Default implementation

    // Capacidad por turno
    private Integer capacidadComida;
    private Integer capacidadCena;

    // Horario en formato "Lun-Vie: 12:00-23:00, Sab-Dom: 13:00-00:00"
    @Column(length = 500)
    private String horario;

    // Horarios de turnos (Comida y Cena)
    private java.time.LocalTime aperturaComida; // e.g. 13:00
    private java.time.LocalTime cierreComida; // e.g. 16:00
    private java.time.LocalTime aperturaCena; // e.g. 20:00
    private java.time.LocalTime cierreCena; // e.g. 23:30

    // Coordenadas GPS para mapa
    private Double latitud;
    private Double longitud;

    // Posición en el mapa estático (porcentaje 0-100)
    // Coordenadas en mapa imagen (0-100%)
    private Double mapaX;
    private Double mapaY;

    // Relación OneToMany con Reviews
    @OneToMany(mappedBy = "local", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<ResenaEntity> reviews = new java.util.ArrayList<>();

    // Valoración media (Calculada o almacenada) -- Removed duplicate
    // private Double valoracion;

    // Métodos helper
    public void addReview(ResenaEntity review) {
        reviews.add(review);
        review.setLocal(this);
        recalculateRating();
    }

    public void recalculateRating() {
        if (reviews.isEmpty()) {
            this.valoracion = 0.0;
        } else {
            double sum = reviews.stream().mapToInt(ResenaEntity::getPuntuacion).sum();
            this.valoracion = Math.round((sum / reviews.size()) * 10.0) / 10.0;
        }
    }

    private Double posX;
    private Double posY;

    // URL de imagen del restaurante
    @Column(length = 500)
    private String imagenUrl;

    // Valoración promedio (1.0 - 5.0)
    @Transient
    private Double valoracion;

    // Propietario (CEO) del restaurante
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "propietario_id")
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private UserEntity propietario;

    @org.hibernate.annotations.Formula("(SELECT count(u.id) FROM usuarios u WHERE u.restaurante_trabajo_id = id AND u.rol = 'EMPLEADO')")
    private Integer numeroEmpleados;

    public Integer getNumeroEmpleados() {
        return numeroEmpleados;
    }

    public void setNumeroEmpleados(Integer numeroEmpleados) {
        this.numeroEmpleados = numeroEmpleados;
    }

    @org.hibernate.annotations.Formula("(SELECT count(r.id) FROM reservas r WHERE r.local_id = id AND r.estado = 'CONFIRMADA')")
    private Integer reservasConfirmadas;

    public Integer getReservasConfirmadas() {
        return reservasConfirmadas;
    }

    public void setReservasConfirmadas(Integer reservasConfirmadas) {
        this.reservasConfirmadas = reservasConfirmadas;
    }

    // Constructores
    public LocalEntity() {
    }

    public LocalEntity(String nombre, String direccion, Integer capacidad) {
        this.nombre = nombre;
        this.direccion = direccion;
        this.capacidad = capacidad;
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

    public Integer getCapacidadComida() {
        return capacidadComida;
    }

    public void setCapacidadComida(Integer capacidadComida) {
        this.capacidadComida = capacidadComida;
    }

    public Integer getCapacidadCena() {
        return capacidadCena;
    }

    public void setCapacidadCena(Integer capacidadCena) {
        this.capacidadCena = capacidadCena;
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

    public UserEntity getPropietario() {
        return propietario;
    }

    public void setPropietario(UserEntity propietario) {
        this.propietario = propietario;
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