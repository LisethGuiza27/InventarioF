package com.inventario.controller;

import com.inventario.model.Cliente;
import com.inventario.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private ClienteService service;

    @GetMapping
    public String listar(@RequestParam(required = false) String buscar, Model model) {
        try {
            List<Cliente> clientes;
            if (buscar != null && !buscar.trim().isEmpty()) {
                clientes = service.buscar(buscar);
                model.addAttribute("buscar", buscar);
            } else {
                clientes = service.listarActivos();
            }
            model.addAttribute("clientes", clientes);
            return "clientes/listado";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar clientes: " + e.getMessage());
            return "clientes/listado";
        }
    }

    @GetMapping("/nuevo")
    public String nuevoForm(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "clientes/formulario";
    }

    @PostMapping
public String crear(@Valid @ModelAttribute Cliente cliente,
                   BindingResult result,
                   RedirectAttributes redirect,
                   Model model) {
    
    System.out.println("=== CREAR CLIENTE ===");
    System.out.println("Código: " + cliente.getCodigo());
    System.out.println("Nombre: " + cliente.getNombre());
    
    if (result.hasErrors()) {
        System.out.println("Errores de validación:");
        result.getAllErrors().forEach(error -> 
            System.out.println("- " + error.getDefaultMessage())
        );
        return "clientes/formulario";
    }
    
    try {
        // Inicializar valores por defecto
        if (cliente.getDiasCredito() == null) cliente.setDiasCredito(0);
        if (cliente.getLimiteCredito() == null) cliente.setLimiteCredito(0.0);
        if (cliente.getSaldoPendiente() == null) cliente.setSaldoPendiente(0.0);
        if (cliente.getTotalCompras() == null) cliente.setTotalCompras(0.0);
        
        Cliente clienteGuardado = service.crear(cliente);
        
        System.out.println("Cliente guardado exitosamente con ID: " + clienteGuardado.getId());
        
        redirect.addFlashAttribute("mensaje", "Cliente creado exitosamente");
        redirect.addFlashAttribute("tipo", "success");
        return "redirect:/clientes";
        
    } catch (Exception e) {
        e.printStackTrace();
        System.err.println("Error al guardar cliente: " + e.getMessage());
        
        redirect.addFlashAttribute("mensaje", "Error: " + e.getMessage());
        redirect.addFlashAttribute("tipo", "error");
        return "redirect:/clientes/nuevo";
    }
}

    @GetMapping("/{id}/editar")
    public String editarForm(@PathVariable Integer id, Model model, RedirectAttributes redirect) {
        try {
            Cliente cliente = service.obtenerPorId(id)
                    .orElseThrow(() -> new Exception("Cliente no encontrado"));
            model.addAttribute("cliente", cliente);
            return "clientes/formulario";
        } catch (Exception e) {
            redirect.addFlashAttribute("mensaje", e.getMessage());
            redirect.addFlashAttribute("tipo", "error");
            return "redirect:/clientes";
        }
    }

    @PostMapping("/{id}")
    public String actualizar(@PathVariable Integer id,
                            @Valid @ModelAttribute Cliente cliente,
                            BindingResult result,
                            RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return "clientes/formulario";
        }
        
        try {
            service.actualizar(id, cliente);
            redirect.addFlashAttribute("mensaje", "Cliente actualizado exitosamente");
            redirect.addFlashAttribute("tipo", "success");
        } catch (Exception e) {
            redirect.addFlashAttribute("mensaje", "Error: " + e.getMessage());
            redirect.addFlashAttribute("tipo", "error");
        }
        return "redirect:/clientes";
    }

    @GetMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Integer id, RedirectAttributes redirect) {
        try {
            service.eliminar(id);
            redirect.addFlashAttribute("mensaje", "Cliente eliminado exitosamente");
            redirect.addFlashAttribute("tipo", "success");
        } catch (Exception e) {
            redirect.addFlashAttribute("mensaje", "Error: " + e.getMessage());
            redirect.addFlashAttribute("tipo", "error");
        }
        return "redirect:/clientes";
    }

    @GetMapping("/{id}/detalle")
    public String verDetalle(@PathVariable Integer id, Model model, RedirectAttributes redirect) {
        try {
            Cliente cliente = service.obtenerPorId(id)
                    .orElseThrow(() -> new Exception("Cliente no encontrado"));
            model.addAttribute("cliente", cliente);
            return "clientes/detalle";
        } catch (Exception e) {
            redirect.addFlashAttribute("mensaje", e.getMessage());
            redirect.addFlashAttribute("tipo", "error");
            return "redirect:/clientes";
        }
    }
}