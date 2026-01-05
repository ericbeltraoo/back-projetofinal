// ========== ItemPedido.java ==========
package com.projetofinal.sistemapedidos.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "itens_pedido")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemPedido {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;
    
    @ManyToOne
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;
    
    @Column(nullable = false)
    private Integer quantity;
    
    @Column(name = "price_unit", nullable = false, precision = 10, scale = 2)
    private BigDecimal priceUnit;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;
    
    @PrePersist
    @PreUpdate
    protected void calcularSubtotal() {
        if (priceUnit != null && quantity != null) {
            this.subtotal = priceUnit.multiply(BigDecimal.valueOf(quantity));
        }
    }
}