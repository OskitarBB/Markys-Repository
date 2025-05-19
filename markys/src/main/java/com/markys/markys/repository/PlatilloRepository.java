package com.markys.markys.repository;

import com.markys.markys.model.Platillo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface PlatilloRepository extends JpaRepository<Platillo, Long> {

    @Query("SELECT p FROM Platillo p WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<Platillo> buscarPorNombre(@Param("nombre") String nombre);
}
