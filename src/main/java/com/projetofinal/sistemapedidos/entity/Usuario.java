package com.projetofinal.sistemapedidos.entity;

import com.projetofinal.sistemapedidos.entity.enums.RoleUsuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleUsuario role;

    @Column(name = "class_name", length = 50)
    private String className;

    @Column(precision = 10, scale = 2)
    private BigDecimal balance;

    @Column(length = 20)
    private String phone;

    @Column(unique = true, length = 50)
    private String registration;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // --- NOVO: LISTA DE FAVORITOS ---
    @ManyToMany
    @JoinTable(
            name = "usuario_favoritos", // Nome da tabela intermediária
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "produto_id")
    )
    private Set<Produto> favoritos = new HashSet<>();
    // Usei Set para evitar duplicatas (não favoritar o mesmo item 2x)

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (balance == null) {
            balance = BigDecimal.ZERO;
        }
    }
}