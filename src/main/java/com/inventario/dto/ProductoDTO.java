package com.inventario.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoDTO {
    private Integer id;
    private String codigo;
    private String nombre;
    private String descripcion;
    private String categoriaNombre;
    private Integer categoriaId;
    private String proveedorNombre;
    private Integer proveedorId;
    private String codigoBarras;
    private String sku;
    private String unidadMedida;
    private Double precioVenta;
    private Double precioCompra;
    private Double precioMayoreo;
    private Integer stockActual;
    private Integer stockMinimo;
    private Integer stockMaximo;
    private Integer puntoReorden;
    private String lote;
    private String numeroSerie;
    private LocalDate fechaVencimiento;
    private Boolean esPerecedero;
    private Boolean requiereLote;
    private Boolean requiereSerie;
    private String marca;
    private String modelo;
    private String color;
    private String talla;
    private Double peso;
    private Double volumen;
    private String imagenUrl;
    private Boolean tieneImpuestos;
    private Double porcentajeImpuesto;
    private Double costoAlmacenamiento;
    private String observaciones;
    private Boolean activo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Campos calculados
    private Double valorStock;
    private Double margenGanancia;
    private String estadoStock; // "NORMAL", "BAJO", "AGOTADO"
    private Integer diasParaVencer;
    private Boolean requiereReabastecimiento;
    
    // Constructor desde entidad
    public static ProductoDTO fromEntity(com.inventario.model.Producto producto) {
        ProductoDTO dto = new ProductoDTO();
        dto.setId(producto.getId());
        dto.setCodigo(producto.getCodigo());
        dto.setNombre(producto.getNombre());
        dto.setDescripcion(producto.getDescripcion());
        
        if (producto.getCategoria() != null) {
            dto.setCategoriaNombre(producto.getCategoria().getNombre());
            dto.setCategoriaId(producto.getCategoria().getId());
        }
        
        if (producto.getProveedor() != null) {
            dto.setProveedorNombre(producto.getProveedor().getNombre());
            dto.setProveedorId(producto.getProveedor().getId());
        }
        
        dto.setCodigoBarras(producto.getCodigoBarras());
        dto.setSku(producto.getSku());
        dto.setUnidadMedida(producto.getUnidadMedida());
        dto.setPrecioVenta(producto.getPrecioVenta());
        dto.setPrecioCompra(producto.getPrecioCompra());
        dto.setPrecioMayoreo(producto.getPrecioMayoreo());
        dto.setStockActual(producto.getStockActual());
        dto.setStockMinimo(producto.getStockMinimo());
        dto.setStockMaximo(producto.getStockMaximo());
        dto.setPuntoReorden(producto.getPuntoReorden());
        dto.setLote(producto.getLote());
        dto.setNumeroSerie(producto.getNumeroSerie());
        dto.setFechaVencimiento(producto.getFechaVencimiento());
        dto.setEsPerecedero(producto.getEsPerecedero());
        dto.setRequiereLote(producto.getRequiereLote());
        dto.setRequiereSerie(producto.getRequiereSerie());
        dto.setMarca(producto.getMarca());
        dto.setModelo(producto.getModelo());
        dto.setColor(producto.getColor());
        dto.setTalla(producto.getTalla());
        dto.setPeso(producto.getPeso());
        dto.setVolumen(producto.getVolumen());
        dto.setImagenUrl(producto.getImagenUrl());
        dto.setTieneImpuestos(producto.getTieneImpuestos());
        dto.setPorcentajeImpuesto(producto.getPorcentajeImpuesto());
        dto.setCostoAlmacenamiento(producto.getCostoAlmacenamiento());
        dto.setObservaciones(producto.getObservaciones());
        dto.setActivo(producto.getActivo());
        dto.setCreatedAt(producto.getCreatedAt());
        dto.setUpdatedAt(producto.getUpdatedAt());
        
        // Calcular campos adicionales
        dto.setValorStock(producto.calcularValorStock());
        dto.setMargenGanancia(producto.calcularMargen());
        dto.setEstadoStock(determinarEstadoStock(producto));
        
        if (producto.getFechaVencimiento() != null) {
            dto.setDiasParaVencer(
                (int) java.time.temporal.ChronoUnit.DAYS.between(
                    LocalDate.now(), 
                    producto.getFechaVencimiento()
                )
            );
        }
        
        dto.setRequiereReabastecimiento(producto.necesitaReabastecimiento());
        
        return dto;
    }
    
    private static String determinarEstadoStock(com.inventario.model.Producto producto) {
        if (producto.estaAgotado()) return "AGOTADO";
        if (producto.esStockBajo()) return "BAJO";
        return "NORMAL";
    }
}