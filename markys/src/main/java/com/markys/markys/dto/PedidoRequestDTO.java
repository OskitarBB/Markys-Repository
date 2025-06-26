package com.markys.markys.dto;

import java.math.BigDecimal;
import java.util.List;

public class PedidoRequestDTO {
    private List<PedidoItemDTO> items;
    private BigDecimal total;

    public List<PedidoItemDTO> getItems() { return items; }
    public void setItems(List<PedidoItemDTO> items) { this.items = items; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
}