package com.inventario.service;

import com.inventario.model.*;
import com.inventario.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MovimientoService {

    @Autowired
    private MovimientoRepository movimientoRepo;
    
    @Autowired
    private ProductoService productoService;
    
    @Autowired
    private KardexRepository kardexRepo;
    
    @Autowired
    private ProductoRepository productoRepo;

    // Listar todos
    public List<Movimiento> listarTodos() {
        return movimientoRepo.findAll();
    }
    
    // Obtener por ID
    public Optional<Movimiento> obtenerPorId(Integer id) {
        return movimientoRepo.findById(id);
    }
    
    // Obtener movimientos recientes
    public List<Movimiento> obtenerMovimientosRecientes(int limite) {
        return movimientoRepo.findAllOrderByFechaDesc().stream()
                .limit(limite)
                .toList();
    }
    
    // Movimientos de hoy
    public List<Movimiento> obtenerMovimientosHoy() {
        return movimientoRepo.findMovimientosHoy();
    }
    
    // Contar movimientos de hoy
    public long contarMovimientosHoy() {
        return movimientoRepo.countMovimientosHoy();
    }
    
    // Crear movimiento de entrada
    public Movimiento crearEntrada(Movimiento movimiento, Usuario usuario) throws Exception {
        validarMovimiento(movimiento);
        
        movimiento.setCodigo(generarCodigo("ENT"));
        movimiento.setTipo(Movimiento.TipoMovimiento.ENTRADA);
        movimiento.setUsuario(usuario);
        movimiento.setEstado(Movimiento.EstadoMovimiento.PENDIENTE);
        
        // Guardar movimiento
        Movimiento guardado = movimientoRepo.save(movimiento);
        
        // Procesar detalles
        for (MovimientoDetalle detalle : movimiento.getDetalles()) {
            procesarEntrada(detalle, guardado, usuario);
        }
        
        guardado.calcularTotales();
        return movimientoRepo.save(guardado);
    }
    
    // Crear movimiento de salida
    public Movimiento crearSalida(Movimiento movimiento, Usuario usuario) throws Exception {
        validarMovimiento(movimiento);
        
        movimiento.setCodigo(generarCodigo("SAL"));
        movimiento.setTipo(Movimiento.TipoMovimiento.SALIDA);
        movimiento.setUsuario(usuario);
        movimiento.setEstado(Movimiento.EstadoMovimiento.PENDIENTE);
        
        // Validar stock disponible
        for (MovimientoDetalle detalle : movimiento.getDetalles()) {
            Producto producto = detalle.getProducto();
            if (producto.getStockActual() < detalle.getCantidad()) {
                throw new Exception("Stock insuficiente para: " + producto.getNombre() +
                                  ". Stock actual: " + producto.getStockActual());
            }
        }
        
        // Guardar movimiento
        Movimiento guardado = movimientoRepo.save(movimiento);
        
        // Procesar detalles
        for (MovimientoDetalle detalle : movimiento.getDetalles()) {
            procesarSalida(detalle, guardado, usuario);
        }
        
        guardado.calcularTotales();
        return movimientoRepo.save(guardado);
    }
    
    // Aprobar movimiento
    public Movimiento aprobar(Integer movimientoId, Usuario usuario) throws Exception {
        Movimiento movimiento = movimientoRepo.findById(movimientoId)
                .orElseThrow(() -> new Exception("Movimiento no encontrado"));
        
        if (movimiento.getEstado() != Movimiento.EstadoMovimiento.PENDIENTE) {
            throw new Exception("Solo se pueden aprobar movimientos pendientes");
        }
        
        movimiento.aprobar(usuario);
        return movimientoRepo.save(movimiento);
    }
    
    // Completar movimiento
    public Movimiento completar(Integer movimientoId) throws Exception {
        Movimiento movimiento = movimientoRepo.findById(movimientoId)
                .orElseThrow(() -> new Exception("Movimiento no encontrado"));
        
        if (movimiento.getEstado() != Movimiento.EstadoMovimiento.APROBADO) {
            throw new Exception("El movimiento debe estar aprobado");
        }
        
        movimiento.completar();
        return movimientoRepo.save(movimiento);
    }
    
    // Anular movimiento
    public Movimiento anular(Integer movimientoId) throws Exception {
        Movimiento movimiento = movimientoRepo.findById(movimientoId)
                .orElseThrow(() -> new Exception("Movimiento no encontrado"));
        
        if (movimiento.getEstado() == Movimiento.EstadoMovimiento.COMPLETADO) {
            throw new Exception("No se puede anular un movimiento completado");
        }
        
        // Revertir cambios en stock
        revertirMovimiento(movimiento);
        
        movimiento.anular();
        return movimientoRepo.save(movimiento);
    }
    
    // Procesar entrada al inventario
    private void procesarEntrada(MovimientoDetalle detalle, Movimiento movimiento, 
                                 Usuario usuario) throws Exception {
        Producto producto = detalle.getProducto();
        Integer stockAnterior = producto.getStockActual();
        
        // Actualizar stock
        productoService.aumentarStock(producto.getId(), detalle.getCantidad());
        
        // Registrar en Kardex
        Kardex kardex = new Kardex();
        kardex.setProducto(producto);
        kardex.setMovimiento(movimiento);
        kardex.setTipoOperacion(Kardex.TipoOperacion.ENTRADA);
        kardex.setCantidad(detalle.getCantidad());
        kardex.setSaldoAnterior(stockAnterior);
        kardex.setSaldoNuevo(stockAnterior + detalle.getCantidad());
        kardex.setPrecioUnitario(detalle.getPrecioUnitario());
        kardex.setValorTotal(detalle.getSubtotal());
        kardex.setUsuario(usuario);
        
        kardexRepo.save(kardex);
    }
    
    // Procesar salida del inventario
    private void procesarSalida(MovimientoDetalle detalle, Movimiento movimiento, 
                               Usuario usuario) throws Exception {
        Producto producto = detalle.getProducto();
        Integer stockAnterior = producto.getStockActual();
        
        // Actualizar stock
        productoService.disminuirStock(producto.getId(), detalle.getCantidad());
        
        // Registrar en Kardex
        Kardex kardex = new Kardex();
        kardex.setProducto(producto);
        kardex.setMovimiento(movimiento);
        kardex.setTipoOperacion(Kardex.TipoOperacion.SALIDA);
        kardex.setCantidad(detalle.getCantidad());
        kardex.setSaldoAnterior(stockAnterior);
        kardex.setSaldoNuevo(stockAnterior - detalle.getCantidad());
        kardex.setPrecioUnitario(detalle.getPrecioUnitario());
        kardex.setValorTotal(detalle.getSubtotal());
        kardex.setUsuario(usuario);
        
        kardexRepo.save(kardex);
    }
    
    // Revertir movimiento anulado
    private void revertirMovimiento(Movimiento movimiento) throws Exception {
        for (MovimientoDetalle detalle : movimiento.getDetalles()) {
            if (movimiento.getTipo() == Movimiento.TipoMovimiento.ENTRADA) {
                // Revertir entrada = disminuir stock
                productoService.disminuirStock(
                    detalle.getProducto().getId(), 
                    detalle.getCantidad()
                );
            } else if (movimiento.getTipo() == Movimiento.TipoMovimiento.SALIDA) {
                // Revertir salida = aumentar stock
                productoService.aumentarStock(
                    detalle.getProducto().getId(), 
                    detalle.getCantidad()
                );
            }
        }
    }
    
    // Generar código único
    private String generarCodigo(String prefijo) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        return prefijo + "-" + timestamp.substring(timestamp.length() - 8);
    }
    
    // Validar movimiento
    private void validarMovimiento(Movimiento movimiento) throws Exception {
        if (movimiento.getDetalles() == null || movimiento.getDetalles().isEmpty()) {
            throw new Exception("El movimiento debe tener al menos un detalle");
        }
        
        for (MovimientoDetalle detalle : movimiento.getDetalles()) {
            if (detalle.getCantidad() == null || detalle.getCantidad() <= 0) {
                throw new Exception("La cantidad debe ser mayor a 0");
            }
            if (detalle.getPrecioUnitario() == null || detalle.getPrecioUnitario() <= 0) {
                throw new Exception("El precio unitario debe ser mayor a 0");
            }
        }
    }
}