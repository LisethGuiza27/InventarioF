package com.inventario.controller;

import com.inventario.model.Proveedor;
import com.inventario.service.ProveedorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/proveedores")
public class ProveedorController {

    @Autowired
    private ProveedorService service;

    @GetMapping
    public String listar(@RequestParam(required = false) String buscar, Model model) {
        try {
            List<Proveedor> proveedores;
            if (buscar != null && !buscar.trim().isEmpty()) {
                proveedores = service.buscar(buscar);
                model.addAttribute("buscar", buscar);
            } else {
                proveedores = service.listarActivos();
            }
            model.addAttribute("proveedores", proveedores);
            return "proveedores/listado";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error al cargar proveedores: " + e.getMessage());
            model.addAttribute("proveedores", List.of());
            return "proveedores/listado";
        }
    }

    @GetMapping("/nuevo")
    public String nuevoForm(Model model) {
        Proveedor proveedor = new Proveedor();
        proveedor.setActivo(true);
        model.addAttribute("proveedor", proveedor);
        return "proveedores/formulario";
    }

    @PostMapping
    public String crear(@Valid @ModelAttribute Proveedor proveedor,
                       BindingResult result,
                       RedirectAttributes redirect,
                       Model model) {
        
        System.out.println("=== CREAR PROVEEDOR ===");
        System.out.println("Código: " + proveedor.getCodigo());
        System.out.println("Razón Social: " + proveedor.getRazonSocial());
        
        if (result.hasErrors()) {
            System.out.println("Errores de validación:");
            result.getAllErrors().forEach(error -> 
                System.out.println("- " + error.getDefaultMessage())
            );
            return "proveedores/formulario";
        }
        
        try {
            // Inicializar valores por defecto
            if (proveedor.getDiasCredito() == null) proveedor.setDiasCredito(0);
            if (proveedor.getLimiteCredito() == null) proveedor.setLimiteCredito(0.0);
            if (proveedor.getSaldoPendiente() == null) proveedor.setSaldoPendiente(0.0);
            if (proveedor.getCalificacion() == null) proveedor.setCalificacion(5);
            if (proveedor.getPais() == null || proveedor.getPais().isEmpty()) proveedor.setPais("Colombia");
            if (proveedor.getActivo() == null) proveedor.setActivo(true);
            
            service.crear(proveedor);
            redirect.addFlashAttribute("mensaje", "Proveedor creado exitosamente");
            redirect.addFlashAttribute("tipo", "success");
            return "redirect:/proveedores";
        } catch (Exception e) {
            e.printStackTrace();
            redirect.addFlashAttribute("mensaje", "Error: " + e.getMessage());
            redirect.addFlashAttribute("tipo", "error");
            return "redirect:/proveedores/nuevo";
        }
    }

    @GetMapping("/{id}/editar")
    public String editarForm(@PathVariable Integer id, Model model, RedirectAttributes redirect) {
        try {
            Proveedor proveedor = service.obtenerPorId(id)
                    .orElseThrow(() -> new Exception("Proveedor no encontrado"));
            model.addAttribute("proveedor", proveedor);
            return "proveedores/formulario";
        } catch (Exception e) {
            redirect.addFlashAttribute("mensaje", e.getMessage());
            redirect.addFlashAttribute("tipo", "error");
            return "redirect:/proveedores";
        }
    }

    @PostMapping("/{id}")
    public String actualizar(@PathVariable Integer id,
                            @Valid @ModelAttribute Proveedor proveedor,
                            BindingResult result,
                            RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return "proveedores/formulario";
        }
        
        try {
            service.actualizar(id, proveedor);
            redirect.addFlashAttribute("mensaje", "Proveedor actualizado exitosamente");
            redirect.addFlashAttribute("tipo", "success");
        } catch (Exception e) {
            e.printStackTrace();
            redirect.addFlashAttribute("mensaje", "Error: " + e.getMessage());
            redirect.addFlashAttribute("tipo", "error");
        }
        return "redirect:/proveedores";
    }

    @GetMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Integer id, RedirectAttributes redirect) {
        try {
            service.eliminar(id);
            redirect.addFlashAttribute("mensaje", "Proveedor eliminado exitosamente");
            redirect.addFlashAttribute("tipo", "success");
        } catch (Exception e) {
            e.printStackTrace();
            redirect.addFlashAttribute("mensaje", "Error: " + e.getMessage());
            redirect.addFlashAttribute("tipo", "error");
        }
        return "redirect:/proveedores";
    }

    @GetMapping("/{id}/detalle")
    public String verDetalle(@PathVariable Integer id, Model model, RedirectAttributes redirect) {
        try {
            Proveedor proveedor = service.obtenerPorId(id)
                    .orElseThrow(() -> new Exception("Proveedor no encontrado"));
            model.addAttribute("proveedor", proveedor);
            return "proveedores/detalle";
        } catch (Exception e) {
            redirect.addFlashAttribute("mensaje", e.getMessage());
            redirect.addFlashAttribute("tipo", "error");
            return "redirect:/proveedores";
        }
    }
}