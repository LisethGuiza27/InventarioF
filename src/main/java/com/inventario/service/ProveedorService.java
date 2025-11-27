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
        
        if (proveedor.getRfc() != null) {
            if (repository.findByRfc(proveedor.getRfc()).isPresent()) {
                throw new Exception("El RFC ya está registrado");
            }
        }
        
        // Inicializar valores por defecto
        if (proveedor.getDiasCredito() == null) proveedor.setDiasCredito(0);
        if (proveedor.getLimiteCredito() == null) proveedor.setLimiteCredito(0.0);
        if (proveedor.getCalificacion() == null) proveedor.setCalificacion(5);
        if (proveedor.getPais() == null || proveedor.getPais().isEmpty()) proveedor.setPais("México");
        
        return repository.save(proveedor);
    }

    public Proveedor actualizar(Integer id, Proveedor proveedor) throws Exception {
        Proveedor existente = repository.findById(id)
                .orElseThrow(() -> new Exception("Proveedor no encontrado"));
        
        if (proveedor.getRazonSocial() != null) existente.setRazonSocial(proveedor.getRazonSocial());
        if (proveedor.getNombreComercial() != null) existente.setNombreComercial(proveedor.getNombreComercial());
        if (proveedor.getRfc() != null) existente.setRfc(proveedor.getRfc());
        if (proveedor.getDireccion() != null) existente.setDireccion(proveedor.getDireccion());
        if (proveedor.getTelefono() != null) existente.setTelefono(proveedor.getTelefono());
        if (proveedor.getCelular() != null) existente.setCelular(proveedor.getCelular());
        if (proveedor.getEmail() != null) existente.setEmail(proveedor.getEmail());
        if (proveedor.getCiudad() != null) existente.setCiudad(proveedor.getCiudad());
        if (proveedor.getEstado() != null) existente.setEstado(proveedor.getEstado());
        if (proveedor.getCodigoPostal() != null) existente.setCodigoPostal(proveedor.getCodigoPostal());
        if (proveedor.getPais() != null) existente.setPais(proveedor.getPais());
        if (proveedor.getContactoNombre() != null) existente.setContactoNombre(proveedor.getContactoNombre());
        if (proveedor.getContactoCargo() != null) existente.setContactoCargo(proveedor.getContactoCargo());
        if (proveedor.getContactoEmail() != null) existente.setContactoEmail(proveedor.getContactoEmail());
        if (proveedor.getContactoTelefono() != null) existente.setContactoTelefono(proveedor.getContactoTelefono());
        if (proveedor.getDiasCredito() != null) existente.setDiasCredito(proveedor.getDiasCredito());
        if (proveedor.getLimiteCredito() != null) existente.setLimiteCredito(proveedor.getLimiteCredito());
        if (proveedor.getCalificacion() != null) existente.setCalificacion(proveedor.getCalificacion());
        if (proveedor.getNotas() != null) existente.setNotas(proveedor.getNotas());
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
        if (proveedor.getRazonSocial() == null || proveedor.getRazonSocial().trim().isEmpty()) {
            throw new Exception("La razón social es obligatoria");
        }
    }
}