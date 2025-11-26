package com.inventario.controller;

import com.inventario.model.*;
import com.inventario.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/movimientos")
public class MovimientoController {

    @Autowired
    private MovimientoService service;
    
    @Autowired
    private ProductoService productoService;
    
    @Autowired
    private AlmacenService almacenService;
    
    @Autowired
    private ProveedorService proveedorService;
    
    @Autowired
    private ClienteService clienteService;
    
    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String listar(@RequestParam(required = false) String tipo,
                        @RequestParam(required = false) LocalDate desde,
                        @RequestParam(required = false) LocalDate hasta,
                        Model model) {
        try {
            List<Movimiento> movimientos;
            
            if (desde != null && hasta != null) {
                movimientos = service.obtenerPorRangoFechas(desde, hasta);
            } else if (tipo != null && !tipo.isEmpty()) {
                movimientos = service.obtenerPorTipo(Movimiento.TipoMovimiento.valueOf(tipo));
            } else {
                movimientos = service.listarTodos();
            }
            
            model.addAttribute("movimientos", movimientos);
            model.addAttribute("tipo", tipo);
            model.addAttribute("desde", desde);
            model.addAttribute("hasta", hasta);
            
            return "movimientos/listado";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar movimientos: " + e.getMessage());
            return "movimientos/listado";
        }
    }

    @GetMapping("/nuevo")
    public String nuevoForm(@RequestParam(required = false) String tipo, Model model) {
        model.addAttribute("movimiento", new Movimiento());
        model.addAttribute("tipo", tipo);
        model.addAttribute("productos", productoService.listarActivos());
        model.addAttribute("almacenes", almacenService.listarActivos());
        model.addAttribute("proveedores", proveedorService.listarActivos());
        model.addAttribute("clientes", clienteService.listarActivos());
        return "movimientos/crear";
    }

    @PostMapping("/entrada")
    public String crearEntrada(@ModelAttribute Movimiento movimiento,
                              Authentication auth,
                              RedirectAttributes redirect) {
        try {
            Usuario usuario = usuarioService.obtenerPorUsername(auth.getName())
                    .orElseThrow(() -> new Exception("Usuario no encontrado"));
            
            Movimiento creado = service.crearEntrada(movimiento, usuario);
            redirect.addFlashAttribute("mensaje", "Entrada creada exitosamente");
            redirect.addFlashAttribute("tipo", "success");
            return "redirect:/movimientos/" + creado.getId();
        } catch (Exception e) {
            redirect.addFlashAttribute("mensaje", "Error: " + e.getMessage());
            redirect.addFlashAttribute("tipo", "error");
            return "redirect:/movimientos/nuevo?tipo=ENTRADA";
        }
    }

    @PostMapping("/salida")
    public String crearSalida(@ModelAttribute Movimiento movimiento,
                             Authentication auth,
                             RedirectAttributes redirect) {
        try {
            Usuario usuario = usuarioService.obtenerPorUsername(auth.getName())
                    .orElseThrow(() -> new Exception("Usuario no encontrado"));
            
            Movimiento creado = service.crearSalida(movimiento, usuario);
            redirect.addFlashAttribute("mensaje", "Salida creada exitosamente");
            redirect.addFlashAttribute("tipo", "success");
            return "redirect:/movimientos/" + creado.getId();
        } catch (Exception e) {
            redirect.addFlashAttribute("mensaje", "Error: " + e.getMessage());
            redirect.addFlashAttribute("tipo", "error");
            return "redirect:/movimientos/nuevo?tipo=SALIDA";
        }
    }

    @GetMapping("/{id}")
    public String verDetalle(@PathVariable Integer id, Model model, RedirectAttributes redirect) {
        try {
            Movimiento movimiento = service.obtenerPorId(id)
                    .orElseThrow(() -> new Exception("Movimiento no encontrado"));
            model.addAttribute("movimiento", movimiento);
            return "movimientos/detalle";
        } catch (Exception e) {
            redirect.addFlashAttribute("mensaje", e.getMessage());
            redirect.addFlashAttribute("tipo", "error");
            return "redirect:/movimientos";
        }
    }

    @PostMapping("/{id}/aprobar")
    public String aprobar(@PathVariable Integer id,
                         Authentication auth,
                         RedirectAttributes redirect) {
        try {
            Usuario usuario = usuarioService.obtenerPorUsername(auth.getName())
                    .orElseThrow(() -> new Exception("Usuario no encontrado"));
            
            service.aprobar(id, usuario);
            redirect.addFlashAttribute("mensaje", "Movimiento aprobado exitosamente");
            redirect.addFlashAttribute("tipo", "success");
        } catch (Exception e) {
            redirect.addFlashAttribute("mensaje", "Error: " + e.getMessage());
            redirect.addFlashAttribute("tipo", "error");
        }
        return "redirect:/movimientos/" + id;
    }

    @PostMapping("/{id}/completar")
    public String completar(@PathVariable Integer id, RedirectAttributes redirect) {
        try {
            service.completar(id);
            redirect.addFlashAttribute("mensaje", "Movimiento completado exitosamente");
            redirect.addFlashAttribute("tipo", "success");
        } catch (Exception e) {
            redirect.addFlashAttribute("mensaje", "Error: " + e.getMessage());
            redirect.addFlashAttribute("tipo", "error");
        }
        return "redirect:/movimientos/" + id;
    }

    @PostMapping("/{id}/anular")
    public String anular(@PathVariable Integer id, RedirectAttributes redirect) {
        try {
            service.anular(id);
            redirect.addFlashAttribute("mensaje", "Movimiento anulado exitosamente");
            redirect.addFlashAttribute("tipo", "success");
        } catch (Exception e) {
            redirect.addFlashAttribute("mensaje", "Error: " + e.getMessage());
            redirect.addFlashAttribute("tipo", "error");
        }
        return "redirect:/movimientos/" + id;
    }
}