package com.markys.markys.repository;

import com.markys.markys.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    @Query("SELECT DATE(p.fecha), SUM(p.total) FROM Pedido p WHERE p.estado = 'PAGADO' GROUP BY DATE(p.fecha) ORDER BY DATE(p.fecha)")
    List<Object[]> obtenerTotalesPorDia();
}

