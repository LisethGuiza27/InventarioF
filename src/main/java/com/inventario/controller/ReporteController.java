package com.inventario.controller;

import com.inventario.service.ReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

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
}