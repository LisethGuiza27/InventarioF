package com.inventario.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "kardex")
public class Kardex {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "almacen_id")
    private Almacen almacen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movimiento_id")
    private Movimiento movimiento;

    @Column(name = "tipo_operacion", nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoOperacion tipoOperacion;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(name = "saldo_anterior", nullable = false)
    private Integer saldoAnterior;

    @Column(name = "saldo_nuevo", nullable = false)
    private Integer saldoNuevo;

    @Column(name = "precio_unitario")
    private Double precioUnitario;

    @Column(name = "valor_total")
    private Double valorTotal;

    @Column(name = "fecha_operacion", nullable = false)
    private LocalDateTime fechaOperacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(length = 500)
    private String observaciones;

    public enum TipoOperacion {
        ENTRADA, SALIDA
    }

    @PrePersist
    protected void onCreate() {
        if (fechaOperacion == null) {
            fechaOperacion = LocalDateTime.now();
        }
    }

    // Constructores
    public Kardex() {
    }

    public Kardex(Producto producto, TipoOperacion tipo, Integer cantidad,
            Integer saldoAnterior, Usuario usuario) {
        this.producto = producto;
        this.tipoOperacion = tipo;
        this.cantidad = cantidad;
        this.saldoAnterior = saldoAnterior;
        this.saldoNuevo = tipo == TipoOperacion.ENTRADA
                ? saldoAnterior + cantidad : saldoAnterior - cantidad;
        this.usuario = usuario;
        this.fechaOperacion = LocalDateTime.now();
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

    public Movimiento getMovimiento() {
        return movimiento;
    }

    public void setMovimiento(Movimiento movimiento) {
        this.movimiento = movimiento;
    }

    public TipoOperacion getTipoOperacion() {
        return tipoOperacion;
    }

    public void setTipoOperacion(TipoOperacion tipoOperacion) {
        this.tipoOperacion = tipoOperacion;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Integer getSaldoAnterior() {
        return saldoAnterior;
    }

    public void setSaldoAnterior(Integer saldoAnterior) {
        this.saldoAnterior = saldoAnterior;
    }

    public Integer getSaldoNuevo() {
        return saldoNuevo;
    }

    public void setSaldoNuevo(Integer saldoNuevo) {
        this.saldoNuevo = saldoNuevo;
    }

    public Double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(Double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public Double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(Double valorTotal) {
        this.valorTotal = valorTotal;
    }

    public LocalDateTime getFechaOperacion() {
        return fechaOperacion;
    }

    public void setFechaOperacion(LocalDateTime fechaOperacion) {
        this.fechaOperacion = fechaOperacion;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    @Override
    public String toString() {
        return "Kardex{id=" + id + ", tipo=" + tipoOperacion + ", cantidad=" + cantidad
                + ", saldoNuevo=" + saldoNuevo + "}";
    }
}
