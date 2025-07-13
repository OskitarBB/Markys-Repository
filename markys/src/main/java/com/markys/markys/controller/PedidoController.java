/*
package com.markys.markys.controller;

import com.markys.markys.dto.PedidoRequestDTO;
import com.markys.markys.model.Pedido;
import com.markys.markys.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @PostMapping
    public ResponseEntity<?> crearPedido(@RequestBody PedidoRequestDTO pedidoDTO) {
        Pedido pedido = pedidoService.registrarPedido(pedidoDTO.getItems(), pedidoDTO.getTotal());
        return ResponseEntity.ok(pedido.getId());
    }
}
*/