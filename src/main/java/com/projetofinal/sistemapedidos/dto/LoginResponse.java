// ========== LoginResponse.java ==========
package com.projetofinal.sistemapedidos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private Boolean success;
    private String message;
    private UsuarioDTO user;
}