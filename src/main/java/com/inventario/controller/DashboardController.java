package com.inventario.controller;

import com.inventario.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private MovimientoService movimientoService;

    @Autowired
    private AlertaService alertaService;

    @GetMapping
    public String dashboard(Model model, Authentication authentication) {
        try {
            String username = authentication != null ? authentication.getName() : "Usuario";

            // Estadísticas generales (con manejo de errores)
            Map<String, Object> estadisticas = obtenerEstadisticasSeguras();
            model.addAttribute("estadisticas", estadisticas);

            // Productos con stock bajo (manejo seguro)
            try {
                model.addAttribute("productosStockBajo",
                        productoService.obtenerProductosStockBajo(10));
            } catch (Exception e) {
                model.addAttribute("productosStockBajo", java.util.Collections.emptyList());
            }

            // Alertas activas (manejo seguro)
            try {
                model.addAttribute("alertasActivas",
                        alertaService.obtenerAlertasActivas(5));
            } catch (Exception e) {
                model.addAttribute("alertasActivas", java.util.Collections.emptyList());
            }

            // Movimientos recientes (manejo seguro)
            try {
                model.addAttribute("movimientosRecientes",
                        movimientoService.obtenerMovimientosRecientes(10));
            } catch (Exception e) {
                model.addAttribute("movimientosRecientes", java.util.Collections.emptyList());
            }

            // Productos próximos a vencer (manejo seguro)
            try {
                model.addAttribute("productosProximosVencer",
                        productoService.obtenerProductosProximosVencer(30));
            } catch (Exception e) {
                model.addAttribute("productosProximosVencer", java.util.Collections.emptyList());
            }

            // Información del usuario
            model.addAttribute("username", username);
            model.addAttribute("fechaActual", LocalDate.now());

            return "dashboard/index";

        } catch (Exception e) {
            System.err.println("Error en dashboard: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Error al cargar el dashboard: " + e.getMessage());
            // Retornar una vista de dashboard básica incluso con error
            model.addAttribute("username", "Usuario");
            model.addAttribute("fechaActual", LocalDate.now());
            model.addAttribute("estadisticas", obtenerEstadisticasVacias());
            return "dashboard/index";
        }
    }

    private Map<String, Object> obtenerEstadisticasSeguras() {
        try {
            long totalProductos = productoService.contarProductosActivos();
            long productosBajoStock = productoService.contarProductosStockBajo();
            long productosAgotados = productoService.contarProductosAgotados();
            long movimientosHoy = movimientoService.contarMovimientosHoy();
            double valorInventario = productoService.calcularValorTotalInventario();

            Map<String, Object> stats = new HashMap<>();
            stats.put("totalProductos", totalProductos);
            stats.put("productosBajoStock", productosBajoStock);
            stats.put("productosAgotados", productosAgotados);
            stats.put("movimientosHoy", movimientosHoy);
            stats.put("valorInventario", valorInventario);
            stats.put("porcentajeStockBajo", totalProductos > 0
                    ? (productosBajoStock * 100.0 / totalProductos) : 0);

            return stats;
        } catch (Exception e) {
            System.err.println("Error obteniendo estadísticas: " + e.getMessage());
            return obtenerEstadisticasVacias();
        }
    }

    private Map<String, Object> obtenerEstadisticasVacias() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalProductos", 0L);
        stats.put("productosBajoStock", 0L);
        stats.put("productosAgotados", 0L);
        stats.put("movimientosHoy", 0L);
        stats.put("valorInventario", 0.0);
        stats.put("porcentajeStockBajo", 0.0);
        return stats;
    }
}
