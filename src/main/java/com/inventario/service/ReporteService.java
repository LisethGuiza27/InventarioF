package com.inventario.service;

import com.inventario.model.*;
import com.inventario.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ReporteService {

    @Autowired
    private ProductoRepository productoRepo;
    
    @Autowired
    private MovimientoRepository movimientoRepo;
    
    @Autowired
    private KardexRepository kardexRepo;
    
    @Autowired
    private CategoriaRepository categoriaRepo;

    // Reporte de inventario general
    public Map<String, Object> reporteInventario() {
        Map<String, Object> reporte = new HashMap<>();
        
        List<Producto> productos = productoRepo.findByActivoTrue();
        
        reporte.put("totalProductos", productos.size());
        reporte.put("valorTotal", productos.stream()
                .mapToDouble(p -> p.getStockActual() * p.getPrecioVenta())
                .sum());
        reporte.put("stockTotal", productos.stream()
                .mapToInt(Producto::getStockActual)
                .sum());
        reporte.put("productos", productos);
        
        // Por categoría
        Map<String, Long> porCategoria = productos.stream()
                .filter(p -> p.getCategoria() != null)
                .collect(Collectors.groupingBy(
                    p -> p.getCategoria().getNombre(),
                    Collectors.counting()
                ));
        reporte.put("porCategoria", porCategoria);
        
        return reporte;
    }

    // Reporte de movimientos por rango de fechas
    public Map<String, Object> reporteMovimientos(LocalDate desde, LocalDate hasta) {
        Map<String, Object> reporte = new HashMap<>();
        
        LocalDateTime inicioFecha = desde.atStartOfDay();
        LocalDateTime finFecha = hasta.atTime(23, 59, 59);
        
        List<Movimiento> movimientos = movimientoRepo.findByFechaBetween(inicioFecha, finFecha);
        
        reporte.put("totalMovimientos", movimientos.size());
        reporte.put("movimientos", movimientos);
        
        // Por tipo
        Map<String, Long> porTipo = movimientos.stream()
                .collect(Collectors.groupingBy(
                    m -> m.getTipo().name(),
                    Collectors.counting()
                ));
        reporte.put("porTipo", porTipo);
        
        // Valor total
        double valorTotal = movimientos.stream()
                .mapToDouble(Movimiento::getValorTotal)
                .sum();
        reporte.put("valorTotal", valorTotal);
        
        // Entradas vs Salidas
        double totalEntradas = movimientos.stream()
                .filter(m -> m.getTipo() == Movimiento.TipoMovimiento.ENTRADA)
                .mapToDouble(Movimiento::getValorTotal)
                .sum();
        
        double totalSalidas = movimientos.stream()
                .filter(m -> m.getTipo() == Movimiento.TipoMovimiento.SALIDA)
                .mapToDouble(Movimiento::getValorTotal)
                .sum();
        
        reporte.put("totalEntradas", totalEntradas);
        reporte.put("totalSalidas", totalSalidas);
        reporte.put("diferencia", totalEntradas - totalSalidas);
        
        return reporte;
    }

    // Reporte de productos con stock bajo
    public List<Producto> reporteStockBajo() {
        return productoRepo.findProductosStockBajo();
    }

    // Reporte de valoración de inventario
    public Map<String, Object> reporteValoracion() {
        Map<String, Object> reporte = new HashMap<>();
        
        List<Producto> productos = productoRepo.findByActivoTrue();
        
        // Valor a precio de venta
        double valorVenta = productos.stream()
                .mapToDouble(p -> p.getStockActual() * p.getPrecioVenta())
                .sum();
        
        // Valor a precio de compra
        double valorCompra = productos.stream()
                .filter(p -> p.getPrecioCompra() != null)
                .mapToDouble(p -> p.getStockActual() * p.getPrecioCompra())
                .sum();
        
        // Utilidad potencial
        double utilidadPotencial = valorVenta - valorCompra;
        double margenPromedio = valorCompra > 0 ? (utilidadPotencial / valorCompra) * 100 : 0;
        
        reporte.put("valorVenta", valorVenta);
        reporte.put("valorCompra", valorCompra);
        reporte.put("utilidadPotencial", utilidadPotencial);
        reporte.put("margenPromedio", margenPromedio);
        reporte.put("totalProductos", productos.size());
        
        // Top 10 productos más valiosos
        List<Map<String, Object>> topProductos = productos.stream()
                .map(p -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("producto", p);
                    item.put("valor", p.getStockActual() * p.getPrecioVenta());
                    return item;
                })
                .sorted((a, b) -> Double.compare((Double)b.get("valor"), (Double)a.get("valor")))
                .limit(10)
                .collect(Collectors.toList());
        
        reporte.put("topProductos", topProductos);
        
        return reporte;
    }

    // Kardex de un producto específico
    public List<Kardex> kardexProducto(Integer productoId) {
        return kardexRepo.findByProductoIdOrderByFechaDesc(productoId);
    }

    // Reporte de productos próximos a vencer
    public List<Producto> reporteProductosProximosVencer(int dias) {
        LocalDate fechaLimite = LocalDate.now().plusDays(dias);
        return productoRepo.findProductosProximosVencer(fechaLimite);
    }

    // Reporte de productos más vendidos (top N)
    public List<Map<String, Object>> reporteProductosMasVendidos(int top) {
        // Obtener todas las salidas
        List<Movimiento> salidas = movimientoRepo.findByTipo(Movimiento.TipoMovimiento.SALIDA);
        
        // Agrupar por producto y sumar cantidades
        Map<Producto, Integer> ventasPorProducto = new HashMap<>();
        
        for (Movimiento mov : salidas) {
            for (MovimientoDetalle detalle : mov.getDetalles()) {
                Producto producto = detalle.getProducto();
                ventasPorProducto.put(producto, 
                    ventasPorProducto.getOrDefault(producto, 0) + detalle.getCantidad());
            }
        }
        
        // Ordenar y tomar top N
        return ventasPorProducto.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(top)
                .map(entry -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("producto", entry.getKey());
                    item.put("cantidadVendida", entry.getValue());
                    item.put("valorTotal", entry.getValue() * entry.getKey().getPrecioVenta());
                    return item;
                })
                .collect(Collectors.toList());
    }

    // Reporte de rotación de inventario
    public Map<String, Object> reporteRotacion(LocalDate desde, LocalDate hasta) {
        Map<String, Object> reporte = new HashMap<>();
        
        List<Producto> productos = productoRepo.findByActivoTrue();
        List<Map<String, Object>> rotacion = new ArrayList<>();
        
        LocalDateTime inicioFecha = desde.atStartOfDay();
        LocalDateTime finFecha = hasta.atTime(23, 59, 59);
        
        for (Producto producto : productos) {
            List<Kardex> movimientos = kardexRepo.findByProductoAndFecha(
                producto.getId(), inicioFecha, finFecha);
            
            int totalSalidas = movimientos.stream()
                    .filter(k -> k.getTipoOperacion() == Kardex.TipoOperacion.SALIDA)
                    .mapToInt(Kardex::getCantidad)
                    .sum();
            
            int stockPromedio = producto.getStockActual();
            
            Map<String, Object> item = new HashMap<>();
            item.put("producto", producto);
            item.put("totalSalidas", totalSalidas);
            item.put("stockPromedio", stockPromedio);
            item.put("rotacion", stockPromedio > 0 ? (double)totalSalidas / stockPromedio : 0);
            
            rotacion.add(item);
        }
        
        // Ordenar por rotación
        rotacion.sort((a, b) -> 
            Double.compare((Double)b.get("rotacion"), (Double)a.get("rotacion")));
        
        reporte.put("productos", rotacion);
        reporte.put("desde", desde);
        reporte.put("hasta", hasta);
        
        return reporte;
    }
}