// ========== PedidoDTO.java ==========
package com.projetofinal.sistemapedidos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoDTO {
    private Long id;
    private Long userId;
    private String userName;
    private String userPhone;
    private List<ItemPedidoDTO> items;
    private BigDecimal total;
    private String status;
    private String paymentMethod;
    private LocalDateTime createdAt;
    private String pickupCode;
}