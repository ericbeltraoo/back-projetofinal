// ========== CriarPedidoRequest.java ==========
package com.projetofinal.sistemapedidos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CriarPedidoRequest {
    private Long userId;
    private List<ItemPedidoRequest> items;
    private String paymentMethod;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemPedidoRequest {
        private Long productId;
        private Integer quantity;
    }
}