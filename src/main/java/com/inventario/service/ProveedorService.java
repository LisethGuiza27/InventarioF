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

        if (proveedor.getRfc() != null && !proveedor.getRfc().trim().isEmpty()) {
            if (repository.findByRfc(proveedor.getRfc()).isPresent()) {
                throw new Exception("El RFC ya está registrado");
            }
        }

        // Inicializar valores por defecto
        if (proveedor.getDiasCredito() == null) {
            proveedor.setDiasCredito(0);
        }
        if (proveedor.getLimiteCredito() == null) {
            proveedor.setLimiteCredito(0.0);
        }
        if (proveedor.getSaldoPendiente() == null) {
            proveedor.setSaldoPendiente(0.0);
        }
        if (proveedor.getCalificacion() == null) {
            proveedor.setCalificacion(5);
        }
        if (proveedor.getPais() == null || proveedor.getPais().isEmpty()) {
            proveedor.setPais("Colombia");
        }
        if (proveedor.getActivo() == null) {
            proveedor.setActivo(true);
        }

        return repository.save(proveedor);
    }

    public Proveedor actualizar(Integer id, Proveedor proveedor) throws Exception {
        Proveedor existente = repository.findById(id)
                .orElseThrow(() -> new Exception("Proveedor no encontrado"));

        // Validar código solo si cambió y no está vacío
        if (proveedor.getCodigo() != null && !proveedor.getCodigo().trim().isEmpty()
                && !proveedor.getCodigo().equals(existente.getCodigo())) {
            Optional<Proveedor> codigoExistente = repository.findByCodigo(proveedor.getCodigo());
            if (codigoExistente.isPresent() && !codigoExistente.get().getId().equals(id)) {
                throw new Exception("El código ya está registrado");
            }
            existente.setCodigo(proveedor.getCodigo());
        }

        // Validar RFC solo si cambió y no está vacío
        if (proveedor.getRfc() != null && !proveedor.getRfc().trim().isEmpty()
                && !proveedor.getRfc().equals(existente.getRfc())) {
            Optional<Proveedor> rfcExistente = repository.findByRfc(proveedor.getRfc());
            if (rfcExistente.isPresent() && !rfcExistente.get().getId().equals(id)) {
                throw new Exception("El RFC ya está registrado");
            }
            existente.setRfc(proveedor.getRfc());
        }

        // ✅ CORRECCIÓN: Solo actualizar si el valor no es null Y no está vacío
        if (proveedor.getRazonSocial() != null && !proveedor.getRazonSocial().trim().isEmpty()) {
            existente.setRazonSocial(proveedor.getRazonSocial());
        }
        if (proveedor.getNombreComercial() != null && !proveedor.getNombreComercial().trim().isEmpty()) {
            existente.setNombreComercial(proveedor.getNombreComercial());
        }
        if (proveedor.getDireccion() != null && !proveedor.getDireccion().trim().isEmpty()) {
            existente.setDireccion(proveedor.getDireccion());
        }
        if (proveedor.getTelefono() != null && !proveedor.getTelefono().trim().isEmpty()) {
            existente.setTelefono(proveedor.getTelefono());
        }
        if (proveedor.getCelular() != null && !proveedor.getCelular().trim().isEmpty()) {
            existente.setCelular(proveedor.getCelular());
        }
        if (proveedor.getEmail() != null && !proveedor.getEmail().trim().isEmpty()) {
            existente.setEmail(proveedor.getEmail());
        }
        if (proveedor.getCiudad() != null && !proveedor.getCiudad().trim().isEmpty()) {
            existente.setCiudad(proveedor.getCiudad());
        }
        if (proveedor.getEstado() != null && !proveedor.getEstado().trim().isEmpty()) {
            existente.setEstado(proveedor.getEstado());
        }
        if (proveedor.getCodigoPostal() != null && !proveedor.getCodigoPostal().trim().isEmpty()) {
            existente.setCodigoPostal(proveedor.getCodigoPostal());
        }
        if (proveedor.getPais() != null && !proveedor.getPais().trim().isEmpty()) {
            existente.setPais(proveedor.getPais());
        }
        if (proveedor.getContactoNombre() != null && !proveedor.getContactoNombre().trim().isEmpty()) {
            existente.setContactoNombre(proveedor.getContactoNombre());
        }
        if (proveedor.getContactoCargo() != null && !proveedor.getContactoCargo().trim().isEmpty()) {
            existente.setContactoCargo(proveedor.getContactoCargo());
        }
        if (proveedor.getContactoEmail() != null && !proveedor.getContactoEmail().trim().isEmpty()) {
            existente.setContactoEmail(proveedor.getContactoEmail());
        }
        if (proveedor.getContactoTelefono() != null && !proveedor.getContactoTelefono().trim().isEmpty()) {
            existente.setContactoTelefono(proveedor.getContactoTelefono());
        }
        if (proveedor.getNotas() != null && !proveedor.getNotas().trim().isEmpty()) {
            existente.setNotas(proveedor.getNotas());
        }

        // Para campos numéricos, actualizar siempre
        if (proveedor.getDiasCredito() != null) {
            existente.setDiasCredito(proveedor.getDiasCredito());
        }
        if (proveedor.getLimiteCredito() != null) {
            existente.setLimiteCredito(proveedor.getLimiteCredito());
        }
        if (proveedor.getCalificacion() != null) {
            existente.setCalificacion(proveedor.getCalificacion());
        }
        if (proveedor.getActivo() != null) {
            existente.setActivo(proveedor.getActivo());
        }

        return repository.save(existente);
    }

    // ✅ CORRECCIÓN: Método para eliminar permanentemente
    public void eliminar(Integer id) throws Exception {
        Proveedor proveedor = repository.findById(id)
                .orElseThrow(() -> new Exception("Proveedor no encontrado"));

        // Eliminar permanentemente de la base de datos
        repository.delete(proveedor);
    }

    // Método adicional para desactivar (soft delete)
    public void desactivar(Integer id) throws Exception {
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
