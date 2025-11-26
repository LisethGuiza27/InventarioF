package com.inventario.service;

import com.inventario.model.Proveedor;
import com.inventario.repository.ProveedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProveedorService {

    @Autowired
    private ProveedorRepository repository;

    public List<Proveedor> listarTodos() {
        return repository.findAll();
    }

    public List<Proveedor> listarActivos() {
        return repository.findByActivoTrue();
    }

    public Optional<Proveedor> obtenerPorId(Integer id) {
        return repository.findById(id);
    }

    public Optional<Proveedor> obtenerPorCodigo(String codigo) {
        return repository.findByCodigo(codigo);
    }

    public Proveedor crear(Proveedor proveedor) throws Exception {
        validarProveedor(proveedor);
        
        if (repository.findByCodigo(proveedor.getCodigo()).isPresent()) {
            throw new Exception("El código ya existe");
        }
        
        if (proveedor.getRuc() != null) {
            if (repository.findByRuc(proveedor.getRuc()).isPresent()) {
                throw new Exception("El RUC ya está registrado");
            }
        }
        
        return repository.save(proveedor);
    }

    public Proveedor actualizar(Integer id, Proveedor proveedor) throws Exception {
        Proveedor existente = repository.findById(id)
                .orElseThrow(() -> new Exception("Proveedor no encontrado"));
        
        if (proveedor.getNombre() != null) existente.setNombre(proveedor.getNombre());
        if (proveedor.getRazonSocial() != null) existente.setRazonSocial(proveedor.getRazonSocial());
        if (proveedor.getRuc() != null) existente.setRuc(proveedor.getRuc());
        if (proveedor.getDireccion() != null) existente.setDireccion(proveedor.getDireccion());
        if (proveedor.getTelefono() != null) existente.setTelefono(proveedor.getTelefono());
        if (proveedor.getEmail() != null) existente.setEmail(proveedor.getEmail());
        if (proveedor.getPersonaContacto() != null) existente.setPersonaContacto(proveedor.getPersonaContacto());
        if (proveedor.getCiudad() != null) existente.setCiudad(proveedor.getCiudad());
        if (proveedor.getPais() != null) existente.setPais(proveedor.getPais());
        if (proveedor.getDiasCredito() != null) existente.setDiasCredito(proveedor.getDiasCredito());
        if (proveedor.getLimiteCredito() != null) existente.setLimiteCredito(proveedor.getLimiteCredito());
        if (proveedor.getActivo() != null) existente.setActivo(proveedor.getActivo());
        
        return repository.save(existente);
    }

    public void eliminar(Integer id) throws Exception {
        Proveedor proveedor = repository.findById(id)
                .orElseThrow(() -> new Exception("Proveedor no encontrado"));
        
        proveedor.setActivo(false);
        repository.save(proveedor);
    }

    public List<Proveedor> buscar(String termino) {
        return repository.buscar(termino);
    }

    public List<Proveedor> obtenerConSaldoPendiente() {
        return repository.findConSaldoPendiente();
    }

    private void validarProveedor(Proveedor proveedor) throws Exception {
        if (proveedor.getCodigo() == null || proveedor.getCodigo().trim().isEmpty()) {
            throw new Exception("El código es obligatorio");
        }
        if (proveedor.getNombre() == null || proveedor.getNombre().trim().isEmpty()) {
            throw new Exception("El nombre es obligatorio");
        }
    }
}