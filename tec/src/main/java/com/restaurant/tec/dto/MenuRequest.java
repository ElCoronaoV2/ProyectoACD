package com.restaurant.tec.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class MenuRequest {
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private Boolean disponible;
    private String alergenos;

    private String primerPlato;
    private String primerPlatoDesc;
    private String primerPlatoIngredientes;

    private String segundoPlato;
    private String segundoPlatoDesc;
    private String segundoPlatoIngredientes;

    private String postre;
    private String postreDesc;
    private String postreIngredientes;

    @NotNull(message = "El ID del local es obligatorio")
    private Long localId;

    // Getters y Setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public Boolean getDisponible() {
        return disponible;
    }

    public void setDisponible(Boolean disponible) {
        this.disponible = disponible;
    }

    public String getAlergenos() {
        return alergenos;
    }

    public void setAlergenos(String alergenos) {
        this.alergenos = alergenos;
    }

    public String getPrimerPlato() {
        return primerPlato;
    }

    public void setPrimerPlato(String primerPlato) {
        this.primerPlato = primerPlato;
    }

    public String getPrimerPlatoDesc() {
        return primerPlatoDesc;
    }

    public void setPrimerPlatoDesc(String primerPlatoDesc) {
        this.primerPlatoDesc = primerPlatoDesc;
    }

    public String getPrimerPlatoIngredientes() {
        return primerPlatoIngredientes;
    }

    public void setPrimerPlatoIngredientes(String primerPlatoIngredientes) {
        this.primerPlatoIngredientes = primerPlatoIngredientes;
    }

    public String getSegundoPlato() {
        return segundoPlato;
    }

    public void setSegundoPlato(String segundoPlato) {
        this.segundoPlato = segundoPlato;
    }

    public String getSegundoPlatoDesc() {
        return segundoPlatoDesc;
    }

    public void setSegundoPlatoDesc(String segundoPlatoDesc) {
        this.segundoPlatoDesc = segundoPlatoDesc;
    }

    public String getSegundoPlatoIngredientes() {
        return segundoPlatoIngredientes;
    }

    public void setSegundoPlatoIngredientes(String segundoPlatoIngredientes) {
        this.segundoPlatoIngredientes = segundoPlatoIngredientes;
    }

    public String getPostre() {
        return postre;
    }

    public void setPostre(String postre) {
        this.postre = postre;
    }

    public String getPostreDesc() {
        return postreDesc;
    }

    public void setPostreDesc(String postreDesc) {
        this.postreDesc = postreDesc;
    }

    public String getPostreIngredientes() {
        return postreIngredientes;
    }

    public void setPostreIngredientes(String postreIngredientes) {
        this.postreIngredientes = postreIngredientes;
    }

    public Long getLocalId() {
        return localId;
    }

    public void setLocalId(Long localId) {
        this.localId = localId;
    }
}
