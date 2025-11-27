package com.inventario.repository;

import com.inventario.model.InventarioAlmacen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface InventarioAlmacenRepository extends JpaRepository<InventarioAlmacen, Integer> {

    Optional<InventarioAlmacen> findByProductoIdAndAlmacenId(Integer productoId, Integer almacenId);

    List<InventarioAlmacen> findByProductoId(Integer productoId);

    List<InventarioAlmacen> findByAlmacenId(Integer almacenId);

    @Query("SELECT ia FROM InventarioAlmacen ia WHERE ia.almacen.id = :almacenId "
            + "AND ia.cantidad < ia.producto.stockMinimo")
    List<InventarioAlmacen> findStockBajoEnAlmacen(@Param("almacenId") Integer almacenId);

    @Query("SELECT ia FROM InventarioAlmacen ia WHERE ia.cantidad = 0")
    List<InventarioAlmacen> findProductosAgotados();

    @Query("SELECT SUM(ia.cantidad) FROM InventarioAlmacen ia WHERE ia.producto.id = :productoId")
    Integer getTotalStockProducto(@Param("productoId") Integer productoId);
}
