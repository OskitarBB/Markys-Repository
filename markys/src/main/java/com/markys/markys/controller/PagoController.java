package com.markys.markys.controller;

import com.markys.markys.dto.CarritoRequest;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.*;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.resources.preference.Preference;
import com.mercadopago.exceptions.MPException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pago")
public class PagoController {

    public PagoController() {
        MercadoPagoConfig.setAccessToken("TU_ACCESS_TOKEN"); // Cambiar esto por el token real
    }

    @PostMapping("/crear-preferencia")
    public ResponseEntity<String> crearPreferencia(@RequestBody CarritoRequest carrito)
            throws MPException, MPApiException {
        List<PreferenceItemRequest> items = carrito.getProductos().stream().map(p ->
                PreferenceItemRequest.builder()
                        .title(p.getNombre())
                        .quantity(p.getCantidad())
                        .unitPrice(p.getPrecio())
                        .currencyId("PEN")
                        .build()
        ).toList();

        PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                .success("http://localhost:8080/success")
                .failure("http://localhost:8080/failure")
                .pending("http://localhost:8080/pending")
                .build();

        PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                .items(items)
                .backUrls(backUrls)
                .autoReturn("approved")
                .build();

        PreferenceClient client = new PreferenceClient();
        Preference preference = client.create(preferenceRequest);

        return ResponseEntity.ok(preference.getInitPoint()); // URL para redirigir al pago
    }
}
