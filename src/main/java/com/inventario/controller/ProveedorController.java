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
            model.addAttribute("mensaje", "Error al cargar proveedores: " + e.getMessage());
            model.addAttribute("tipo", "error");
            model.addAttribute("proveedores", List.of());
            return "proveedores/listado";
        }
    }

    @GetMapping("/nuevo")
    public String nuevoForm(Model model) {
        Proveedor proveedor = new Proveedor();
        proveedor.setActivo(true);
        proveedor.setDiasCredito(0);
        proveedor.setLimiteCredito(0.0);
        proveedor.setCalificacion(5);
        proveedor.setPais("Colombia");
        model.addAttribute("proveedor", proveedor);
        return "proveedores/formulario";
    }

    @PostMapping
    public String crear(@ModelAttribute Proveedor proveedor,
            RedirectAttributes redirect,
            Model model) {

        System.out.println("\n=== CREAR PROVEEDOR ===");
        System.out.println("Código: " + proveedor.getCodigo());
        System.out.println("Razón Social: " + proveedor.getRazonSocial());
        System.out.println("Nombre Comercial: " + proveedor.getNombreComercial());
        System.out.println("RFC: " + proveedor.getRfc());
        System.out.println("Teléfono: " + proveedor.getTelefono());
        System.out.println("Email: " + proveedor.getEmail());

        try {
            // Inicializar valores por defecto
            if (proveedor.getDiasCredito() == null) {
                proveedor.setDiasCredito(0);
            }
            if (proveedor.getLimiteCredito() == null) {
                proveedor.setLimiteCredito(0.0);
            }
            if (proveedor.getSaldoPendiente() == null) {
                proveedor.setSaldoPendiente(0.0);
            }
            if (proveedor.getCalificacion() == null) {
                proveedor.setCalificacion(5);
            }
            if (proveedor.getPais() == null || proveedor.getPais().isEmpty()) {
                proveedor.setPais("Colombia");
            }
            if (proveedor.getActivo() == null) {
                proveedor.setActivo(true);
            }

            Proveedor guardado = service.crear(proveedor);
            System.out.println("✅ Proveedor guardado con ID: " + guardado.getId());

            redirect.addFlashAttribute("mensaje", "Proveedor creado exitosamente");
            redirect.addFlashAttribute("tipo", "success");
            return "redirect:/proveedores";
        } catch (Exception e) {
            System.err.println("❌ Error al crear proveedor: " + e.getMessage());
            e.printStackTrace();

            model.addAttribute("mensaje", "Error: " + e.getMessage());
            model.addAttribute("tipo", "error");
            model.addAttribute("proveedor", proveedor);
            return "proveedores/formulario";
        }
    }

    @GetMapping("/{id}/editar")
    public String editarForm(@PathVariable Integer id, Model model, RedirectAttributes redirect) {
        try {
            Proveedor proveedor = service.obtenerPorId(id)
                    .orElseThrow(() -> new Exception("Proveedor no encontrado"));

            System.out.println("\n=== EDITAR PROVEEDOR ===");
            System.out.println("ID: " + proveedor.getId());
            System.out.println("Código: " + proveedor.getCodigo());
            System.out.println("Razón Social: " + proveedor.getRazonSocial());

            model.addAttribute("proveedor", proveedor);
            return "proveedores/formulario";
        } catch (Exception e) {
            e.printStackTrace();
            redirect.addFlashAttribute("mensaje", e.getMessage());
            redirect.addFlashAttribute("tipo", "error");
            return "redirect:/proveedores";
        }
    }

    @PostMapping("/{id}")
    public String actualizar(@PathVariable Integer id,
            @ModelAttribute Proveedor proveedor,
            RedirectAttributes redirect,
            Model model) {

        System.out.println("\n=== ACTUALIZAR PROVEEDOR ===");
        System.out.println("ID: " + id);
        System.out.println("Código recibido: " + proveedor.getCodigo());
        System.out.println("Razón Social recibida: " + proveedor.getRazonSocial());

        try {
            Proveedor actualizado = service.actualizar(id, proveedor);
            System.out.println("✅ Proveedor actualizado: " + actualizado.getCodigo());

            redirect.addFlashAttribute("mensaje", "Proveedor actualizado exitosamente");
            redirect.addFlashAttribute("tipo", "success");
            return "redirect:/proveedores";
        } catch (Exception e) {
            System.err.println("❌ Error al actualizar: " + e.getMessage());
            e.printStackTrace();

            redirect.addFlashAttribute("mensaje", "Error: " + e.getMessage());
            redirect.addFlashAttribute("tipo", "error");
            return "redirect:/proveedores/" + id + "/editar";
        }
    }

    @GetMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Integer id, RedirectAttributes redirect) {
        try {
            System.out.println("\n=== ELIMINAR PROVEEDOR ===");
            System.out.println("ID a eliminar: " + id);

            service.eliminar(id);

            System.out.println("✅ Proveedor eliminado correctamente");
            redirect.addFlashAttribute("mensaje", "Proveedor eliminado exitosamente");
            redirect.addFlashAttribute("tipo", "success");
        } catch (Exception e) {
            System.err.println("❌ Error al eliminar: " + e.getMessage());
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

            System.out.println("\n=== DETALLE PROVEEDOR ===");
            System.out.println("ID: " + proveedor.getId());
            System.out.println("Código: " + proveedor.getCodigo());
            System.out.println("Razón Social: " + proveedor.getRazonSocial());

            model.addAttribute("proveedor", proveedor);
            return "proveedores/detalle";
        } catch (Exception e) {
            System.err.println("❌ Error al obtener detalle: " + e.getMessage());
            e.printStackTrace();

            redirect.addFlashAttribute("mensaje", e.getMessage());
            redirect.addFlashAttribute("tipo", "error");
            return "redirect:/proveedores";
        }
    }
}
