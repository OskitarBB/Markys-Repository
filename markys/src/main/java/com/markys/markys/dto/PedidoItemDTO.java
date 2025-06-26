package com.markys.markys.dto;

import java.math.BigDecimal;

public class PedidoItemDTO {
    private Long platilloId;
    private int cantidad;
    private BigDecimal precioUnitario;

    // Getters y Setters
    public Long getPlatilloId() { return platilloId; }
    public void setPlatilloId(Long platilloId) { this.platilloId = platilloId; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }
}