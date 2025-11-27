package com.inventario.service;

import com.inventario.model.Categoria;
import com.inventario.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CategoriaService {

    @Autowired
    private CategoriaRepository repository;

    public List<Categoria> listarTodos() {
        return repository.findAll();
    }

    public List<Categoria> listarActivos() {
        return repository.findByActivoTrue();
    }

    public Optional<Categoria> obtenerPorId(Integer id) {
        return repository.findById(id);
    }

    public Optional<Categoria> obtenerPorCodigo(String codigo) {
        return repository.findByCodigo(codigo);
    }

    public Categoria crear(Categoria categoria) throws Exception {
        validarCategoria(categoria);

        if (repository.findByCodigo(categoria.getCodigo()).isPresent()) {
            throw new Exception("El código ya existe");
        }

        return repository.save(categoria);
    }

    public Categoria actualizar(Integer id, Categoria categoria) throws Exception {
        Categoria existente = repository.findById(id)
                .orElseThrow(() -> new Exception("Categoría no encontrada"));

        if (categoria.getNombre() != null) {
            existente.setNombre(categoria.getNombre());
        }
        if (categoria.getDescripcion() != null) {
            existente.setDescripcion(categoria.getDescripcion());
        }
        if (categoria.getIcono() != null) {
            existente.setIcono(categoria.getIcono());
        }
        if (categoria.getColor() != null) {
            existente.setColor(categoria.getColor());
        }
        if (categoria.getOrden() != null) {
            existente.setOrden(categoria.getOrden());
        }
        if (categoria.getActivo() != null) {
            existente.setActivo(categoria.getActivo());
        }

        return repository.save(existente);
    }

    public void eliminar(Integer id) throws Exception {
        Categoria categoria = repository.findById(id)
                .orElseThrow(() -> new Exception("Categoría no encontrada"));

        categoria.setActivo(false);
        repository.save(categoria);
    }

    public List<Categoria> obtenerCategoriasPrincipales() {
        return repository.findByCategoriaPadreIsNull();
    }

    public List<Categoria> obtenerSubcategorias(Integer categoriaPadreId) {
        return repository.findByCategoriaPadreId(categoriaPadreId);
    }

    public List<Categoria> buscarPorNombre(String nombre) {
        return repository.buscarPorNombre(nombre);
    }

    private void validarCategoria(Categoria categoria) throws Exception {
        if (categoria.getCodigo() == null || categoria.getCodigo().trim().isEmpty()) {
            throw new Exception("El código es obligatorio");
        }
        if (categoria.getNombre() == null || categoria.getNombre().trim().isEmpty()) {
            throw new Exception("El nombre es obligatorio");
        }
    }
}
