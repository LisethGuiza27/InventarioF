package com.inventario.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "movimientos")
public class Movimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 50)
    private String codigo;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private TipoMovimiento tipo; // ENTRADA, SALIDA, AJUSTE

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private SubtipoMovimiento subtipo; // COMPRA, VENTA, DEVOLUCION, AJUSTE_POSITIVO, etc.

    @Column(name = "fecha_movimiento", nullable = false)
    private LocalDateTime fechaMovimiento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "almacen_id")
    private Almacen almacen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proveedor_id")
    private Proveedor proveedor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @OneToMany(mappedBy = "movimiento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MovimientoDetalle> detalles = new ArrayList<>();

    @Column(name = "numero_documento", length = 50)
    private String numeroDocumento;

    @Column(name = "total_items")
    private Integer totalItems = 0;

    @Column(name = "valor_total")
    private Double valorTotal = 0.0;

    @Column(length = 500)
    private String observaciones;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private EstadoMovimiento estado = EstadoMovimiento.PENDIENTE;

    @Column(name = "fecha_aprobacion")
    private LocalDateTime fechaAprobacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aprobado_por")
    private Usuario aprobadoPor;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (fechaMovimiento == null) {
            fechaMovimiento = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Enums
    public enum TipoMovimiento {
        ENTRADA, SALIDA, AJUSTE, TRANSFERENCIA
    }

    public enum SubtipoMovimiento {
        COMPRA, VENTA, DEVOLUCION_COMPRA, DEVOLUCION_VENTA,
        AJUSTE_POSITIVO, AJUSTE_NEGATIVO, TRANSFERENCIA_ENTRADA,
        TRANSFERENCIA_SALIDA, PRODUCCION, MERMA, DONACION
    }

    public enum EstadoMovimiento {
        PENDIENTE, APROBADO, COMPLETADO, ANULADO
    }

    // Constructores
    public Movimiento() {
    }

    public Movimiento(String codigo, TipoMovimiento tipo, SubtipoMovimiento subtipo, Usuario usuario) {
        this.codigo = codigo;
        this.tipo = tipo;
        this.subtipo = subtipo;
        this.usuario = usuario;
    }

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public TipoMovimiento getTipo() {
        return tipo;
    }

    public void setTipo(TipoMovimiento tipo) {
        this.tipo = tipo;
    }

    public SubtipoMovimiento getSubtipo() {
        return subtipo;
    }

    public void setSubtipo(SubtipoMovimiento subtipo) {
        this.subtipo = subtipo;
    }

    public LocalDateTime getFechaMovimiento() {
        return fechaMovimiento;
    }

    public void setFechaMovimiento(LocalDateTime fechaMovimiento) {
        this.fechaMovimiento = fechaMovimiento;
    }

    public Almacen getAlmacen() {
        return almacen;
    }

    public void setAlmacen(Almacen almacen) {
        this.almacen = almacen;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<MovimientoDetalle> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<MovimientoDetalle> detalles) {
        this.detalles = detalles;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public Integer getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(Integer totalItems) {
        this.totalItems = totalItems;
    }

    public Double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(Double valorTotal) {
        this.valorTotal = valorTotal;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public EstadoMovimiento getEstado() {
        return estado;
    }

    public void setEstado(EstadoMovimiento estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaAprobacion() {
        return fechaAprobacion;
    }

    public void setFechaAprobacion(LocalDateTime fechaAprobacion) {
        this.fechaAprobacion = fechaAprobacion;
    }

    public Usuario getAprobadoPor() {
        return aprobadoPor;
    }

    public void setAprobadoPor(Usuario aprobadoPor) {
        this.aprobadoPor = aprobadoPor;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    // Métodos de utilidad
    public void agregarDetalle(MovimientoDetalle detalle) {
        detalles.add(detalle);
        detalle.setMovimiento(this);
        calcularTotales();
    }

    public void removerDetalle(MovimientoDetalle detalle) {
        detalles.remove(detalle);
        detalle.setMovimiento(null);
        calcularTotales();
    }

    public void calcularTotales() {
        this.totalItems = detalles.stream()
                .mapToInt(MovimientoDetalle::getCantidad)
                .sum();
        this.valorTotal = detalles.stream()
                .mapToDouble(d -> d.getCantidad() * d.getPrecioUnitario())
                .sum();
    }

    public void aprobar(Usuario usuario) {
        this.estado = EstadoMovimiento.APROBADO;
        this.fechaAprobacion = LocalDateTime.now();
        this.aprobadoPor = usuario;
    }

    public void completar() {
        this.estado = EstadoMovimiento.COMPLETADO;
    }

    public void anular() {
        this.estado = EstadoMovimiento.ANULADO;
    }

    public boolean esEntrada() {
        return tipo == TipoMovimiento.ENTRADA;
    }

    public boolean esSalida() {
        return tipo == TipoMovimiento.SALIDA;
    }

    @Override
    public String toString() {
        return "Movimiento{"
                + "id=" + id
                + ", codigo='" + codigo + '\''
                + ", tipo=" + tipo
                + ", subtipo=" + subtipo
                + ", estado=" + estado
                + ", totalItems=" + totalItems
                + ", valorTotal=" + valorTotal
                + '}';
    }
}
