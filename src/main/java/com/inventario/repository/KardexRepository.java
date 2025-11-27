package com.inventario.repository;

import com.inventario.model.Kardex;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface KardexRepository extends JpaRepository<Kardex, Integer> {

    List<Kardex> findByProductoId(Integer productoId);

    List<Kardex> findByAlmacenId(Integer almacenId);

    @Query("SELECT k FROM Kardex k WHERE k.producto.id = :productoId "
            + "ORDER BY k.fechaOperacion DESC")
    List<Kardex> findByProductoIdOrderByFechaDesc(@Param("productoId") Integer productoId);

    @Query("SELECT k FROM Kardex k WHERE k.producto.id = :productoId "
            + "AND k.fechaOperacion BETWEEN :inicio AND :fin")
    List<Kardex> findByProductoAndFecha(@Param("productoId") Integer productoId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);

    @Query("SELECT k FROM Kardex k WHERE k.almacen.id = :almacenId "
            + "AND k.fechaOperacion BETWEEN :inicio AND :fin")
    List<Kardex> findByAlmacenAndFecha(@Param("almacenId") Integer almacenId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);
}
