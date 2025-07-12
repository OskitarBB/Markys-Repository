package com.markys.markys.repository;

import com.markys.markys.model.DetallePedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Long> {

    @Query("SELECT d.platillo.nombre, SUM(d.cantidad) FROM DetallePedido d GROUP BY d.platillo.nombre")
    List<Object[]> contarPedidosPorPlatillo();
}


