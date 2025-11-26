package com.inventario.repository;

import com.inventario.model.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Integer> {
    Optional<Proveedor> findByCodigo(String codigo);
    Optional<Proveedor> findByRuc(String ruc);
    List<Proveedor> findByActivoTrue();
    
    @Query("SELECT p FROM Proveedor p WHERE p.nombre LIKE %:termino% OR p.codigo LIKE %:termino%")
    List<Proveedor> buscar(String termino);
    
    @Query("SELECT p FROM Proveedor p WHERE p.saldoPendiente > 0")
    List<Proveedor> findConSaldoPendiente();
}
