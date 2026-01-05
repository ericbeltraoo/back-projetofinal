// ========== ItemPedidoDTO.java ==========
package com.projetofinal.sistemapedidos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemPedidoDTO {
    private Long id;
    private ProdutoDTO product;
    private Integer quantity;
    private BigDecimal priceUnit;
    private BigDecimal subtotal;
}