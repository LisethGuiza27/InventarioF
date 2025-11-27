package com.inventario.repository;

import com.inventario.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    Optional<Producto> findByCodigo(String codigo);

    List<Producto> findByActivoTrue();

    @Query("SELECT p FROM Producto p WHERE p.activo = true AND p.stockActual <= p.stockMinimo")
    List<Producto> findProductosStockBajo();

    @Query("SELECT p FROM Producto p WHERE p.activo = true AND p.stockActual = 0")
    List<Producto> findProductosAgotados();

    @Query("SELECT p FROM Producto p WHERE p.activo = true AND p.fechaVencimiento <= :fecha")
    List<Producto> findProductosProximosVencer(@Param("fecha") LocalDate fecha);

    @Query("SELECT COUNT(p) FROM Producto p WHERE p.activo = true")
    long countProductosActivos();

    @Query("SELECT COUNT(p) FROM Producto p WHERE p.activo = true AND p.stockActual <= p.stockMinimo")
    long countProductosStockBajo();

    @Query("SELECT COUNT(p) FROM Producto p WHERE p.activo = true AND p.stockActual = 0")
    long countProductosAgotados();

    @Query("SELECT SUM(p.stockActual * p.precioVenta) FROM Producto p WHERE p.activo = true")
    Double calcularValorTotalInventario();

    @Query("SELECT p FROM Producto p WHERE p.nombre LIKE %:termino% OR p.codigo LIKE %:termino%")
    List<Producto> buscarPorTermino(@Param("termino") String termino);
}
