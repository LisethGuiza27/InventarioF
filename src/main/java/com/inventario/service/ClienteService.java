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
        
        // Inicializar valores por defecto si son null
        if (cliente.getDiasCredito() == null) cliente.setDiasCredito(0);
        if (cliente.getLimiteCredito() == null) cliente.setLimiteCredito(0.0);
        if (cliente.getSaldoActual() == null) cliente.setSaldoActual(0.0);
        if (cliente.getDescuentoGeneral() == null) cliente.setDescuentoGeneral(0.0);
        if (cliente.getPais() == null || cliente.getPais().isEmpty()) cliente.setPais("México");
        
        return repository.save(cliente);
    }

    public Cliente actualizar(Integer id, Cliente cliente) throws Exception {
        Cliente existente = repository.findById(id)
                .orElseThrow(() -> new Exception("Cliente no encontrado"));
        
        if (cliente.getNombre() != null) existente.setNombre(cliente.getNombre());
        if (cliente.getNombreComercial() != null) existente.setNombreComercial(cliente.getNombreComercial());
        if (cliente.getRfc() != null) existente.setRfc(cliente.getRfc());
        if (cliente.getDireccion() != null) existente.setDireccion(cliente.getDireccion());
        if (cliente.getTelefono() != null) existente.setTelefono(cliente.getTelefono());
        if (cliente.getCelular() != null) existente.setCelular(cliente.getCelular());
        if (cliente.getEmail() != null) existente.setEmail(cliente.getEmail());
        if (cliente.getCiudad() != null) existente.setCiudad(cliente.getCiudad());
        if (cliente.getEstado() != null) existente.setEstado(cliente.getEstado());
        if (cliente.getCodigoPostal() != null) existente.setCodigoPostal(cliente.getCodigoPostal());
        if (cliente.getPais() != null) existente.setPais(cliente.getPais());
        if (cliente.getDiasCredito() != null) existente.setDiasCredito(cliente.getDiasCredito());
        if (cliente.getLimiteCredito() != null) existente.setLimiteCredito(cliente.getLimiteCredito());
        if (cliente.getDescuentoGeneral() != null) existente.setDescuentoGeneral(cliente.getDescuentoGeneral());
        if (cliente.getNotas() != null) existente.setNotas(cliente.getNotas());
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