package com.inventario.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "El código es obligatorio")
    @Size(min = 3, max = 50)
    @Column(nullable = false, length = 50, unique = true)
    private String codigo;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 200)
    @Column(nullable = false, length = 200)
    private String nombre;

    @Column(length = 500)
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proveedor_id")
    private Proveedor proveedor;

    @Column(length = 20)
    private String codigoBarras;

    @Column(length = 50)
    private String sku;

    @Column(length = 50)
    private String unidadMedida = "UND";

    @NotNull(message = "El precio de venta es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false)
    @Column(name = "precio_venta", nullable = false)
    private Double precioVenta;

    @Column(name = "precio_compra")
    private Double precioCompra;

    @Column(name = "precio_mayoreo")
    private Double precioMayoreo;

    @NotNull
    @Min(0)
    @Column(name = "stock_actual", nullable = false)
    private Integer stockActual = 0;

    @Column(name = "stock_minimo")
    private Integer stockMinimo = 0;

    @Column(name = "stock_maximo")
    private Integer stockMaximo;

    @Column(name = "punto_reorden")
    private Integer puntoReorden;

    @Column(length = 50)
    private String lote;

    @Column(name = "numero_serie", length = 100)
    private String numeroSerie;

    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;

    @Column(name = "es_perecedero")
    private Boolean esPerecedero = false;

    @Column(name = "requiere_lote")
    private Boolean requiereLote = false;

    @Column(name = "requiere_serie")
    private Boolean requiereSerie = false;

    @Column(length = 20)
    private String marca;

    @Column(length = 20)
    private String modelo;

    @Column(length = 20)
    private String color;

    @Column(length = 50)
    private String talla;

    @Column
    private Double peso;

    @Column
    private Double volumen;

    @Column(name = "imagen_url", length = 500)
    private String imagenUrl;

    @Column(name = "tiene_impuestos")
    private Boolean tieneImpuestos = true;

    @Column(name = "porcentaje_impuesto")
    private Double porcentajeImpuesto = 0.0;

    @Column(name = "costo_almacenamiento")
    private Double costoAlmacenamiento;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Constructores
    public Producto() {
    }

    public Producto(String codigo, String nombre, Double precioVenta) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.precioVenta = precioVenta;
    }

    // Getters y Setters completos
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

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public Double getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(Double precioVenta) {
        this.precioVenta = precioVenta;
    }

    public Double getPrecioCompra() {
        return precioCompra;
    }

    public void setPrecioCompra(Double precioCompra) {
        this.precioCompra = precioCompra;
    }

    public Double getPrecioMayoreo() {
        return precioMayoreo;
    }

    public void setPrecioMayoreo(Double precioMayoreo) {
        this.precioMayoreo = precioMayoreo;
    }

    public Integer getStockActual() {
        return stockActual;
    }

    public void setStockActual(Integer stockActual) {
        this.stockActual = stockActual;
    }

    public Integer getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(Integer stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public Integer getStockMaximo() {
        return stockMaximo;
    }

    public void setStockMaximo(Integer stockMaximo) {
        this.stockMaximo = stockMaximo;
    }

    public Integer getPuntoReorden() {
        return puntoReorden;
    }

    public void setPuntoReorden(Integer puntoReorden) {
        this.puntoReorden = puntoReorden;
    }

    public String getLote() {
        return lote;
    }

    public void setLote(String lote) {
        this.lote = lote;
    }

    public String getNumeroSerie() {
        return numeroSerie;
    }

    public void setNumeroSerie(String numeroSerie) {
        this.numeroSerie = numeroSerie;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public Boolean getEsPerecedero() {
        return esPerecedero;
    }

    public void setEsPerecedero(Boolean esPerecedero) {
        this.esPerecedero = esPerecedero;
    }

    public Boolean getRequiereLote() {
        return requiereLote;
    }

    public void setRequiereLote(Boolean requiereLote) {
        this.requiereLote = requiereLote;
    }

    public Boolean getRequiereSerie() {
        return requiereSerie;
    }

    public void setRequiereSerie(Boolean requiereSerie) {
        this.requiereSerie = requiereSerie;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getTalla() {
        return talla;
    }

    public void setTalla(String talla) {
        this.talla = talla;
    }

    public Double getPeso() {
        return peso;
    }

    public void setPeso(Double peso) {
        this.peso = peso;
    }

    public Double getVolumen() {
        return volumen;
    }

    public void setVolumen(Double volumen) {
        this.volumen = volumen;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public Boolean getTieneImpuestos() {
        return tieneImpuestos;
    }

    public void setTieneImpuestos(Boolean tieneImpuestos) {
        this.tieneImpuestos = tieneImpuestos;
    }

    public Double getPorcentajeImpuesto() {
        return porcentajeImpuesto;
    }

    public void setPorcentajeImpuesto(Double porcentajeImpuesto) {
        this.porcentajeImpuesto = porcentajeImpuesto;
    }

    public Double getCostoAlmacenamiento() {
        return costoAlmacenamiento;
    }

    public void setCostoAlmacenamiento(Double costoAlmacenamiento) {
        this.costoAlmacenamiento = costoAlmacenamiento;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    // Métodos de utilidad
    public boolean tieneStockDisponible() {
        return stockActual != null && stockActual > 0;
    }

    public boolean necesitaReabastecimiento() {
        if (puntoReorden != null) {
            return stockActual <= puntoReorden;
        }
        return stockActual <= stockMinimo;
    }

    public boolean estaAgotado() {
        return stockActual == 0;
    }

    public boolean esStockBajo() {
        return stockActual <= stockMinimo && stockActual > 0;
    }

    public boolean estaProximoVencer(int diasAnticipacion) {
        if (fechaVencimiento == null) {
            return false;
        }
        LocalDate fechaLimite = LocalDate.now().plusDays(diasAnticipacion);
        return !fechaVencimiento.isAfter(fechaLimite);
    }

    public Double calcularMargen() {
        if (precioCompra != null && precioVenta != null && precioCompra > 0) {
            return ((precioVenta - precioCompra) / precioCompra) * 100;
        }
        return 0.0;
    }

    public Double calcularValorStock() {
        return stockActual * precioVenta;
    }

    @Override
    public String toString() {
        return "Producto{id=" + id + ", codigo='" + codigo + "', nombre='" + nombre
                + "', stock=" + stockActual + ", activo=" + activo + "}";
    }
}
