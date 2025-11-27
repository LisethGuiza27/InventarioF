package com.inventario.controller;

import com.inventario.model.Almacen;
import com.inventario.service.AlmacenService;
import com.inventario.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/almacenes")
public class AlmacenController {

    @Autowired
    private AlmacenService service;
    
    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String listar(Model model) {
        try {
            List<Almacen> almacenes = service.listarTodos();
            model.addAttribute("almacenes", almacenes);
            return "almacenes/listado";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar almacenes: " + e.getMessage());
            return "almacenes/listado";
        }
    }

    @GetMapping("/nuevo")
    public String nuevoForm(Model model) {
        model.addAttribute("almacen", new Almacen());
        model.addAttribute("usuarios", usuarioService.listarActivos());
        return "almacenes/formulario";
    }

    @PostMapping
    public String crear(@Valid @ModelAttribute Almacen almacen,
                       BindingResult result,
                       RedirectAttributes redirect,
                       Model model) {
        if (result.hasErrors()) {
            model.addAttribute("usuarios", usuarioService.listarActivos());
            return "almacenes/formulario";
        }
        
        try {
            service.crear(almacen);
            redirect.addFlashAttribute("mensaje", "Almacén creado exitosamente");
            redirect.addFlashAttribute("tipo", "success");
        } catch (Exception e) {
            redirect.addFlashAttribute("mensaje", "Error: " + e.getMessage());
            redirect.addFlashAttribute("tipo", "error");
        }
        return "redirect:/almacenes";
    }

    @GetMapping("/{id}/editar")
    public String editarForm(@PathVariable Integer id, Model model, RedirectAttributes redirect) {
        try {
            Almacen almacen = service.obtenerPorId(id)
                    .orElseThrow(() -> new Exception("Almacén no encontrado"));
            model.addAttribute("almacen", almacen);
            model.addAttribute("usuarios", usuarioService.listarActivos());
            return "almacenes/formulario";
        } catch (Exception e) {
            redirect.addFlashAttribute("mensaje", e.getMessage());
            redirect.addFlashAttribute("tipo", "error");
            return "redirect:/almacenes";
        }
    }

    @PostMapping("/{id}")
    public String actualizar(@PathVariable Integer id,
                            @Valid @ModelAttribute Almacen almacen,
                            BindingResult result,
                            RedirectAttributes redirect,
                            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("usuarios", usuarioService.listarActivos());
            return "almacenes/formulario";
        }
        
        try {
            service.actualizar(id, almacen);
            redirect.addFlashAttribute("mensaje", "Almacén actualizado exitosamente");
            redirect.addFlashAttribute("tipo", "success");
        } catch (Exception e) {
            redirect.addFlashAttribute("mensaje", "Error: " + e.getMessage());
            redirect.addFlashAttribute("tipo", "error");
        }
        return "redirect:/almacenes";
    }

    @GetMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Integer id, RedirectAttributes redirect) {
        try {
            System.out.println("=== ELIMINAR ALMACÉN ===");
            System.out.println("ID: " + id);
            
            service.eliminar(id);
            
            redirect.addFlashAttribute("mensaje", "Almacén desactivado exitosamente");
            redirect.addFlashAttribute("tipo", "success");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al desactivar almacén: " + e.getMessage());
            
            redirect.addFlashAttribute("mensaje", "Error: " + e.getMessage());
            redirect.addFlashAttribute("tipo", "error");
        }
        return "redirect:/almacenes";
    }

    @GetMapping("/{id}/inventario")
    public String verInventario(@PathVariable Integer id, Model model, RedirectAttributes redirect) {
        try {
            Almacen almacen = service.obtenerPorId(id)
                    .orElseThrow(() -> new Exception("Almacén no encontrado"));
            model.addAttribute("almacen", almacen);
            model.addAttribute("inventario", service.obtenerInventario(id));
            return "almacenes/inventario";
        } catch (Exception e) {
            redirect.addFlashAttribute("mensaje", e.getMessage());
            redirect.addFlashAttribute("tipo", "error");
            return "redirect:/almacenes";
        }
    }
}