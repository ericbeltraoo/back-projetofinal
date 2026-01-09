// ========== PedidoController.java ==========
package com.projetofinal.sistemapedidos.controller;

import com.projetofinal.sistemapedidos.dto.CriarPedidoRequest;
import com.projetofinal.sistemapedidos.dto.PedidoDTO;
import com.projetofinal.sistemapedidos.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "*")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @GetMapping
    public ResponseEntity<List<PedidoDTO>> listarTodos() {
        return ResponseEntity.ok(pedidoService.listarTodos());
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<PedidoDTO>> listarPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(pedidoService.listarPorUsuario(usuarioId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<PedidoDTO>> listarPorStatus(@PathVariable String status) {
        return ResponseEntity.ok(pedidoService.listarPorStatus(status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoDTO> buscarPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(pedidoService.buscarPorId(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<PedidoDTO> buscarPorCodigoRetirada(@PathVariable String codigo) {
        try {
            return ResponseEntity.ok(pedidoService.buscarPorCodigoRetirada(codigo));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody CriarPedidoRequest request) {
        try {
            PedidoDTO pedido = pedidoService.criar(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(pedido);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<PedidoDTO> atualizarStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            String novoStatus = body.get("status");
            String motivo = body.get("reason");
            return ResponseEntity.ok(pedidoService.atualizarStatus(id, novoStatus, motivo));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Endpoint corrigido para evitar erro de compilação de subclassing no catch.
     */
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelarPedido(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            String motivo = body.get("reason");
            PedidoDTO pedido = pedidoService.cancelarPedido(id, motivo);
            return ResponseEntity.ok(pedido);
        } catch (IllegalStateException e) {
            // Erros específicos de regra de negócio (ex: status inválido para cancelar)
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (RuntimeException e) {
            // Outros erros de execução (ex: banco de dados, saldo, estoque)
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            // Erros genéricos
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erro inesperado ao processar o cancelamento."));
        }
    }
}