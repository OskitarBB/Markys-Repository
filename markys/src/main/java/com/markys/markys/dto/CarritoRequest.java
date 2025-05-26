package com.markys.markys.dto;

import java.util.List;

public class CarritoRequest {
    private List<CarritoItemDTO> productos;

    public List<CarritoItemDTO> getProductos() {
        return productos;
    }

    public void setProductos(List<CarritoItemDTO> productos) {
        this.productos = productos;
    }
}

