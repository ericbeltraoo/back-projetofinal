// ========== ProdutoController.java ==========
package com.projetofinal.sistemapedidos.controller;

import com.projetofinal.sistemapedidos.dto.ProdutoDTO;
import com.projetofinal.sistemapedidos.entity.Produto;
import com.projetofinal.sistemapedidos.service.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/produtos")
@CrossOrigin(origins = "*")
public class ProdutoController {
    
    @Autowired
    private ProdutoService produtoService;
    
    @GetMapping
    public ResponseEntity<List<ProdutoDTO>> listarTodos() {
        return ResponseEntity.ok(produtoService.listarTodos());
    }
    
    @GetMapping("/disponiveis")
    public ResponseEntity<List<ProdutoDTO>> listarDisponiveis() {
        return ResponseEntity.ok(produtoService.listarDisponiveis());
    }
    
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<ProdutoDTO>> buscarPorCategoria(@PathVariable String categoria) {
        return ResponseEntity.ok(produtoService.buscarPorCategoria(categoria));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ProdutoDTO> buscarPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(produtoService.buscarPorId(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping
    public ResponseEntity<ProdutoDTO> criar(@RequestBody Produto produto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(produtoService.criar(produto));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ProdutoDTO> atualizar(@PathVariable Long id, @RequestBody Produto produto) {
        try {
            return ResponseEntity.ok(produtoService.atualizar(id, produto));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PatchMapping("/{id}/estoque")
    public ResponseEntity<ProdutoDTO> atualizarEstoque(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        try {
            Integer novoEstoque = body.get("stock");
            return ResponseEntity.ok(produtoService.atualizarEstoque(id, novoEstoque));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        produtoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}