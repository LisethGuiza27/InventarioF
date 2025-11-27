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
    Optional<Proveedor> findByRfc(String rfc);
    List<Proveedor> findByActivoTrue();
    
    @Query("SELECT p FROM Proveedor p " +
       "WHERE LOWER(p.razonSocial) LIKE LOWER(CONCAT('%', :termino, '%')) " +
       "   OR LOWER(p.nombreComercial) LIKE LOWER(CONCAT('%', :termino, '%')) " +
       "   OR LOWER(p.codigo) LIKE LOWER(CONCAT('%', :termino, '%'))")
    List<Proveedor> buscar(String termino);
    
    @Query("SELECT p FROM Proveedor p WHERE p.saldoPendiente > 0")
    List<Proveedor> findConSaldoPendiente();

}
