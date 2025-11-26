package com.inventario.controller;

import com.inventario.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
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
            String username = authentication.getName();
            
            // Estadísticas generales
            Map<String, Object> estadisticas = obtenerEstadisticas();
            model.addAttribute("estadisticas", estadisticas);
            
            // Productos con stock bajo
            model.addAttribute("productosStockBajo", 
                productoService.obtenerProductosStockBajo(10));
            
            // Alertas activas
            model.addAttribute("alertasActivas", 
                alertaService.obtenerAlertasActivas(5));
            
            // Movimientos recientes
            model.addAttribute("movimientosRecientes", 
                movimientoService.obtenerMovimientosRecientes(10));
            
            // Productos próximos a vencer
            model.addAttribute("productosProximosVencer", 
                productoService.obtenerProductosProximosVencer(30));
            
            // Información del usuario
            model.addAttribute("username", username);
            model.addAttribute("fechaActual", LocalDate.now());
            
            return "dashboard/index";
            
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar el dashboard: " + e.getMessage());
            return "error/500";
        }
    }
    
    private Map<String, Object> obtenerEstadisticas() {
        long totalProductos = productoService.contarProductosActivos();
        long productosBajoStock = productoService.contarProductosStockBajo();
        long productosAgotados = productoService.contarProductosAgotados();
        long movimientosHoy = movimientoService.contarMovimientosHoy();
        
        double valorInventario = productoService.calcularValorTotalInventario();
        
        return Map.of(
            "totalProductos", totalProductos,
            "productosBajoStock", productosBajoStock,
            "productosAgotados", productosAgotados,
            "movimientosHoy", movimientosHoy,
            "valorInventario", valorInventario,
            "porcentajeStockBajo", totalProductos > 0 ? 
                (productosBajoStock * 100.0 / totalProductos) : 0
        );
    }
}