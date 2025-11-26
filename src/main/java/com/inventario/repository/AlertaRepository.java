package com.inventario.repository;

import com.inventario.model.Alerta;
import com.inventario.model.Alerta.TipoAlerta;
import com.inventario.model.Alerta.PrioridadAlerta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AlertaRepository extends JpaRepository<Alerta, Integer> {
    List<Alerta> findByActivaTrueAndLeidaFalse();
    List<Alerta> findByProductoId(Integer productoId);
    List<Alerta> findByTipo(TipoAlerta tipo);
    List<Alerta> findByPrioridad(PrioridadAlerta prioridad);
    
    @Query("SELECT a FROM Alerta a WHERE a.activa = true AND a.leida = false " +
           "ORDER BY a.prioridad DESC, a.createdAt DESC")
    List<Alerta> findAlertasActivasNoLeidas();
    
    @Query("SELECT a FROM Alerta a WHERE a.activa = true AND a.leida = false " +
           "AND a.prioridad IN ('ALTA', 'CRITICA') ORDER BY a.createdAt DESC")
    List<Alerta> findAlertasCriticas();
    
    @Query("SELECT COUNT(a) FROM Alerta a WHERE a.activa = true AND a.leida = false")
    long countAlertasActivas();
}
