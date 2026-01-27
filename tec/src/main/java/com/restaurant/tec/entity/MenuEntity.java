package com.restaurant.tec.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

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
    @Column(length = 1000)
    private String ingredientes;

    // Alérgenos separados por comas (ej: "gluten,lactosa,frutos secos")
    @Column(length = 500)
    private String alergenos;

    // Relación con el local
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "local_id", nullable = false)
    private LocalEntity local;

    // Indica si el menú está disponible
    @Column(nullable = false)
    private Boolean disponible = true;

    // Constructores
    public MenuEntity() {}

    public MenuEntity(String nombre, String descripcion, BigDecimal precio, LocalEntity local) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.local = local;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    public String getIngredientes() { return ingredientes; }
    public void setIngredientes(String ingredientes) { this.ingredientes = ingredientes; }

    public String getAlergenos() { return alergenos; }
    public void setAlergenos(String alergenos) { this.alergenos = alergenos; }

    public LocalEntity getLocal() { return local; }
    public void setLocal(LocalEntity local) { this.local = local; }

    public Boolean getDisponible() { return disponible; }
    public void setDisponible(Boolean disponible) { this.disponible = disponible; }
}
