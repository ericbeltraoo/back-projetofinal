// ========== PedidoService.java ==========
package com.projetofinal.sistemapedidos.service;

import com.projetofinal.sistemapedidos.dto.*;
import com.projetofinal.sistemapedidos.entity.*;
import com.projetofinal.sistemapedidos.entity.enums.MetodoPagamento;
import com.projetofinal.sistemapedidos.entity.enums.StatusPedido;
import com.projetofinal.sistemapedidos.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PedidoService {
    
    @Autowired
    private PedidoRepository pedidoRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private ProdutoRepository produtoRepository;
    
    @Autowired
    private ProdutoService produtoService;
    
    public List<PedidoDTO> listarTodos() {
        return pedidoRepository.findByOrderByCreatedAtDesc().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<PedidoDTO> listarPorUsuario(Long usuarioId) {
        return pedidoRepository.findByUsuarioId(usuarioId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<PedidoDTO> listarPorStatus(String status) {
        StatusPedido statusEnum = StatusPedido.valueOf(status.toUpperCase());
        return pedidoRepository.findByStatus(statusEnum).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public PedidoDTO buscarPorId(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));
        return convertToDTO(pedido);
    }
    
    public PedidoDTO buscarPorCodigoRetirada(String codigo) {
        Pedido pedido = pedidoRepository.findByPickupCode(codigo)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));
        return convertToDTO(pedido);
    }
    
    @Transactional
    public PedidoDTO criar(CriarPedidoRequest request) {
        Usuario usuario = usuarioRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setUserName(usuario.getName());
        pedido.setUserPhone(usuario.getPhone());
        pedido.setStatus(StatusPedido.PENDING);
        pedido.setPaymentMethod(MetodoPagamento.valueOf(request.getPaymentMethod().toUpperCase()));
        
        // Adicionar itens
        for (CriarPedidoRequest.ItemPedidoRequest itemReq : request.getItems()) {
            Produto produto = produtoRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
            
            // Verificar estoque
            if (produto.getStock() < itemReq.getQuantity()) {
                throw new RuntimeException("Estoque insuficiente para: " + produto.getName());
            }
            
            ItemPedido item = new ItemPedido();
            item.setPedido(pedido);
            item.setProduto(produto);
            item.setQuantity(itemReq.getQuantity());
            item.setPrice​Unit(produto.getPrice());
            
            pedido.getItems().add(item);
            
            // Atualizar estoque
            produto.setStock(produto.getStock() - itemReq.getQuantity());
            produtoRepository.save(produto);
        }
        
        // Calcular total
        pedido.calcularTotal();
        
        // Validar saldo se pagamento for por balance
        if (pedido.getPaymentMethod() == MetodoPagamento.BALANCE) {
            if (usuario.getBalance().compareTo(pedido.getTotal()) < 0) {
                throw new RuntimeException("Saldo insuficiente");
            }
            // Debitar saldo
            usuario.setBalance(usuario.getBalance().subtract(pedido.getTotal()));
            usuarioRepository.save(usuario);
        }
        
        Pedido salvo = pedidoRepository.save(pedido);
        return convertToDTO(salvo);
    }
    
    @Transactional
    public PedidoDTO atualizarStatus(Long id, String novoStatus) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));
        
        pedido.setStatus(StatusPedido.valueOf(novoStatus.toUpperCase()));
        Pedido salvo = pedidoRepository.save(pedido);
        return convertToDTO(salvo);
    }
    
    private PedidoDTO convertToDTO(Pedido pedido) {
        List<ItemPedidoDTO> itemsDTO = pedido.getItems().stream()
                .map(item -> new ItemPedidoDTO(
                    item.getId(),
                    produtoService.convertToEntity(produtoService.buscarPorId(item.getProduto().getId()))
                        != null ? produtoService.buscarPorId(item.getProduto().getId()) : null,
                    item.getQuantity(),
                    item.getPriceUnit(),
                    item.getSubtotal()
                ))
                .collect(Collectors.toList());
        
        return new PedidoDTO(
            pedido.getId(),
            pedido.getUsuario().getId(),
            pedido.getUserName(),
            pedido.getUserPhone(),
            itemsDTO,
            pedido.getTotal(),
            pedido.getStatus().name().toLowerCase(),
            pedido.getPaymentMethod().name().toLowerCase(),
            pedido.getCreatedAt(),
            pedido.getPickupCode()
        );
    }
}