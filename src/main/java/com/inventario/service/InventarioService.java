package com.inventario.service;

import com.inventario.model.*;
import com.inventario.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio avanzado de gestión de inventario
 */
@Service
@Transactional
public class InventarioService {

    @Autowired
    private ProductoRepository productoRepo;

    @Autowired
    private InventarioAlmacenRepository inventarioAlmacenRepo;

    @Autowired
    private MovimientoRepository movimientoRepo;

    @Autowired
    private AlertaService alertaService;

    /**
     * Obtiene el estado general del inventario
     */
    public Map<String, Object> obtenerEstadoGeneral() {
        Map<String, Object> estado = new HashMap<>();

        List<Producto> todosProductos = productoRepo.findByActivoTrue();

        estado.put("totalProductos", todosProductos.size());
        estado.put("valorTotalInventario", calcularValorTotal(todosProductos));
        estado.put("productosStockBajo", contarProductosStockBajo(todosProductos));
        estado.put("productosAgotados", contarProductosAgotados(todosProductos));
        estado.put("productosProximosVencer", contarProductosProximosVencer(todosProductos, 30));

        return estado;
    }

    /**
     * Calcula el valor total del inventario
     */
    public double calcularValorTotal(List<Producto> productos) {
        return productos.stream()
                .mapToDouble(p -> p.getStockActual() * p.getPrecioVenta())
                .sum();
    }

    /**
     * Cuenta productos con stock bajo
     */
    public long contarProductosStockBajo(List<Producto> productos) {
        return productos.stream()
                .filter(p -> p.getStockActual() <= p.getStockMinimo() && p.getStockActual() > 0)
                .count();
    }

    /**
     * Cuenta productos agotados
     */
    public long contarProductosAgotados(List<Producto> productos) {
        return productos.stream()
                .filter(p -> p.getStockActual() == 0)
                .count();
    }

    /**
     * Cuenta productos próximos a vencer
     */
    public long contarProductosProximosVencer(List<Producto> productos, int dias) {
        LocalDate fechaLimite = LocalDate.now().plusDays(dias);
        return productos.stream()
                .filter(p -> p.getFechaVencimiento() != null
                && !p.getFechaVencimiento().isAfter(fechaLimite))
                .count();
    }

    /**
     * Obtiene productos que necesitan reabastecimiento
     */
    public List<Producto> obtenerProductosReabastecimiento() {
        return productoRepo.findByActivoTrue().stream()
                .filter(Producto::necesitaReabastecimiento)
                .sorted(Comparator.comparing(Producto::getStockActual))
                .collect(Collectors.toList());
    }

    /**
     * Calcula la rotación de inventario para un producto
     */
    public double calcularRotacionProducto(Integer productoId, LocalDate desde, LocalDate hasta) {
        Producto producto = productoRepo.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        // Obtener salidas en el período
        List<Movimiento> salidas = movimientoRepo.findByFechaBetween(
                desde.atStartOfDay(),
                hasta.atTime(23, 59, 59)
        ).stream()
                .filter(m -> m.getTipo() == Movimiento.TipoMovimiento.SALIDA)
                .collect(Collectors.toList());

        int totalSalidas = salidas.stream()
                .flatMap(m -> m.getDetalles().stream())
                .filter(d -> d.getProducto().getId().equals(productoId))
                .mapToInt(MovimientoDetalle::getCantidad)
                .sum();

        // Stock promedio
        int stockPromedio = producto.getStockActual();

        // Rotación = Ventas / Stock Promedio
        return stockPromedio > 0 ? (double) totalSalidas / stockPromedio : 0;
    }

