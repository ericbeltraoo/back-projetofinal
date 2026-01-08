// ========== UsuarioDTO.java ==========
package com.projetofinal.sistemapedidos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {
    private Long id;
    private String name;
    private String email;
    private String password;
    private String role; // String para receber o .name() do Enum
    private String className;
    private BigDecimal balance;
    private String phone;
    private String registration;

    // Construtor manual corrigido
    public UsuarioDTO(Long id, String name, String email, String role, String className, BigDecimal balance, String phone, String registration) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.className = className;
        this.balance = balance;
        this.phone = phone;
        this.registration = registration; // CORREÇÃO: attribution correta
    }
}