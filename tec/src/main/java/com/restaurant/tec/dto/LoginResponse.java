package com.restaurant.tec.dto;

public class LoginResponse {

    private String token;
    private String type = "Bearer";
    private String email;
    private String nombre;
    private String rol;

    // Constructores
    public LoginResponse() {}

    public LoginResponse(String token, String email, String nombre, String rol) {
        this.token = token;
        this.email = email;
        this.nombre = nombre;
        this.rol = rol;
    }

    // Getters y Setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}
