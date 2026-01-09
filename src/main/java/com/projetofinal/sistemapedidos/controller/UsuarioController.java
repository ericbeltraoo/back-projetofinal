package com.projetofinal.sistemapedidos.controller;

import com.projetofinal.sistemapedidos.dto.UsuarioDTO;
import com.projetofinal.sistemapedidos.entity.Usuario;
import com.projetofinal.sistemapedidos.entity.Produto; // Certifique-se que o nome da classe é Produto
import com.projetofinal.sistemapedidos.repository.UsuarioRepository;
import com.projetofinal.sistemapedidos.repository.ProdutoRepository; // Importante
import com.projetofinal.sistemapedidos.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProdutoRepository produtoRepository; // Injeção do repositório de produtos

    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> listarTodos() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> buscarPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(usuarioService.buscarPorId(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UsuarioDTO> buscarPorEmail(@PathVariable String email) {
        try {
            return ResponseEntity.ok(usuarioService.buscarPorEmail(email));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<UsuarioDTO> criar(@RequestBody Usuario usuario) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.criar(usuario));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> atualizar(@PathVariable Long id, @RequestBody Usuario usuario) {
        try {
            return ResponseEntity.ok(usuarioService.atualizar(id, usuario));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        usuarioService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    // =======================================================
    // ADICIONAR SALDO
    // =======================================================
    @PostMapping("/{id}/adicionar-saldo")
    public ResponseEntity<?> adicionarSaldo(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        try {
            Object amountObj = payload.get("amount");
            if (amountObj == null) {
                return ResponseEntity.badRequest().body("Valor não informado");
            }

            BigDecimal valorParaAdicionar = new BigDecimal(amountObj.toString());

            if (valorParaAdicionar.compareTo(BigDecimal.ZERO) <= 0) {
                return ResponseEntity.badRequest().body("Valor inválido");
            }

            Usuario user = usuarioRepository.findById(id).orElse(null);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }

            BigDecimal saldoAtual = user.getBalance() != null ? user.getBalance() : BigDecimal.ZERO;
            BigDecimal novoSaldo = saldoAtual.add(valorParaAdicionar);

            user.setBalance(novoSaldo);
            usuarioRepository.save(user);

            return ResponseEntity.ok(user);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Erro: " + e.getMessage());
        }
    }

    // =======================================================
    // NOVO: TOGGLE FAVORITOS (Adicionar/Remover)
    // =======================================================
    @PostMapping("/{userId}/favorites/{produtoId}")
    public ResponseEntity<?> toggleFavorito(@PathVariable Long userId, @PathVariable Long produtoId) {
        try {
            // 1. Buscar Usuario
            Optional<Usuario> usuarioOpt = usuarioRepository.findById(userId);
            if (usuarioOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado");
            }
            Usuario usuario = usuarioOpt.get();

            // 2. Buscar Produto
            Optional<Produto> produtoOpt = produtoRepository.findById(produtoId);
            if (produtoOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Produto não encontrado");
            }
            Produto produto = produtoOpt.get();

            // 3. Lógica de Adicionar ou Remover
            if (usuario.getFavoritos().contains(produto)) {
                usuario.getFavoritos().remove(produto);
                System.out.println("Produto removido dos favoritos: " + produto.getName()); // Supondo que Produto tem getName()
            } else {
                usuario.getFavoritos().add(produto);
                System.out.println("Produto adicionado aos favoritos: " + produto.getName());
            }

            // 4. Salvar
            usuarioRepository.save(usuario);

            // 5. Retornar o usuário atualizado (com a lista nova)
            return ResponseEntity.ok(usuario);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Erro ao atualizar favoritos: " + e.getMessage());
        }
    }
}