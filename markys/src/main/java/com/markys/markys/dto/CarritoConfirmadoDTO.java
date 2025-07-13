package com.markys.markys.dto;

import java.math.BigDecimal;
import java.util.List;

public class CarritoConfirmadoDTO {
    private List<CarritoItemDTO> productos;
    private BigDecimal total;
    private String metodoEntrega;
    private String direccionEntrega;

    public List<CarritoItemDTO> getProductos() {
        return productos;
    }

    public void setProductos(List<CarritoItemDTO> productos) {
        this.productos = productos;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getMetodoEntrega() {
        return metodoEntrega;
    }

    public void setMetodoEntrega(String metodoEntrega) {
        this.metodoEntrega = metodoEntrega;
    }

    public String getDireccionEntrega() {
        return direccionEntrega;
    }

    public void setDireccionEntrega(String direccionEntrega) {
        this.direccionEntrega = direccionEntrega;
    }
}