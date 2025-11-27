package com.inventario.service;

import com.inventario.model.Almacen;
import com.inventario.model.InventarioAlmacen;
import com.inventario.repository.AlmacenRepository;
import com.inventario.repository.InventarioAlmacenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AlmacenService {

    @Autowired
    private AlmacenRepository repository;

    @Autowired
    private InventarioAlmacenRepository inventarioRepo;

    public List<Almacen> listarTodos() {
        return repository.findAll();
    }

    public List<Almacen> listarActivos() {
        return repository.findByActivoTrue();
    }

    public Optional<Almacen> obtenerPorId(Integer id) {
        return repository.findById(id);
    }

    public Optional<Almacen> obtenerPorCodigo(String codigo) {
        return repository.findByCodigo(codigo);
    }

    public Almacen crear(Almacen almacen) throws Exception {
        validarAlmacen(almacen);

        if (repository.findByCodigo(almacen.getCodigo()).isPresent()) {
            throw new Exception("El código ya existe");
        }

        return repository.save(almacen);
    }

    public Almacen actualizar(Integer id, Almacen almacen) throws Exception {
        Almacen existente = repository.findById(id)
                .orElseThrow(() -> new Exception("Almacén no encontrado"));

        // ✅ CORRECCIÓN: Validar código solo si cambió
        if (almacen.getCodigo() != null && !almacen.getCodigo().equals(existente.getCodigo())) {
            Optional<Almacen> codigoExistente = repository.findByCodigo(almacen.getCodigo());
            if (codigoExistente.isPresent() && !codigoExistente.get().getId().equals(id)) {
                throw new Exception("El código ya está registrado");
            }
            existente.setCodigo(almacen.getCodigo());
        }

        if (almacen.getNombre() != null) {
            existente.setNombre(almacen.getNombre());
        }
        if (almacen.getDescripcion() != null) {
            existente.setDescripcion(almacen.getDescripcion());
        }
        if (almacen.getDireccion() != null) {
            existente.setDireccion(almacen.getDireccion());
        }
        if (almacen.getCiudad() != null) {
            existente.setCiudad(almacen.getCiudad());
        }
        if (almacen.getPais() != null) {
            existente.setPais(almacen.getPais());
        }
        if (almacen.getCapacidadMaxima() != null) {
            existente.setCapacidadMaxima(almacen.getCapacidadMaxima());
        }
        if (almacen.getUsuarioResponsable() != null) {
            existente.setUsuarioResponsable(almacen.getUsuarioResponsable());
        }
        if (almacen.getActivo() != null) {
            existente.setActivo(almacen.getActivo());
        }

        return repository.save(existente);
    }

    public void eliminar(Integer id) throws Exception {
        Almacen almacen = repository.findById(id)
                .orElseThrow(() -> new Exception("Almacén no encontrado"));

        almacen.setActivo(false);
        repository.save(almacen);
    }

    public List<InventarioAlmacen> obtenerInventario(Integer almacenId) {
        return inventarioRepo.findByAlmacenId(almacenId);
    }

    public List<InventarioAlmacen> obtenerStockBajo(Integer almacenId) {
        return inventarioRepo.findStockBajoEnAlmacen(almacenId);
    }

    public List<Almacen> buscar(String termino) {
        return repository.buscar(termino);
    }

    private void validarAlmacen(Almacen almacen) throws Exception {
        if (almacen.getCodigo() == null || almacen.getCodigo().trim().isEmpty()) {
            throw new Exception("El código es obligatorio");
        }
        if (almacen.getNombre() == null || almacen.getNombre().trim().isEmpty()) {
            throw new Exception("El nombre es obligatorio");
        }
    }
}
