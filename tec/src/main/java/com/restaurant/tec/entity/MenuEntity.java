package com.restaurant.tec.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;

/**
 * Entidad que representa un menú de restaurante.
 * Incluye estructura de platos (primero, segundo, postre, bebida), precio, ingredientes y alérgenos.
 * Puede estar asociado a un restaurante y programarse en fechas específicas.
 * 
 * @author RestaurantTec Team
 * @version 1.0
 */
@Entity
@Table(name = "menus")
public class MenuEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(length = 1000)
    private String descripcion;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    // Ingredientes separados por comas
    @Column(length = 2000) // Aumentamos tamaño para JSON o lista larga
    private String ingredientes;

    // Alérgenos separados por comas
    @Column(length = 500)
    private String alergenos;

    // --- Estructura de Platos ---
    @Column(columnDefinition = "TEXT")
    private String primerPlato;
    @Column(columnDefinition = "TEXT")
    private String primerPlatoDesc;
    @Column(columnDefinition = "TEXT")
    private String primerPlatoIngredientes;

    @Column(columnDefinition = "TEXT")
    private String segundoPlato;
    @Column(columnDefinition = "TEXT")
    private String segundoPlatoDesc;
    @Column(columnDefinition = "TEXT")
    private String segundoPlatoIngredientes;

    @Column(columnDefinition = "TEXT")
    private String postre;
    @Column(columnDefinition = "TEXT")
    private String postreDesc;
    @Column(columnDefinition = "TEXT")
    private String postreIngredientes;

    // Relación con el local (Opcional para menús generales)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "local_id", nullable = true)
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private LocalEntity local;

    // Propietario del menú (para no perderlo si se borra el local)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "propietario_id")
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private UserEntity propietario;

    // Indica si el menú está disponible
    @Column(nullable = false)
    private boolean disponible = true;

    // Valoración media (Calculada, no persistida)
    @Transient
    private Double valoracionMedia;

    public Double getValoracionMedia() {
        return valoracionMedia;
    }

    public void setValoracionMedia(Double valoracionMedia) {
        this.valoracionMedia = valoracionMedia;
    }

    // Constructores
    public MenuEntity() {
    }

    public MenuEntity(String nombre, String descripcion, BigDecimal precio, LocalEntity local) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.local = local;
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

    public String getIngredientes() {
        return ingredientes;
    }

    public void setIngredientes(String ingredientes) {
        this.ingredientes = ingredientes;
    }

    public String getAlergenos() {
        return alergenos;
    }

    public void setAlergenos(String alergenos) {
        this.alergenos = alergenos;
    }

    public LocalEntity getLocal() {
        return local;
    }

    public void setLocal(LocalEntity local) {
        this.local = local;
    }

    public UserEntity getPropietario() {
        return propietario;
    }

    public void setPropietario(UserEntity propietario) {
        this.propietario = propietario;
    }

    public Boolean getDisponible() {
        return disponible;
    }

    public void setDisponible(Boolean disponible) {
        this.disponible = disponible;
    }

    // Getters y Setters para Platos
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
}
