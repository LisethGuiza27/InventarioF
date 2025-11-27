package com.inventario.controller;

import com.inventario.model.Producto;
import com.inventario.service.ReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/reportes")
public class ReporteController {

    @Autowired
    private ReporteService service;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("fechaDesde", LocalDate.now().minusDays(30));
        model.addAttribute("fechaHasta", LocalDate.now());
        return "reportes/index";
    }

    @GetMapping("/inventario")
    public String reporteInventario(Model model) {
        try {
            model.addAttribute("reporte", service.reporteInventario());
            return "reportes/inventario";
        } catch (Exception e) {
            model.addAttribute("error", "Error al generar reporte: " + e.getMessage());
            return "reportes/inventario";
        }
    }

    @GetMapping("/movimientos")
    public String reporteMovimientos(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            Model model) {
        try {
            model.addAttribute("reporte", service.reporteMovimientos(desde, hasta));
            model.addAttribute("desde", desde);
            model.addAttribute("hasta", hasta);
            return "reportes/movimientos";
        } catch (Exception e) {
            model.addAttribute("error", "Error al generar reporte: " + e.getMessage());
            return "reportes/movimientos";
        }
    }

    @GetMapping("/stock-bajo")
    public String reporteStockBajo(Model model) {
        try {
            model.addAttribute("productos", service.reporteStockBajo());
            return "reportes/stock-bajo";
        } catch (Exception e) {
            model.addAttribute("error", "Error al generar reporte: " + e.getMessage());
            return "reportes/stock-bajo";
        }
    }

    @GetMapping("/valoracion")
    public String reporteValoracion(Model model) {
        try {
            model.addAttribute("reporte", service.reporteValoracion());
            return "reportes/valoracion";
        } catch (Exception e) {
            model.addAttribute("error", "Error al generar reporte: " + e.getMessage());
            return "reportes/valoracion";
        }
    }

    @GetMapping("/kardex/{productoId}")
    public String kardexProducto(@PathVariable Integer productoId, Model model) {
        try {
            model.addAttribute("kardex", service.kardexProducto(productoId));
            return "reportes/kardex";
        } catch (Exception e) {
            model.addAttribute("error", "Error al generar kardex: " + e.getMessage());
            return "reportes/kardex";
        }
    }

    @GetMapping("/proximos-vencer")
    public String reporteProximosVencer(@RequestParam(defaultValue = "30") int dias, Model model) {
        try {
            List<Producto> productos = service.reporteProductosProximosVencer(dias);
            model.addAttribute("productos", productos);
            model.addAttribute("dias", dias);
            return "reportes/proximos-vencer";
        } catch (Exception e) {
            model.addAttribute("error", "Error al generar reporte: " + e.getMessage());
            return "reportes/proximos-vencer";
        }
    }

    @GetMapping("/mas-vendidos")
    public String reporteMasVendidos(@RequestParam(defaultValue = "10") int top, Model model) {
        try {
            List<Map<String, Object>> productos = service.reporteProductosMasVendidos(top);
            model.addAttribute("productos", productos);
            model.addAttribute("top", top);
            return "reportes/mas-vendidos";
        } catch (Exception e) {
            model.addAttribute("error", "Error al generar reporte: " + e.getMessage());
            return "reportes/mas-vendidos";
        }
    }

    @GetMapping("/rotacion")
    public String reporteRotacion(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            Model model) {
        try {
            Map<String, Object> reporte = service.reporteRotacion(desde, hasta);
            model.addAttribute("reporte", reporte);
            model.addAttribute("desde", desde);
            model.addAttribute("hasta", hasta);
            return "reportes/rotacion";
        } catch (Exception e) {
            model.addAttribute("error", "Error al generar reporte: " + e.getMessage());
            return "reportes/rotacion";
        }
    }

}
