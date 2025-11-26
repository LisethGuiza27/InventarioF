package com.inventario.repository;

import com.inventario.model.Almacen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AlmacenRepository extends JpaRepository<Almacen, Integer> {
    Optional<Almacen> findByCodigo(String codigo);
    List<Almacen> findByActivoTrue();
    List<Almacen> findByUsuarioResponsableId(Integer usuarioId);
    
    @Query("SELECT a FROM Almacen a WHERE a.nombre LIKE %:termino% OR a.codigo LIKE %:termino%")
    List<Almacen> buscar(String termino);
}
