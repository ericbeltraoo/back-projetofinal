// ========== ProdutoDTO.java ==========
package com.projetofinal.sistemapedidos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProdutoDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String category;
    private String image;
    private Boolean available;
    private Integer stock;
}