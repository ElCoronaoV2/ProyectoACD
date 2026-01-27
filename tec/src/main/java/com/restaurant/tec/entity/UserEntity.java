package com.restaurant.tec.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

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

    // Roles: 'ADMIN' para el dueño, 'USER' para clientes
    @Column(nullable = false)
    private String rol = "USER";

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
    public UserEntity() {}

    public UserEntity(String email, String password, String nombre) {
        this.email = email;
        this.password = password;
        this.nombre = nombre;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    
    public String getAlergenos() { return alergenos; }
    public void setAlergenos(String alergenos) { this.alergenos = alergenos; }
    
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
    
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    
    public LocalDateTime getUltimoAcceso() { return ultimoAcceso; }
    public void setUltimoAcceso(LocalDateTime ultimoAcceso) { this.ultimoAcceso = ultimoAcceso; }
}