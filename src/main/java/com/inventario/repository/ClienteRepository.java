package com.inventario.repository;

import com.inventario.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
    Optional<Cliente> findByCodigo(String codigo);
    Optional<Cliente> findByNumeroDocumento(String numeroDocumento);
    List<Cliente> findByActivoTrue();
    
    @Query("SELECT c FROM Cliente c WHERE c.nombre LIKE %:termino% OR c.codigo LIKE %:termino%")
    List<Cliente> buscar(String termino);
    
    @Query("SELECT c FROM Cliente c WHERE c.saldoPendiente > 0")
    List<Cliente> findConSaldoPendiente();
    
    @Query("SELECT c FROM Cliente c WHERE c.fechaUltimaCompra >= :fechaInicio")
    List<Cliente> findClientesActivos(LocalDateTime fechaInicio);
}