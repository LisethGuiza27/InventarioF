package com.inventario.controller;

import com.inventario.model.Producto;
import com.inventario.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    @Autowired
    private ProductoService service;

    // GET - Listar todos (Vista HTML)
    @GetMapping
    public String listar(Model model) {
        List<Producto> productos = service.listarTodos();
        model.addAttribute("productos", productos);
        return "productos/listado";
    }

    // GET - Formulario nuevo producto
    @GetMapping("/nuevo")
    public String nuevoForm(Model model) {
        model.addAttribute("producto", new Producto());
        return "productos/formulario";
    }

    // POST - Crear producto
    @PostMapping
    public String crear(@ModelAttribute Producto producto, 
                       RedirectAttributes redirect) {
        try {
            service.crear(producto);
            redirect.addFlashAttribute("mensaje", "Producto creado exitosamente");
            redirect.addFlashAttribute("tipo", "success");
        } catch (Exception e) {
            redirect.addFlashAttribute("mensaje", "Error: " + e.getMessage());
            redirect.addFlashAttribute("tipo", "error");
        }
        return "redirect:/productos";
    }

    // GET - Formulario editar
    @GetMapping("/{id}/editar")
    public String editarForm(@PathVariable Integer id, Model model,
                            RedirectAttributes redirect) {
        Optional<Producto> p = service.obtenerPorId(id);
        if (p.isPresent()) {
            model.addAttribute("producto", p.get());
            return "productos/formulario";
        }
        redirect.addFlashAttribute("mensaje", "Producto no encontrado");
        redirect.addFlashAttribute("tipo", "error");
        return "redirect:/productos";
    }

    // PUT - Actualizar producto
    @PostMapping("/{id}")
    public String actualizar(@PathVariable Integer id,
                            @ModelAttribute Producto producto,
                            RedirectAttributes redirect) {
        try {
            service.actualizar(id, producto);
            redirect.addFlashAttribute("mensaje", "Producto actualizado exitosamente");
            redirect.addFlashAttribute("tipo", "success");
        } catch (Exception e) {
            redirect.addFlashAttribute("mensaje", "Error: " + e.getMessage());
            redirect.addFlashAttribute("tipo", "error");
        }
        return "redirect:/productos";
    }

    // DELETE - Eliminar producto
    @GetMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Integer id, RedirectAttributes redirect) {
        try {
            service.eliminar(id);
            redirect.addFlashAttribute("mensaje", "Producto eliminado exitosamente");
            redirect.addFlashAttribute("tipo", "success");
        } catch (Exception e) {
            redirect.addFlashAttribute("mensaje", "Error: " + e.getMessage());
            redirect.addFlashAttribute("tipo", "error");
        }
        return "redirect:/productos";
    }

    // GET - Home
    @GetMapping("/")
    public String home() {
        return "redirect:/productos";
    }
}
