package com.markys.markys.controller;

import com.markys.markys.dto.CarritoItemDTO;
import com.markys.markys.dto.CarritoRequest;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.*;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.resources.preference.Preference;
import com.mercadopago.exceptions.MPException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/pago")
public class PagoController {

    public PagoController() {
        MercadoPagoConfig.setAccessToken("APP_USR-6246925132439479-062001-5f12cdc1c38747034ed29c6ef1a49f1f-2509538210"); // Token de mercadoPago
    }

    @PostMapping("/preferencia")
    public ResponseEntity<?> crearPreferencia(@RequestBody CarritoRequest carrito)
            throws MPException, MPApiException {
        System.out.println("[DEBUG] JSON recibido en preferencia: " + carrito);
        if (carrito == null || carrito.getProductos() == null) {
            return ResponseEntity.badRequest().body("Error: Carrito vacío o productos no enviados");
        }
        /*
        //Prueba para la pasarela
        List<PreferenceItemRequest> items = List.of(
                PreferenceItemRequest.builder()
                        .title("Producto de prueba")
                        .quantity(1)
                        .unitPrice(BigDecimal.valueOf(10.0))
                        .currencyId("PEN")
                        .build()
        );

         */
        List<PreferenceItemRequest> items = carrito.getProductos().stream()
                .filter(p -> p.getNombre() != null && p.getPrecio() != null)
                .map(p ->
                        PreferenceItemRequest.builder()
                                .title(p.getNombre())
                                .quantity(p.getCantidad())
                                .unitPrice(BigDecimal.valueOf(p.getPrecio().doubleValue()))
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
                //.autoReturn("approved")
                .build();

        try {
            PreferenceClient client = new PreferenceClient();
            Preference preference = client.create(preferenceRequest);
            // Retornamos el ID de preferencia en formato JSON para usarlo en el frontend
            return ResponseEntity.ok("{\"preferenceId\": \"" + preference.getId() + "\"}");
        } catch (MPApiException apiEx) {
            System.out.println("[ERROR] MercadoPago API error:");
            System.out.println("Status Code: " + apiEx.getStatusCode());
            System.out.println("Message: " + apiEx.getApiResponse().getContent());
            return ResponseEntity.status(apiEx.getStatusCode())
                    .body("MercadoPago API error: " + apiEx.getApiResponse().getContent());
        } catch (MPException ex) {
            System.out.println("[ERROR] Error general de MercadoPago: " + ex.getMessage());
            return ResponseEntity.status(500)
                    .body("Error general de MercadoPago: " + ex.getMessage());
        }
    }
}
