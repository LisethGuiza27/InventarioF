package com.inventario.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "movimiento_detalle")
public class MovimientoDetalle {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movimiento_id", nullable = false)
    private Movimiento movimiento;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;
    
    @Column(nullable = false)
    private Integer cantidad;
    
    @Column(name = "precio_unitario", nullable = false)
    private Double precioUnitario;
    
    @Column(nullable = false)
    private Double subtotal;
    
    @Column(length = 50)
    private String lote;
    
    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;
    
    @Column(length = 500)
    private String observaciones;
    
    @PrePersist
    @PreUpdate
    protected void calcularSubtotal() {
        if (cantidad != null && precioUnitario != null) {
            this.subtotal = cantidad * precioUnitario;
        }
    }
    
    // Constructores
    public MovimientoDetalle() {}
    
    public MovimientoDetalle(Producto producto, Integer cantidad, Double precioUnitario) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = cantidad * precioUnitario;
    }
    
    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public Movimiento getMovimiento() { return movimiento; }
    public void setMovimiento(Movimiento movimiento) { this.movimiento = movimiento; }
    
    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }
    
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { 
        this.cantidad = cantidad;
        calcularSubtotal();
    }
    
    public Double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(Double precioUnitario) { 
        this.precioUnitario = precioUnitario;
        calcularSubtotal();
    }
    
    public Double getSubtotal() { return subtotal; }
    public void setSubtotal(Double subtotal) { this.subtotal = subtotal; }
    
    public String getLote() { return lote; }
    public void setLote(String lote) { this.lote = lote; }
    
    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }
    
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    
    @Override
    public String toString() {
        return "MovimientoDetalle{id=" + id + ", producto=" + producto.getNombre() + 
               ", cantidad=" + cantidad + ", subtotal=" + subtotal + "}";
    }
}