package com.inventario.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja excepciones de negocio
     */
    @ExceptionHandler(BusinessException.class)
    public ModelAndView handleBusinessException(
            BusinessException ex,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute("mensaje", ex.getMessage());
        redirectAttributes.addFlashAttribute("tipo", "error");

        String referer = request.getHeader("Referer");
        if (referer != null && !referer.isEmpty()) {
            return new ModelAndView("redirect:" + referer);
        }

        return new ModelAndView("redirect:/dashboard");
    }

    /**
     * Maneja excepciones de recurso no encontrado
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ModelAndView handleResourceNotFoundException(
            ResourceNotFoundException ex,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute("mensaje", ex.getMessage());
        redirectAttributes.addFlashAttribute("tipo", "error");

        return new ModelAndView("redirect:/dashboard");
    }

    /**
     * Maneja errores de validación
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(
            MethodArgumentNotValidException ex) {

        Map<String, Object> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error
                -> errors.put(error.getField(), error.getDefaultMessage())
        );

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("errors", errors);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja cualquier excepción no contemplada
     */
    @ExceptionHandler(Exception.class)
    public ModelAndView handleGeneralException(
            Exception ex,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        ex.printStackTrace(); // Log para desarrollo

        String mensaje = "Ha ocurrido un error inesperado. Por favor, contacte al administrador.";

        // En desarrollo, mostramos el mensaje real
        if (isDevelopmentMode()) {
            mensaje = "Error: " + ex.getMessage();
        }

        redirectAttributes.addFlashAttribute("mensaje", mensaje);
        redirectAttributes.addFlashAttribute("tipo", "error");

        return new ModelAndView("redirect:/dashboard");
    }

    /**
     * Determina si estamos en modo desarrollo
     */
    private boolean isDevelopmentMode() {
        String profile = System.getProperty("spring.profiles.active");
        return profile != null && profile.contains("dev");
    }
}
