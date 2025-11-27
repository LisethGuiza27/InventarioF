package com.inventario.controller;

import com.inventario.model.Producto;
import com.inventario.service.ProductoService;
import com.inventario.service.CategoriaService;
import com.inventario.service.ProveedorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;
    
    @Autowired
    private CategoriaService categoriaService;
    
    @Autowired
    private ProveedorService proveedorService;

    @GetMapping
    public String listar(Model model) {
        try {
            List<Producto> productos = productoService.listarActivos();
            model.addAttribute("productos", productos);
            return "productos/listado";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error al cargar productos: " + e.getMessage());
            model.addAttribute("productos", List.of());
            return "productos/listado";
        }
    }

    @GetMapping("/nuevo")
    public String nuevoForm(Model model) {
        try {
            model.addAttribute("producto", new Producto());
            model.addAttribute("categorias", categoriaService.listarActivos());
            model.addAttribute("proveedores", proveedorService.listarActivos());
            return "productos/formulario";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error al cargar el formulario: " + e.getMessage());
            return "redirect:/productos";
        }
    }

    @PostMapping
    public String crear(@Valid @ModelAttribute("producto") Producto producto, 
                       BindingResult result,
                       @RequestParam(value = "categoriaId", required = false) Integer categoriaId,
                       @RequestParam(value = "proveedorId", required = false) Integer proveedorId,
                       Model model,
                       RedirectAttributes redirect) {
        
        System.out.println("=== CREAR PRODUCTO ===");
        System.out.println("Código: " + producto.getCodigo());
        System.out.println("Nombre: " + producto.getNombre());
        System.out.println("Precio: " + producto.getPrecioVenta());
        System.out.println("CategoriaId: " + categoriaId);
        System.out.println("ProveedorId: " + proveedorId);
        
        if (result.hasErrors()) {
            System.out.println("Errores de validación:");
            result.getAllErrors().forEach(error -> 
                System.out.println("- " + error.getDefaultMessage())
            );
            
            model.addAttribute("categorias", categoriaService.listarActivos());
            model.addAttribute("proveedores", proveedorService.listarActivos());
            return "productos/formulario";
        }
        
        try {
            // Asignar categoría si existe
            if (categoriaId != null && categoriaId > 0) {
                categoriaService.obtenerPorId(categoriaId).ifPresent(producto::setCategoria);
            }
            
            // Asignar proveedor si existe
            if (proveedorId != null && proveedorId > 0) {
                proveedorService.obtenerPorId(proveedorId).ifPresent(producto::setProveedor);
            }
            
            // Inicializar valores por defecto
            if (producto.getStockActual() == null) producto.setStockActual(0);
            if (producto.getStockMinimo() == null) producto.setStockMinimo(0);
            if (producto.getActivo() == null) producto.setActivo(true);
            
            Producto productoGuardado = productoService.crear(producto);
            
            System.out.println("Producto guardado exitosamente con ID: " + productoGuardado.getId());
            
            redirect.addFlashAttribute("mensaje", "Producto creado exitosamente");
            redirect.addFlashAttribute("tipo", "success");
            
            return "redirect:/productos";
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al guardar producto: " + e.getMessage());
            
            redirect.addFlashAttribute("mensaje", "Error al crear producto: " + e.getMessage());
            redirect.addFlashAttribute("tipo", "error");
            
            return "redirect:/productos/nuevo";
        }
    }

    @GetMapping("/{id}/editar")
    public String editarForm(@PathVariable Integer id, Model model, RedirectAttributes redirect) {
        try {
            Optional<Producto> productoOpt = productoService.obtenerPorId(id);
            
            if (!productoOpt.isPresent()) {
                redirect.addFlashAttribute("mensaje", "Producto no encontrado con ID: " + id);
                redirect.addFlashAttribute("tipo", "error");
                return "redirect:/productos";
            }
            
            model.addAttribute("producto", productoOpt.get());
            model.addAttribute("categorias", categoriaService.listarActivos());
            model.addAttribute("proveedores", proveedorService.listarActivos());
            
            return "productos/formulario";
            
        } catch (Exception e) {
            e.printStackTrace();
            redirect.addFlashAttribute("mensaje", "Error al cargar producto: " + e.getMessage());
            redirect.addFlashAttribute("tipo", "error");
            return "redirect:/productos";
        }
    }

    @PostMapping("/{id}")
    public String actualizar(@PathVariable Integer id,
                            @Valid @ModelAttribute("producto") Producto producto,
                            @RequestParam(value = "categoriaId", required = false) Integer categoriaId,
                            @RequestParam(value = "proveedorId", required = false) Integer proveedorId,
                            BindingResult result,
                            Model model,
                            RedirectAttributes redirect) {
        
        if (result.hasErrors()) {
            model.addAttribute("categorias", categoriaService.listarActivos());
            model.addAttribute("proveedores", proveedorService.listarActivos());
            return "productos/formulario";
        }
        
        try {
            // Asignar categoría si existe
            if (categoriaId != null && categoriaId > 0) {
                categoriaService.obtenerPorId(categoriaId).ifPresent(producto::setCategoria);
            }
            
            // Asignar proveedor si existe
            if (proveedorId != null && proveedorId > 0) {
                proveedorService.obtenerPorId(proveedorId).ifPresent(producto::setProveedor);
            }
            
            Producto productoActualizado = productoService.actualizar(id, producto);
            
            redirect.addFlashAttribute("mensaje", "Producto actualizado exitosamente");
            redirect.addFlashAttribute("tipo", "success");
            
            return "redirect:/productos";
            
        } catch (Exception e) {
            e.printStackTrace();
            redirect.addFlashAttribute("mensaje", "Error al actualizar: " + e.getMessage());
            redirect.addFlashAttribute("tipo", "error");
            
            return "redirect:/productos/" + id + "/editar";
        }
    }

    @GetMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Integer id, RedirectAttributes redirect) {
        try {
            productoService.eliminar(id);
            
            redirect.addFlashAttribute("mensaje", "Producto eliminado exitosamente");
            redirect.addFlashAttribute("tipo", "success");
            
        } catch (Exception e) {
            e.printStackTrace();
            
            redirect.addFlashAttribute("mensaje", "Error al eliminar: " + e.getMessage());
            redirect.addFlashAttribute("tipo", "error");
        }
        
        return "redirect:/productos";
    }

    @GetMapping("/{id}/detalle")
    public String verDetalle(@PathVariable Integer id, Model model, RedirectAttributes redirect) {
        try {
            Optional<Producto> productoOpt = productoService.obtenerPorId(id);
            
            if (!productoOpt.isPresent()) {
                redirect.addFlashAttribute("mensaje", "Producto no encontrado");
                redirect.addFlashAttribute("tipo", "error");
                return "redirect:/productos";
            }
            
            model.addAttribute("producto", productoOpt.get());
            return "productos/detalle";
            
        } catch (Exception e) {
            e.printStackTrace();
            redirect.addFlashAttribute("mensaje", "Error: " + e.getMessage());
            redirect.addFlashAttribute("tipo", "error");
            return "redirect:/productos";
        }
    }

    @GetMapping("/buscar")
    public String buscar(@RequestParam String termino, Model model) {
        try {
            List<Producto> productos = productoService.buscar(termino);
            model.addAttribute("productos", productos);
            model.addAttribute("termino", termino);
            return "productos/listado";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error en la búsqueda: " + e.getMessage());
            model.addAttribute("productos", List.of());
            return "productos/listado";
        }
    }
}