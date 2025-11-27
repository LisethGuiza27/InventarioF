package com.inventario.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstadisticasDTO {
    // Estadísticas de productos
    private Long totalProductos;
    private Long productosActivos;
    private Long productosBajoStock;
    private Long productosAgotados;
    private Long productosProximosVencer;
    
    // Estadísticas de inventario
    private Double valorTotalInventario;
    private Double valorInventarioCompra;
    private Double utilidadPotencial;
    private Double margenPromedioGanancia;
    
    // Estadísticas de movimientos
    private Long movimientosHoy;
    private Long movimientosMes;
    private Long entradasHoy;
    private Long salidasHoy;
    
    // Porcentajes y ratios
    private Double porcentajeStockBajo;
    private Double porcentajeAgotados;
    private Double tasaRotacionPromedio;
    
    // Alertas
    private Long alertasActivas;
    private Long alertasCriticas;
    
    // Financiero
    private Double ventasTotalesMes;
    private Double comprasTotalesMes;
    private Double diferenciaMes;
    
    // Almacenamiento
    private Double capacidadTotalAlmacenes;
    private Double capacidadUtilizada;
    private Double porcentajeCapacidad;
    
    // Top productos
    private String productoMasVendido;
    private String productoMenosRotacion;
    private String categoriaConMasStock;
    
    // Proveedores y clientes
    private Long totalProveedores;
    private Long totalClientes;
    private Long clientesActivos;
}