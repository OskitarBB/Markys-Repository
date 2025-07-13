package com.markys.markys.controller;

import com.markys.markys.dto.CarritoRequest;
import com.markys.markys.dto.CarritoConfirmadoDTO;
import com.markys.markys.model.Pedido;
import com.markys.markys.repository.PlatilloRepository;
import com.markys.markys.service.PedidoService;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.*;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.preference.Preference;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pago")
public class PagoController {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private PlatilloRepository platilloRepository;

    @PostConstruct
    public void configurarMercadoPago() {
        MercadoPagoConfig.setAccessToken("APP_USR-6246925132439479-062001-5f12cdc1c38747034ed29c6ef1a49f1f-2509538210");
    }

    @PostMapping("/preferencia")
    public ResponseEntity<?> crearPreferencia(@RequestBody CarritoRequest carrito)
            throws MPException, MPApiException {

        if (carrito == null || carrito.getProductos() == null) {
            return ResponseEntity.badRequest().body("Error: Carrito vacío o productos no enviados");
        }

        List<PreferenceItemRequest> items = carrito.getProductos().stream()
                .filter(p -> p.getNombre() != null && p.getPrecio() != null)
                .map(p -> PreferenceItemRequest.builder()
                        .title(p.getNombre())
                        .quantity(p.getCantidad())
                        .unitPrice(BigDecimal.valueOf(p.getPrecio().doubleValue()))
                        .currencyId("PEN")
                        .build())
                .toList();

        PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                .success("http://localhost:8080/success")
                .failure("http://localhost:8080/failure")
                .pending("http://localhost:8080/pending")
                .build();

        PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                .items(items)
                .backUrls(backUrls)
                .build();

        PreferenceClient client = new PreferenceClient();
        Preference preference = client.create(preferenceRequest);

        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("init_point", preference.getInitPoint());
        return ResponseEntity.ok(respuesta);
    }

    @PostMapping("/confirmar")
    public ResponseEntity<String> confirmarPedido(@RequestBody CarritoConfirmadoDTO dto) {
        try {
            Pedido pedido = pedidoService.registrarPedidoDesdeNombres(dto, platilloRepository);
            return ResponseEntity.ok("Pedido guardado con éxito. ID: " + pedido.getId());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al guardar pedido: " + e.getMessage());
        }

    }
}