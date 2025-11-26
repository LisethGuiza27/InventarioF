package com.inventario.service;

import com.inventario.model.Rol;
import com.inventario.repository.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RolService {

    @Autowired
    private RolRepository repository;

    public List<Rol> listarTodos() {
        return repository.findAll();
    }

    public List<Rol> listarActivos() {
        return repository.findByActivoTrue();
    }

    public Optional<Rol> obtenerPorId(Integer id) {
        return repository.findById(id);
    }

    public Optional<Rol> obtenerPorNombre(String nombre) {
        return repository.findByNombre(nombre);
    }

    public Rol crear(Rol rol) throws Exception {
        if (rol.getNombre() == null || rol.getNombre().trim().isEmpty()) {
            throw new Exception("El nombre del rol es obligatorio");
        }
        
        if (repository.findByNombre(rol.getNombre()).isPresent()) {
            throw new Exception("El rol ya existe");
        }
        
        return repository.save(rol);
    }

    public Rol actualizar(Integer id, Rol rol) throws Exception {
        Rol existente = repository.findById(id)
                .orElseThrow(() -> new Exception("Rol no encontrado"));
        
        if (rol.getDescripcion() != null) {
            existente.setDescripcion(rol.getDescripcion());
        }
        if (rol.getActivo() != null) {
            existente.setActivo(rol.getActivo());
        }
        
        return repository.save(existente);
    }

    public void eliminar(Integer id) throws Exception {
        Rol rol = repository.findById(id)
                .orElseThrow(() -> new Exception("Rol no encontrado"));
        
        // Verificar que no tenga usuarios asignados
        if (!rol.getUsuarios().isEmpty()) {
            throw new Exception("No se puede eliminar un rol con usuarios asignados");
        }
        
        rol.setActivo(false);
        repository.save(rol);
    }
}