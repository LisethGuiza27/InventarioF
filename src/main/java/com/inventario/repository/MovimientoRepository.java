package com.inventario.repository;

import com.inventario.model.Movimiento;
import com.inventario.model.Movimiento.TipoMovimiento;
import com.inventario.model.Movimiento.EstadoMovimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MovimientoRepository extends JpaRepository<Movimiento, Integer> {
    Optional<Movimiento> findByCodigo(String codigo);
    List<Movimiento> findByTipo(TipoMovimiento tipo);
    List<Movimiento> findByEstado(EstadoMovimiento estado);
    
    @Query("SELECT m FROM Movimiento m WHERE m.fechaMovimiento BETWEEN :inicio AND :fin")
    List<Movimiento> findByFechaBetween(@Param("inicio") LocalDateTime inicio, 
                                        @Param("fin") LocalDateTime fin);
    
    @Query("SELECT m FROM Movimiento m WHERE DATE(m.fechaMovimiento) = CURRENT_DATE")
    List<Movimiento> findMovimientosHoy();
    
    @Query("SELECT m FROM Movimiento m ORDER BY m.fechaMovimiento DESC")
    List<Movimiento> findAllOrderByFechaDesc();
    
    @Query("SELECT m FROM Movimiento m WHERE m.almacen.id = :almacenId")
    List<Movimiento> findByAlmacenId(@Param("almacenId") Integer almacenId);
    
    @Query("SELECT COUNT(m) FROM Movimiento m WHERE DATE(m.fechaMovimiento) = CURRENT_DATE")
    long countMovimientosHoy();
}
