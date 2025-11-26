package com.inventario.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "alertas")
public class Alerta {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoAlerta tipo;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PrioridadAlerta prioridad = PrioridadAlerta.MEDIA;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "almacen_id")
    private Almacen almacen;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String mensaje;
    
    @Column(nullable = false)
    private Boolean leida = false;
    
    @Column(name = "fecha_lectura")
    private LocalDateTime fechaLectura;
    
    @Column(nullable = false)
    private Boolean activa = true;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    public enum TipoAlerta {
        STOCK_BAJO, STOCK_AGOTADO, VENCIMIENTO_PROXIMO, 
        VENCIDO, STOCK_MAXIMO
    }
    
    public enum PrioridadAlerta {
        BAJA, MEDIA, ALTA, CRITICA
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    // Constructores
    public Alerta() {}
    
    public Alerta(TipoAlerta tipo, Producto producto, String mensaje) {
        this.tipo = tipo;
        this.producto = producto;
        this.mensaje = mensaje;
        this.prioridad = determinarPrioridad(tipo);
    }
    
    private PrioridadAlerta determinarPrioridad(TipoAlerta tipo) {
        return switch (tipo) {
            case STOCK_AGOTADO, VENCIDO -> PrioridadAlerta.CRITICA;
            case STOCK_BAJO, VENCIMIENTO_PROXIMO -> PrioridadAlerta.ALTA;
            case STOCK_MAXIMO -> PrioridadAlerta.MEDIA;
        };
    }
    
    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public TipoAlerta getTipo() { return tipo; }
    public void setTipo(TipoAlerta tipo) { this.tipo = tipo; }
    
    public PrioridadAlerta getPrioridad() { return prioridad; }
    public void setPrioridad(PrioridadAlerta prioridad) { this.prioridad = prioridad; }
    
    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }
    
    public Almacen getAlmacen() { return almacen; }
    public void setAlmacen(Almacen almacen) { this.almacen = almacen; }
    
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    
    public Boolean getLeida() { return leida; }
    public void setLeida(Boolean leida) { this.leida = leida; }
    
    public LocalDateTime getFechaLectura() { return fechaLectura; }
    public void setFechaLectura(LocalDateTime fechaLectura) { this.fechaLectura = fechaLectura; }
    
    public Boolean getActiva() { return activa; }
    public void setActiva(Boolean activa) { this.activa = activa; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    
    // Métodos de utilidad
    public void marcarLeida() {
        this.leida = true;
        this.fechaLectura = LocalDateTime.now();
    }
    
    public void desactivar() {
        this.activa = false;
    }
    
    @Override
    public String toString() {
        return "Alerta{id=" + id + ", tipo=" + tipo + ", prioridad=" + prioridad + 
               ", leida=" + leida + "}";
    }
}