    /**
     * Genera recomendaciones de compra basadas en el consumo histórico
     */
    public Map<String, Object> generarRecomendacionCompra(Integer productoId, int diasHistorico) {
        Producto producto = productoRepo.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        LocalDate desde = LocalDate.now().minusDays(diasHistorico);
        LocalDate hasta = LocalDate.now();

        // Calcular consumo promedio diario
        List<Movimiento> salidas = movimientoRepo.findByFechaBetween(
                desde.atStartOfDay(),
                hasta.atTime(23, 59, 59)
        ).stream()
                .filter(m -> m.getTipo() == Movimiento.TipoMovimiento.SALIDA)
                .collect(Collectors.toList());

        int totalSalidas = salidas.stream()
                .flatMap(m -> m.getDetalles().stream())
                .filter(d -> d.getProducto().getId().equals(productoId))
                .mapToInt(MovimientoDetalle::getCantidad)
                .sum();

        double consumoPromedioDiario = (double) totalSalidas / diasHistorico;

        // Días de cobertura actual
        int diasCobertura = consumoPromedioDiario > 0
                ? (int) (producto.getStockActual() / consumoPromedioDiario) : 999;

        // Cantidad recomendada para 30 días
        int cantidadRecomendada = (int) Math.ceil(consumoPromedioDiario * 30) - producto.getStockActual();
        cantidadRecomendada = Math.max(0, cantidadRecomendada);

        Map<String, Object> recomendacion = new HashMap<>();
        recomendacion.put("producto", producto);
        recomendacion.put("consumoPromedioDiario", consumoPromedioDiario);
        recomendacion.put("diasCobertura", diasCobertura);
        recomendacion.put("cantidadRecomendada", cantidadRecomendada);
        recomendacion.put("necesitaReabastecimiento", diasCobertura < 7);

        return recomendacion;
    }

    /**
     * Verifica y genera alertas automáticas
     */
    public void verificarYGenerarAlertas() {
        List<Producto> productos = productoRepo.findByActivoTrue();

        for (Producto producto : productos) {
            // Alerta de stock bajo
            if (producto.esStockBajo() && producto.getStockActual() > 0) {
                alertaService.crearAlerta(
                        Alerta.TipoAlerta.STOCK_BAJO,
                        producto,
                        String.format("Stock bajo: %d unidades (mínimo: %d)",
                                producto.getStockActual(), producto.getStockMinimo())
                );
            }

            // Alerta de stock agotado
            if (producto.estaAgotado()) {
                alertaService.crearAlerta(
                        Alerta.TipoAlerta.STOCK_AGOTADO,
                        producto,
                        "Producto completamente agotado"
                );
            }

            // Alerta de vencimiento próximo
            if (producto.getFechaVencimiento() != null && producto.estaProximoVencer(30)) {
                long diasRestantes = java.time.temporal.ChronoUnit.DAYS.between(
                        LocalDate.now(),
                        producto.getFechaVencimiento()
                );

                if (diasRestantes <= 0) {
                    alertaService.crearAlerta(
                            Alerta.TipoAlerta.VENCIDO,
                            producto,
                            "Producto vencido desde: " + producto.getFechaVencimiento()
                    );
                } else {
                    alertaService.crearAlerta(
                            Alerta.TipoAlerta.VENCIMIENTO_PROXIMO,
                            producto,
                            String.format("Vence en %d días (%s)", diasRestantes, producto.getFechaVencimiento())
                    );
                }
            }
        }
    }

    /**
     * Obtiene productos más vendidos
     */
    public List<Map<String, Object>> obtenerProductosMasVendidos(int limite, int diasHistorico) {
        LocalDate desde = LocalDate.now().minusDays(diasHistorico);
        LocalDate hasta = LocalDate.now();

        List<Movimiento> salidas = movimientoRepo.findByFechaBetween(
                desde.atStartOfDay(),
                hasta.atTime(23, 59, 59)
        ).stream()
                .filter(m -> m.getTipo() == Movimiento.TipoMovimiento.SALIDA)
                .collect(Collectors.toList());

        // Agrupar por producto
        Map<Producto, Integer> ventasPorProducto = new HashMap<>();

        for (Movimiento mov : salidas) {
            for (MovimientoDetalle detalle : mov.getDetalles()) {
                Producto producto = detalle.getProducto();
                ventasPorProducto.put(producto,
                        ventasPorProducto.getOrDefault(producto, 0) + detalle.getCantidad());
            }
        }

        // Ordenar y limitar
        return ventasPorProducto.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(limite)
                .map(entry -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("producto", entry.getKey());
                    item.put("cantidadVendida", entry.getValue());
                    item.put("valorTotal", entry.getValue() * entry.getKey().getPrecioVenta());
                    return item;
                })
                .collect(Collectors.toList());
    }

    /**
     * Calcula el valor del inventario por categoría
     */
    public Map<String, Double> valorInventarioPorCategoria() {
        List<Producto> productos = productoRepo.findByActivoTrue();

        return productos.stream()
                .filter(p -> p.getCategoria() != null)
                .collect(Collectors.groupingBy(
                        p -> p.getCategoria().getNombre(),
                        Collectors.summingDouble(p -> p.getStockActual() * p.getPrecioVenta())
                ));
    }
}
