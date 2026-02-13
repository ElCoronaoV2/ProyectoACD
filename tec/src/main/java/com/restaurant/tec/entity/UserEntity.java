package com.restaurant.tec.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad que representa un usuario del sistema.
 * Incluye información de autenticación, datos personales, alérgenos y roles.
 * Soporta múltiples roles: USER, CEO, DIRECTOR, EMPLEADO, ADMIN.
 * 
 * @author RestaurantTec Team
 * @version 1.0
 */
@Entity
@Table(name = "usuarios")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nombre;

    private String telefono;

    // Alérgenos separados por comas (ej: "gluten,lactosa,frutos secos")
    @Column(length = 500)
    private String alergenos;

    // Roles: DIRECTOR, CEO, EMPLEADO, USER
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role rol = Role.USER;

    // Relaciones para CEO: Un CEO puede tener múltiples restaurantes
    @OneToMany(mappedBy = "propietario", fetch = FetchType.LAZY)
    private java.util.List<LocalEntity> restaurantesPropios;

    // Relaciones para EMPLEADO: Un empleado trabaja en un restaurante
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurante_trabajo_id")
    private LocalEntity restauranteTrabajo;

    @Column(nullable = false)
    private boolean enabled = false;

    // Campos de auditoría
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "ultimo_acceso")
    private LocalDateTime ultimoAcceso;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        ultimoAcceso = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        ultimoAcceso = LocalDateTime.now();
    }

    // Constructores
    public UserEntity() {
    }

    public UserEntity(String email, String password, String nombre) {
        this.email = email;
        this.password = password;
        this.nombre = nombre;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getAlergenos() {
        return alergenos;
    }

    public void setAlergenos(String alergenos) {
        this.alergenos = alergenos;
    }

    public Role getRol() {
        return rol;
    }

    public void setRol(Role rol) {
        this.rol = rol;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getUltimoAcceso() {
        return ultimoAcceso;
    }

    public void setUltimoAcceso(LocalDateTime ultimoAcceso) {
        this.ultimoAcceso = ultimoAcceso;
    }

    @com.fasterxml.jackson.annotation.JsonIgnore
    public java.util.List<LocalEntity> getRestaurantesPropios() {
        return restaurantesPropios;
    }

    public void setRestaurantesPropios(java.util.List<LocalEntity> restaurantesPropios) {
        this.restaurantesPropios = restaurantesPropios;
    }

    @com.fasterxml.jackson.annotation.JsonIgnore
    public LocalEntity getRestauranteTrabajo() {
        return restauranteTrabajo;
    }

    public void setRestauranteTrabajo(LocalEntity restauranteTrabajo) {
        this.restauranteTrabajo = restauranteTrabajo;
    }
}