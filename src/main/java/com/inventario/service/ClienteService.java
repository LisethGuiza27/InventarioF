package com.inventario.service;

import com.inventario.model.Cliente;
import com.inventario.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ClienteService {

    @Autowired
    private ClienteRepository repository;

    public List<Cliente> listarTodos() {
        return repository.findAll();
    }

    public List<Cliente> listarActivos() {
        return repository.findByActivoTrue();
    }

    public Optional<Cliente> obtenerPorId(Integer id) {
        return repository.findById(id);
    }

    public Optional<Cliente> obtenerPorCodigo(String codigo) {
        return repository.findByCodigo(codigo);
    }

    public Cliente crear(Cliente cliente) throws Exception {
        validarCliente(cliente);
        
        if (repository.findByCodigo(cliente.getCodigo()).isPresent()) {
            throw new Exception("El código ya existe");
        }
        
        if (cliente.getNumeroDocumento() != null) {
            if (repository.findByNumeroDocumento(cliente.getNumeroDocumento()).isPresent()) {
                throw new Exception("El número de documento ya está registrado");
            }
        }
        
        return repository.save(cliente);
    }

    public Cliente actualizar(Integer id, Cliente cliente) throws Exception {
        Cliente existente = repository.findById(id)
                .orElseThrow(() -> new Exception("Cliente no encontrado"));
        
        if (cliente.getNombre() != null) existente.setNombre(cliente.getNombre());
        if (cliente.getTipoDocumento() != null) existente.setTipoDocumento(cliente.getTipoDocumento());
        if (cliente.getNumeroDocumento() != null) existente.setNumeroDocumento(cliente.getNumeroDocumento());
        if (cliente.getDireccion() != null) existente.setDireccion(cliente.getDireccion());
        if (cliente.getTelefono() != null) existente.setTelefono(cliente.getTelefono());
        if (cliente.getEmail() != null) existente.setEmail(cliente.getEmail());
        if (cliente.getCiudad() != null) existente.setCiudad(cliente.getCiudad());
        if (cliente.getPais() != null) existente.setPais(cliente.getPais());
        if (cliente.getDiasCredito() != null) existente.setDiasCredito(cliente.getDiasCredito());
        if (cliente.getLimiteCredito() != null) existente.setLimiteCredito(cliente.getLimiteCredito());
        if (cliente.getActivo() != null) existente.setActivo(cliente.getActivo());
        
        return repository.save(existente);
    }

    public void eliminar(Integer id) throws Exception {
        Cliente cliente = repository.findById(id)
                .orElseThrow(() -> new Exception("Cliente no encontrado"));
        
        cliente.setActivo(false);
        repository.save(cliente);
    }

    public List<Cliente> buscar(String termino) {
        return repository.buscar(termino);
    }

    public List<Cliente> obtenerConSaldoPendiente() {
        return repository.findConSaldoPendiente();
    }

    private void validarCliente(Cliente cliente) throws Exception {
        if (cliente.getCodigo() == null || cliente.getCodigo().trim().isEmpty()) {
            throw new Exception("El código es obligatorio");
        }
        if (cliente.getNombre() == null || cliente.getNombre().trim().isEmpty()) {
            throw new Exception("El nombre es obligatorio");
        }
    }
}