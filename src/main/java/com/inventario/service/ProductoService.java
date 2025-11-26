package com.inventario.service;

import com.inventario.model.Producto;
import com.inventario.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository repository;

    // Listar todos
    public List<Producto> listarTodos() {
        return repository.findAll();
    }

    // Obtener por ID
    public Optional<Producto> obtenerPorId(Integer id) {
        return repository.findById(id);
    }

    // Crear
    public Producto crear(Producto p) throws Exception {
        // Validar
        if (p.getCodigo() == null || p.getCodigo().trim().length() < 3) {
            throw new Exception("Código debe tener al menos 3 caracteres");
        }
        if (p.getNombre() == null || p.getNombre().trim().length() < 5) {
            throw new Exception("Nombre debe tener al menos 5 caracteres");
        }
        if (p.getPrecio() == null || p.getPrecio() <= 0) {
            throw new Exception("Precio debe ser mayor a 0");
        }

        // Verificar código único
        if (repository.findByCodigo(p.getCodigo()).isPresent()) {
            throw new Exception("Código ya existe");
        }

        return repository.save(p);
    }

    // Actualizar
    public Producto actualizar(Integer id, Producto p) throws Exception {
        Optional<Producto> existente = repository.findById(id);
        if (!existente.isPresent()) {
            throw new Exception("Producto no encontrado");
        }

        Producto producto = existente.get();
        if (p.getNombre() != null) producto.setNombre(p.getNombre());
        if (p.getPrecio() != null) producto.setPrecio(p.getPrecio());
        if (p.getStock() != null) producto.setStock(p.getStock());
        if (p.getActivo() != null) producto.setActivo(p.getActivo());

        return repository.save(producto);
    }

    // Eliminar
    public void eliminar(Integer id) throws Exception {
        if (!repository.existsById(id)) {
            throw new Exception("Producto no encontrado");
        }
        repository.deleteById(id);
    }
}
