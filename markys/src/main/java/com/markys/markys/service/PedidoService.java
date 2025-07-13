package com.markys.markys.service;

import com.markys.markys.dto.CarritoConfirmadoDTO;
import com.markys.markys.dto.CarritoItemDTO;
import com.markys.markys.model.DetallePedido;
import com.markys.markys.model.Pedido;
import com.markys.markys.model.Platillo;
import com.markys.markys.repository.PedidoRepository;
import com.markys.markys.repository.PlatilloRepository;
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

    public Pedido registrarPedidoDesdeNombres(CarritoConfirmadoDTO dto, PlatilloRepository platilloRepository) {
        Pedido pedido = new Pedido();
        pedido.setFecha(LocalDateTime.now());
        pedido.setTotal(dto.getTotal());
        pedido.setEstado("PAGADO");
        pedido.setMetodoEntrega("recojo");
        pedido.setDireccionEntrega("Cal. Pérez Figuerola 265 - A media cuadra de Plaza de Pisco");

        List<DetallePedido> detalles = new ArrayList<>();
        for (CarritoItemDTO item : dto.getProductos()) {
            List<Platillo> coincidencias = platilloRepository.buscarPorNombre(item.getNombre());
            if (coincidencias.isEmpty()) {
                throw new RuntimeException("Platillo no encontrado: " + item.getNombre());
            }

            Platillo platillo = coincidencias.get(0);
            DetallePedido detalle = new DetallePedido();
            detalle.setPedido(pedido);
            detalle.setPlatillo(platillo);
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(item.getPrecio());

            detalles.add(detalle);
            System.out.println("[BACKEND] Producto: " + item.getNombre() + ", cantidad: " + item.getCantidad());
        }

        pedido.setDetalles(detalles);
        System.out.println("[BACKEND] Guardando pedido con " + detalles.size() + " detalles.");
        return pedidoRepository.save(pedido);
    }
}