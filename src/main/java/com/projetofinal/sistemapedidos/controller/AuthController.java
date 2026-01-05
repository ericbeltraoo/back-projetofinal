// ========== AuthController.java ==========
package com.projetofinal.sistemapedidos.controller;

import com.projetofinal.sistemapedidos.dto.*;
import com.projetofinal.sistemapedidos.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    @Autowired
    private UsuarioService usuarioService;
    
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        try {
            UsuarioDTO usuario = usuarioService.autenticar(request.getEmail(), request.getPassword());
            
            if (usuario != null) {
                return ResponseEntity.ok(new LoginResponse(true, "Login realizado com sucesso", usuario));
            } else {
                return ResponseEntity.ok(new LoginResponse(false, "Email ou senha incorretos", null));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(new LoginResponse(false, "Erro ao realizar login", null));
        }
    }
}