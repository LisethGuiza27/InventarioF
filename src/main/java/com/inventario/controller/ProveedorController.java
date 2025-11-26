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
            model.addAttribute("error", "Error al cargar proveedores: " + e.getMessage());
            return "proveedores/listado";
        }
    }

    @GetMapping("/nuevo")
    public String nuevoForm(Model model) {
        model.addAttribute("proveedor", new Proveedor());
        return "proveedores/formulario";
    }

    @PostMapping
    public String crear(@Valid @ModelAttribute Proveedor proveedor,
                       BindingResult result,
                       RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return "proveedores/formulario";
        }
        
        try {
            service.crear(proveedor);
            redirect.addFlashAttribute("mensaje", "Proveedor creado exitosamente");
            redirect.addFlashAttribute("tipo", "success");
        } catch (Exception e) {
            redirect.addFlashAttribute("mensaje", "Error: " + e.getMessage());
            redirect.addFlashAttribute("tipo", "error");
        }
        return "redirect:/proveedores";
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