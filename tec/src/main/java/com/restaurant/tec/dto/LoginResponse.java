package com.restaurant.tec.dto;

import com.restaurant.tec.entity.Role;

public class LoginResponse {

    private String token;
    private String type = "Bearer";
    private Long id;
    private String email;
    private String nombre;
    private Role rol;
    private String alergenos;

    // Constructores
    public LoginResponse() {
    }

    public LoginResponse(String token, Long id, String email, String nombre, Role rol, String alergenos) {
        this.token = token;
        this.id = id;
        this.email = email;
        this.nombre = nombre;
        this.rol = rol;
        this.alergenos = alergenos;
    }

    // Getters y Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

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

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Role getRol() {
        return rol;
    }

    public void setRol(Role rol) {
        this.rol = rol;
    }

    public String getAlergenos() {
        return alergenos;
    }

    public void setAlergenos(String alergenos) {
        this.alergenos = alergenos;
    }
}
