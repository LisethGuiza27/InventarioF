package com.inventario.controller;

import com.inventario.model.*;
import com.inventario.service.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

    // ✅ SOLUCIÓN: Usar HttpServletRequest para extraer arrays dinámicos
    @PostMapping("/entrada")
    public String crearEntrada(
            @RequestParam("almacen.id") Integer almacenId,
            @RequestParam(value = "proveedor.id", required = false) Integer proveedorId,
            @RequestParam(value = "numeroDocumento", required = false) String numeroDocumento,
            @RequestParam(value = "observaciones", required = false) String observaciones,
            HttpServletRequest request,
            Authentication auth,
            RedirectAttributes redirect) {

        System.out.println("=== CREAR ENTRADA ===");
        System.out.println("Almacén ID: " + almacenId);

        try {
            // ✅ Extraer detalles dinámicamente del request
            List<Integer> productosIds = new ArrayList<>();
            List<Integer> cantidades = new ArrayList<>();
            List<Double> precios = new ArrayList<>();

            int index = 0;
            while (true) {
                String productoIdParam = request.getParameter("detalles[" + index + "].producto.id");
                String cantidadParam = request.getParameter("detalles[" + index + "].cantidad");
                String precioParam = request.getParameter("detalles[" + index + "].precioUnitario");

                if (productoIdParam == null || cantidadParam == null || precioParam == null) {
                    break;
                }

                productosIds.add(Integer.parseInt(productoIdParam));
                cantidades.add(Integer.parseInt(cantidadParam));
                precios.add(Double.parseDouble(precioParam));

                System.out.println("  Detalle " + index + ": Producto ID=" + productoIdParam
                        + ", Cantidad=" + cantidadParam + ", Precio=" + precioParam);

                index++;
            }

            System.out.println("Total productos recibidos: " + productosIds.size());

            // Validar que hay productos
            if (productosIds.isEmpty()) {
                throw new Exception("Debe agregar al menos un producto");
            }

            // Obtener usuario
            Usuario usuario = usuarioService.obtenerPorUsername(auth.getName())
                    .orElseThrow(() -> new Exception("Usuario no encontrado"));

            // Crear movimiento
            Movimiento movimiento = new Movimiento();
            movimiento.setTipo(Movimiento.TipoMovimiento.ENTRADA);
            movimiento.setSubtipo(Movimiento.SubtipoMovimiento.COMPRA);
            movimiento.setFechaMovimiento(LocalDateTime.now());
            movimiento.setNumeroDocumento(numeroDocumento);
            movimiento.setObservaciones(observaciones);
            movimiento.setUsuario(usuario);
            movimiento.setEstado(Movimiento.EstadoMovimiento.PENDIENTE);

            // Asignar almacén
            Almacen almacen = almacenService.obtenerPorId(almacenId)
                    .orElseThrow(() -> new Exception("Almacén no encontrado"));
            movimiento.setAlmacen(almacen);

            // Asignar proveedor si existe
            if (proveedorId != null && proveedorId > 0) {
                proveedorService.obtenerPorId(proveedorId)
                        .ifPresent(movimiento::setProveedor);
            }

            // Crear detalles
            List<MovimientoDetalle> detalles = new ArrayList<>();
            for (int i = 0; i < productosIds.size(); i++) {
                final int idx = i;
                
                Producto producto = productoService.obtenerPorId(productosIds.get(idx))
                        .orElseThrow(() -> new Exception("Producto no encontrado: " + productosIds.get(idx)));

                MovimientoDetalle detalle = new MovimientoDetalle();
                detalle.setProducto(producto);
                detalle.setCantidad(cantidades.get(idx));
                detalle.setPrecioUnitario(precios.get(idx));
                detalle.setMovimiento(movimiento);

                detalles.add(detalle);

                System.out.println("  ✓ " + producto.getNombre() + " x " + cantidades.get(idx) + " @ $" + precios.get(idx));
            }

            movimiento.setDetalles(detalles);
            movimiento.calcularTotales();

            System.out.println("Total items: " + movimiento.getTotalItems());
            System.out.println("Valor total: " + movimiento.getValorTotal());

            // Guardar
            Movimiento creado = service.crearEntrada(movimiento, usuario);

            System.out.println("✅ Entrada creada: " + creado.getCodigo() + " con " + creado.getDetalles().size() + " productos");

            redirect.addFlashAttribute("mensaje",
                    "Entrada registrada exitosamente: " + creado.getCodigo() + " (" + creado.getTotalItems() + " unidades)");
            redirect.addFlashAttribute("tipo", "success");

            return "redirect:/movimientos";

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Error: " + e.getMessage());
            redirect.addFlashAttribute("mensaje", "Error: " + e.getMessage());
            redirect.addFlashAttribute("tipo", "error");
            return "redirect:/movimientos/nuevo?tipo=ENTRADA";
        }
    }

    @PostMapping("/salida")
    public String crearSalida(
            @RequestParam("almacen.id") Integer almacenId,
            @RequestParam(value = "cliente.id", required = false) Integer clienteId,
            @RequestParam(value = "numeroDocumento", required = false) String numeroDocumento,
            @RequestParam(value = "observaciones", required = false) String observaciones,
            HttpServletRequest request,
            Authentication auth,
            RedirectAttributes redirect) {

        System.out.println("=== CREAR SALIDA ===");

        try {
            // ✅ Extraer detalles dinámicamente del request
            List<Integer> productosIds = new ArrayList<>();
            List<Integer> cantidades = new ArrayList<>();
            List<Double> precios = new ArrayList<>();

            int index = 0;
            while (true) {
                String productoIdParam = request.getParameter("detalles[" + index + "].producto.id");
                String cantidadParam = request.getParameter("detalles[" + index + "].cantidad");
                String precioParam = request.getParameter("detalles[" + index + "].precioUnitario");

                if (productoIdParam == null || cantidadParam == null || precioParam == null) {
                    break;
                }

                productosIds.add(Integer.parseInt(productoIdParam));
                cantidades.add(Integer.parseInt(cantidadParam));
                precios.add(Double.parseDouble(precioParam));

                System.out.println("  Detalle " + index + ": Producto ID=" + productoIdParam
                        + ", Cantidad=" + cantidadParam + ", Precio=" + precioParam);

                index++;
            }

            System.out.println("Total productos recibidos: " + productosIds.size());

            // Validar que hay productos
            if (productosIds.isEmpty()) {
                throw new Exception("Debe agregar al menos un producto");
            }

            // Obtener usuario
            Usuario usuario = usuarioService.obtenerPorUsername(auth.getName())
                    .orElseThrow(() -> new Exception("Usuario no encontrado"));

            // Validar stock ANTES de crear
            for (int i = 0; i < productosIds.size(); i++) {
                final int idx = i;
                
                Producto producto = productoService.obtenerPorId(productosIds.get(idx))
                        .orElseThrow(() -> new Exception("Producto no encontrado"));

                if (producto.getStockActual() < cantidades.get(idx)) {
                    throw new Exception("Stock insuficiente para " + producto.getNombre()
                            + ". Disponible: " + producto.getStockActual()
                            + ", Solicitado: " + cantidades.get(idx));
                }
            }

            // Crear movimiento
            Movimiento movimiento = new Movimiento();
            movimiento.setTipo(Movimiento.TipoMovimiento.SALIDA);
            movimiento.setSubtipo(Movimiento.SubtipoMovimiento.VENTA);
            movimiento.setFechaMovimiento(LocalDateTime.now());
            movimiento.setNumeroDocumento(numeroDocumento);
            movimiento.setObservaciones(observaciones);
            movimiento.setUsuario(usuario);
            movimiento.setEstado(Movimiento.EstadoMovimiento.PENDIENTE);

            // Asignar almacén
            Almacen almacen = almacenService.obtenerPorId(almacenId)
                    .orElseThrow(() -> new Exception("Almacén no encontrado"));
            movimiento.setAlmacen(almacen);

            // Asignar cliente si existe
            if (clienteId != null && clienteId > 0) {
                clienteService.obtenerPorId(clienteId)
                        .ifPresent(movimiento::setCliente);
            }

            // Crear detalles
            List<MovimientoDetalle> detalles = new ArrayList<>();
            for (int i = 0; i < productosIds.size(); i++) {
                final int idx = i;
                
                Producto producto = productoService.obtenerPorId(productosIds.get(idx))
                        .orElseThrow(() -> new Exception("Producto no encontrado: " + productosIds.get(idx)));

                MovimientoDetalle detalle = new MovimientoDetalle();
                detalle.setProducto(producto);
                detalle.setCantidad(cantidades.get(idx));
                detalle.setPrecioUnitario(precios.get(idx));
                detalle.setMovimiento(movimiento);

                detalles.add(detalle);

                System.out.println("  ✓ " + producto.getNombre() + " x " + cantidades.get(idx) + " @ $" + precios.get(idx));
            }

            movimiento.setDetalles(detalles);
            movimiento.calcularTotales();

            // Guardar
            Movimiento creado = service.crearSalida(movimiento, usuario);

            System.out.println("✅ Salida creada: " + creado.getCodigo() + " con " + creado.getDetalles().size() + " productos");

            redirect.addFlashAttribute("mensaje",
                    "Salida registrada exitosamente: " + creado.getCodigo() + " (" + creado.getTotalItems() + " unidades)");
            redirect.addFlashAttribute("tipo", "success");

            return "redirect:/movimientos";

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Error: " + e.getMessage());
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