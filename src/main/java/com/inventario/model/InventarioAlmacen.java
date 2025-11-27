package com.inventario.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventario_almacen",
        uniqueConstraints = @UniqueConstraint(columnNames = {"producto_id", "almacen_id"}))
public class InventarioAlmacen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "almacen_id", nullable = false)
    private Almacen almacen;

    @Column(nullable = false)
    private Integer cantidad = 0;

    @Column(name = "ubicacion_especifica", length = 100)
    private String ubicacionEspecifica;

    @Column(name = "fecha_ultima_actualizacion")
    private LocalDateTime fechaUltimaActualizacion;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        fechaUltimaActualizacion = LocalDateTime.now();
    }

    // Constructores
    public InventarioAlmacen() {
    }

    public InventarioAlmacen(Producto producto, Almacen almacen, Integer cantidad) {
        this.producto = producto;
        this.almacen = almacen;
        this.cantidad = cantidad;
    }

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Almacen getAlmacen() {
        return almacen;
    }

    public void setAlmacen(Almacen almacen) {
        this.almacen = almacen;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public String getUbicacionEspecifica() {
        return ubicacionEspecifica;
    }

    public void setUbicacionEspecifica(String ubicacionEspecifica) {
        this.ubicacionEspecifica = ubicacionEspecifica;
    }

    public LocalDateTime getFechaUltimaActualizacion() {
        return fechaUltimaActualizacion;
    }

    // Métodos de utilidad
    public void agregarCantidad(Integer cantidad) {
        this.cantidad += cantidad;
    }

    public void restarCantidad(Integer cantidad) {
        this.cantidad = Math.max(0, this.cantidad - cantidad);
    }

    public boolean tieneStock() {
        return cantidad > 0;
    }

    @Override
    public String toString() {
        return "InventarioAlmacen{id=" + id + ", cantidad=" + cantidad
                + ", almacen=" + (almacen != null ? almacen.getNombre() : "null") + "}";
    }
}
