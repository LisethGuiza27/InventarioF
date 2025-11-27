package com.inventario.repository;

import com.inventario.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {

    Optional<Categoria> findByCodigo(String codigo);

    List<Categoria> findByActivoTrue();

    List<Categoria> findByCategoriaPadreIsNull();

    List<Categoria> findByCategoriaPadreId(Integer categoriaPadreId);

    @Query("SELECT c FROM Categoria c WHERE c.nombre LIKE %:nombre%")
    List<Categoria> buscarPorNombre(String nombre);
}
