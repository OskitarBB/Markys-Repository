package com.markys.markys.service;

import com.markys.markys.dto.PedidoItemDTO;
import com.markys.markys.model.DetallePedido;
import com.markys.markys.model.Pedido;
import com.markys.markys.model.Platillo;
import com.markys.markys.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    public Pedido registrarPedido(List<PedidoItemDTO> items, BigDecimal total) {
        Pedido pedido = new Pedido();
        pedido.setFecha(LocalDateTime.now());
        pedido.setTotal(total);
        pedido.setEstado("PENDIENTE");
        pedido.setMetodoEntrega("recojo");
        pedido.setDireccionEntrega("Cal. Pérez Figuerola 265 - A media cuadra de Plaza de Pisco");

        List<DetallePedido> detalles = new ArrayList<>();
        for (PedidoItemDTO item : items) {
            DetallePedido detalle = new DetallePedido();
            detalle.setPlatillo(new Platillo(item.getPlatilloId()));
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(item.getPrecioUnitario());
            detalle.setPedido(pedido); // enlace bidireccional
            detalles.add(detalle);
        }

        pedido.setDetalles(detalles);
        return pedidoRepository.save(pedido);
    }
}