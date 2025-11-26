package com.inventario.controller;

import com.inventario.model.Usuario;
import com.inventario.service.RolService;
import com.inventario.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService service;
    
    @Autowired
    private RolService rolService;

    @GetMapping
    public String listar(Model model) {
        try {
            List<Usuario> usuarios = service.listarTodos();
            model.addAttribute("usuarios", usuarios);
            return "usuarios/listado";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar usuarios: " + e.getMessage());
            return "usuarios/listado";
        }
    }

    @GetMapping("/nuevo")
    public String nuevoForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("roles", rolService.listarActivos());
        return "usuarios/formulario";
    }

    @PostMapping
    public String crear(@Valid @ModelAttribute Usuario usuario,
                       BindingResult result,
                       RedirectAttributes redirect,
                       Model model) {
        if (result.hasErrors()) {
            model.addAttribute("roles", rolService.listarActivos());
            return "usuarios/formulario";
        }
        
        try {
            service.crear(usuario);
            redirect.addFlashAttribute("mensaje", "Usuario creado exitosamente");
            redirect.addFlashAttribute("tipo", "success");
        } catch (Exception e) {
            redirect.addFlashAttribute("mensaje", "Error: " + e.getMessage());
            redirect.addFlashAttribute("tipo", "error");
        }
        return "redirect:/usuarios";
    }

    @GetMapping("/{id}/editar")
    public String editarForm(@PathVariable Integer id, Model model, RedirectAttributes redirect) {
        try {
            Usuario usuario = service.obtenerPorId(id)
                    .orElseThrow(() -> new Exception("Usuario no encontrado"));
            model.addAttribute("usuario", usuario);
            model.addAttribute("roles", rolService.listarActivos());
            return "usuarios/formulario";
        } catch (Exception e) {
            redirect.addFlashAttribute("mensaje", e.getMessage());
            redirect.addFlashAttribute("tipo", "error");
            return "redirect:/usuarios";
        }
    }

    @PostMapping("/{id}")
    public String actualizar(@PathVariable Integer id,
                            @Valid @ModelAttribute Usuario usuario,
                            BindingResult result,
                            RedirectAttributes redirect,
                            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("roles", rolService.listarActivos());
            return "usuarios/formulario";
        }
        
        try {
            service.actualizar(id, usuario);
            redirect.addFlashAttribute("mensaje", "Usuario actualizado exitosamente");
            redirect.addFlashAttribute("tipo", "success");
        } catch (Exception e) {
            redirect.addFlashAttribute("mensaje", "Error: " + e.getMessage());
            redirect.addFlashAttribute("tipo", "error");
        }
        return "redirect:/usuarios";
    }

    @GetMapping("/{id}/desactivar")
    public String desactivar(@PathVariable Integer id, RedirectAttributes redirect) {
        try {
            service.desactivar(id);
            redirect.addFlashAttribute("mensaje", "Usuario desactivado exitosamente");
            redirect.addFlashAttribute("tipo", "success");
        } catch (Exception e) {
            redirect.addFlashAttribute("mensaje", "Error: " + e.getMessage());
            redirect.addFlashAttribute("tipo", "error");
        }
        return "redirect:/usuarios";
    }

    @GetMapping("/{id}/activar")
    public String activar(@PathVariable Integer id, RedirectAttributes redirect) {
        try {
            service.activar(id);
            redirect.addFlashAttribute("mensaje", "Usuario activado exitosamente");
            redirect.addFlashAttribute("tipo", "success");
        } catch (Exception e) {
            redirect.addFlashAttribute("mensaje", "Error: " + e.getMessage());
            redirect.addFlashAttribute("tipo", "error");
        }
        return "redirect:/usuarios";
    }

    @GetMapping("/perfil")
    public String verPerfil(Authentication auth, Model model) {
        try {
            Usuario usuario = service.obtenerPorUsername(auth.getName())
                    .orElseThrow(() -> new Exception("Usuario no encontrado"));
            model.addAttribute("usuario", usuario);
            return "usuarios/perfil";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "usuarios/perfil";
        }
    }

    @PostMapping("/cambiar-password")
    public String cambiarPassword(@RequestParam String passwordActual,
                                 @RequestParam String passwordNueva,
                                 @RequestParam String passwordConfirmar,
                                 Authentication auth,
                                 RedirectAttributes redirect) {
        try {
            if (!passwordNueva.equals(passwordConfirmar)) {
                throw new Exception("Las contraseñas no coinciden");
            }
            
            Usuario usuario = service.obtenerPorUsername(auth.getName())
                    .orElseThrow(() -> new Exception("Usuario no encontrado"));
            
            service.cambiarPassword(usuario.getId(), passwordActual, passwordNueva);
            redirect.addFlashAttribute("mensaje", "Contraseña cambiada exitosamente");
            redirect.addFlashAttribute("tipo", "success");
        } catch (Exception e) {
            redirect.addFlashAttribute("mensaje", "Error: " + e.getMessage());
            redirect.addFlashAttribute("tipo", "error");
        }
        return "redirect:/usuarios/perfil";
    }
}