package com.projetofinal.sistemapedidos.entity;

import com.projetofinal.sistemapedidos.entity.enums.MetodoPagamento;
import com.projetofinal.sistemapedidos.entity.enums.StatusPedido;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedidos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "user_name", nullable = false, length = 100)
    private String userName;

    @Column(name = "user_phone", length = 20)
    private String userPhone;

    // CascadeType.ALL garante que ao salvar o Pedido, os itens sejam salvos automaticamente
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ItemPedido> items = new ArrayList<>();

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPedido status;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private MetodoPagamento paymentMethod;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "pickup_code", length = 4)
    private String pickupCode;

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = StatusPedido.PENDING;
        }
        // Gera código de retirada dos últimos 4 dígitos do telefone
        if (this.userPhone != null && this.userPhone.length() >= 4) {
            this.pickupCode = this.userPhone.substring(this.userPhone.length() - 4);
        } else {
            // Caso o telefone seja curto ou nulo, gera um código baseado no timestamp
            this.pickupCode = String.valueOf(System.currentTimeMillis()).substring(9);
        }
    }

    /**
     * Calcula o total do pedido somando o subtotal de cada item.
     * Implementação robusta para evitar NullPointerException e erro de 'augend'.
     */
    public void calcularTotal() {
        if (this.items == null || this.items.isEmpty()) {
            this.total = BigDecimal.ZERO;
            return;
        }

        this.total = this.items.stream()
                .map(item -> {
                    BigDecimal sub = item.getSubtotal();
                    return (sub != null) ? sub : BigDecimal.ZERO;
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Método utilitário para adicionar itens garantindo a relação bidirecional
    public void adicionarItem(ItemPedido item) {
        items.add(item);
        item.setPedido(this);
    }
}