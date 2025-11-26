package com.inventario.service;

import com.inventario.model.Producto;
import com.inventario.model.Alerta;
import com.inventario.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductoService {

    @Autowired
    private ProductoRepository repository;
    
    @Autowired
    private AlertaService alertaService;

    // Listar todos
    public List<Producto> listarTodos() {
        return repository.findAll();
    }
    
    // Listar solo activos
    public List<Producto> listarActivos() {
        return repository.findAll().stream()
                .filter(Producto::getActivo)
                .collect(Collectors.toList());
    }

    // Obtener por ID
    public Optional<Producto> obtenerPorId(Integer id) {
        return repository.findById(id);
    }
    
    // Obtener por código
    public Optional<Producto> obtenerPorCodigo(String codigo) {
        return repository.findByCodigo(codigo);
    }

    // Crear producto
    public Producto crear(Producto p) throws Exception {
        validarProducto(p);
        
        // Verificar código único
        if (repository.findByCodigo(p.getCodigo()).isPresent()) {
            throw new Exception("El código '" + p.getCodigo() + "' ya existe");
        }
        
        // Inicializar valores por defecto
        if (p.getStockActual() == null) p.setStockActual(0);
        if (p.getStockMinimo() == null) p.setStockMinimo(0);
        if (p.getActivo() == null) p.setActivo(true);
        
        Producto productoGuardado = repository.save(p);
        
        // Verificar si necesita alerta
        verificarAlertas(productoGuardado);
        
        return productoGuardado;
    }

    // Actualizar producto
    public Producto actualizar(Integer id, Producto p) throws Exception {
        Optional<Producto> existente = repository.findById(id);
        if (!existente.isPresent()) {
            throw new Exception("Producto no encontrado con ID: " + id);
        }

        Producto producto = existente.get();
        
        // Actualizar solo campos modificables
        if (p.getNombre() != null) producto.setNombre(p.getNombre());
        if (p.getDescripcion() != null) producto.setDescripcion(p.getDescripcion());
        if (p.getPrecioVenta() != null) producto.setPrecioVenta(p.getPrecioVenta());
        if (p.getPrecioCompra() != null) producto.setPrecioCompra(p.getPrecioCompra());
        if (p.getStockMinimo() != null) producto.setStockMinimo(p.getStockMinimo());
        if (p.getStockMaximo() != null) producto.setStockMaximo(p.getStockMaximo());
        if (p.getCategoria() != null) producto.setCategoria(p.getCategoria());
        if (p.getProveedor() != null) producto.setProveedor(p.getProveedor());
        if (p.getActivo() != null) producto.setActivo(p.getActivo());

        Producto actualizado = repository.save(producto);
        verificarAlertas(actualizado);
        
        return actualizado;
    }
    
    // Actualizar stock
    public void actualizarStock(Integer productoId, Integer cantidadNueva) throws Exception {
        Producto producto = repository.findById(productoId)
                .orElseThrow(() -> new Exception("Producto no encontrado"));
        
        producto.setStockActual(cantidadNueva);
        repository.save(producto);
        verificarAlertas(producto);
    }
    
    // Aumentar stock
    public void aumentarStock(Integer productoId, Integer cantidad) throws Exception {
        Producto producto = repository.findById(productoId)
                .orElseThrow(() -> new Exception("Producto no encontrado"));
        
        producto.setStockActual(producto.getStockActual() + cantidad);
        repository.save(producto);
        verificarAlertas(producto);
    }
    
    // Disminuir stock
    public void disminuirStock(Integer productoId, Integer cantidad) throws Exception {
        Producto producto = repository.findById(productoId)
                .orElseThrow(() -> new Exception("Producto no encontrado"));
        
        if (producto.getStockActual() < cantidad) {
            throw new Exception("Stock insuficiente. Stock actual: " + producto.getStockActual());
        }
        
        producto.setStockActual(producto.getStockActual() - cantidad);
        repository.save(producto);
        verificarAlertas(producto);
    }

    // Eliminar producto (soft delete)
    public void eliminar(Integer id) throws Exception {
        Producto producto = repository.findById(id)
                .orElseThrow(() -> new Exception("Producto no encontrado"));
        
        producto.setActivo(false);
        repository.save(producto);
    }
    
    // Eliminar permanentemente
    public void eliminarPermanente(Integer id) throws Exception {
        if (!repository.existsById(id)) {
            throw new Exception("Producto no encontrado");
        }
        repository.deleteById(id);
    }
    
    // Buscar productos
    public List<Producto> buscar(String termino) {
        return repository.findAll().stream()
                .filter(p -> p.getNombre().toLowerCase().contains(termino.toLowerCase()) ||
                            p.getCodigo().toLowerCase().contains(termino.toLowerCase()))
                .collect(Collectors.toList());
    }
    
    // Productos con stock bajo
    public List<Producto> obtenerProductosStockBajo(int limite) {
        return repository.findAll().stream()
                .filter(p -> p.getActivo() && p.getStockActual() <= p.getStockMinimo())
                .limit(limite)
                .collect(Collectors.toList());
    }
    
    // Productos agotados
    public List<Producto> obtenerProductosAgotados() {
        return repository.findAll().stream()
                .filter(p -> p.getActivo() && p.getStockActual() == 0)
                .collect(Collectors.toList());
    }
    
    // Productos próximos a vencer
    public List<Producto> obtenerProductosProximosVencer(int dias) {
        LocalDate fechaLimite = LocalDate.now().plusDays(dias);
        return repository.findAll().stream()
                .filter(p -> p.getActivo() && 
                            p.getFechaVencimiento() != null &&
                            !p.getFechaVencimiento().isAfter(fechaLimite))
                .collect(Collectors.toList());
    }
    
    // Contar productos
    public long contarProductosActivos() {
        return repository.findAll().stream()
                .filter(Producto::getActivo)
                .count();
    }
    
    public long contarProductosStockBajo() {
        return repository.findAll().stream()
                .filter(p -> p.getActivo() && p.getStockActual() <= p.getStockMinimo())
                .count();
    }
    
    public long contarProductosAgotados() {
        return repository.findAll().stream()
                .filter(p -> p.getActivo() && p.getStockActual() == 0)
                .count();
    }
    
    // Calcular valor total del inventario
    public double calcularValorTotalInventario() {
        return repository.findAll().stream()
                .filter(Producto::getActivo)
                .mapToDouble(p -> p.getStockActual() * 
                               (p.getPrecioVenta() != null ? p.getPrecioVenta() : 0))
                .sum();
    }
    
    // Validaciones
    private void validarProducto(Producto p) throws Exception {
        if (p.getCodigo() == null || p.getCodigo().trim().length() < 3) {
            throw new Exception("El código debe tener al menos 3 caracteres");
        }
        if (p.getNombre() == null || p.getNombre().trim().length() < 3) {
            throw new Exception("El nombre debe tener al menos 3 caracteres");
        }
        if (p.getPrecioVenta() == null || p.getPrecioVenta() <= 0) {
            throw new Exception("El precio de venta debe ser mayor a 0");
        }
    }
    
    // Verificar alertas automáticas
    private void verificarAlertas(Producto producto) {
        if (!producto.getActivo()) return;
        
        // Alerta stock bajo
        if (producto.getStockActual() <= producto.getStockMinimo() && 
            producto.getStockActual() > 0) {
            alertaService.crearAlerta(
                Alerta.TipoAlerta.STOCK_BAJO,
                producto,
                "Stock bajo: " + producto.getStockActual() + " unidades"
            );
        }
        
        // Alerta stock agotado
        if (producto.getStockActual() == 0) {
            alertaService.crearAlerta(
                Alerta.TipoAlerta.STOCK_AGOTADO,
                producto,
                "Producto agotado"
            );
        }
        
        // Alerta vencimiento próximo (30 días)
        if (producto.getFechaVencimiento() != null) {
            LocalDate fechaLimite = LocalDate.now().plusDays(30);
            if (!producto.getFechaVencimiento().isAfter(fechaLimite)) {
                alertaService.crearAlerta(
                    Alerta.TipoAlerta.VENCIMIENTO_PROXIMO,
                    producto,
                    "Vence el: " + producto.getFechaVencimiento()
                );
            }
        }
    }
}