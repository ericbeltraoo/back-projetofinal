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
import java.util.ArrayList;
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
        pedido.setItems(new ArrayList<>());

        for (CriarPedidoRequest.ItemPedidoRequest itemReq : request.getItems()) {
            Produto produto = produtoRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new RuntimeException("Produto ID " + itemReq.getProductId() + " não encontrado"));

            if (produto.getStock() < itemReq.getQuantity()) {
                throw new RuntimeException("Estoque insuficiente para: " + produto.getName());
            }

            ItemPedido item = new ItemPedido();
            item.setPedido(pedido);
            item.setProduto(produto);
            item.setQuantity(itemReq.getQuantity());

            BigDecimal precoUnitario = (produto.getPrice() != null) ? produto.getPrice() : BigDecimal.ZERO;
            item.setPriceUnit(precoUnitario);

            pedido.getItems().add(item);

            int novoEstoque = produto.getStock() - itemReq.getQuantity();
            produto.setStock(novoEstoque);
            produto.setAvailable(novoEstoque > 0);
            produtoRepository.save(produto);
        }

        pedido.calcularTotal();

        if (pedido.getPaymentMethod() == MetodoPagamento.BALANCE) {
            BigDecimal saldoAtual = (usuario.getBalance() != null) ? usuario.getBalance() : BigDecimal.ZERO;
            if (saldoAtual.compareTo(pedido.getTotal()) < 0) {
                throw new RuntimeException("Saldo insuficiente.");
            }
            usuario.setBalance(saldoAtual.subtract(pedido.getTotal()));
            usuarioRepository.save(usuario);
        }

        Pedido salvo = pedidoRepository.save(pedido);
        return convertToDTO(salvo);
    }

    // ATUALIZADO: Agora suporta receber o motivo
    @Transactional
    public PedidoDTO atualizarStatus(Long id, String novoStatus, String motivo) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        if ("CANCELLED".equalsIgnoreCase(novoStatus)) {
            return cancelarPedido(id, motivo);
        }

        pedido.setStatus(StatusPedido.valueOf(novoStatus.toUpperCase()));
        Pedido salvo = pedidoRepository.save(pedido);
        return convertToDTO(salvo);
    }

    // NOVO MÉTODO: Centraliza a lógica de cancelamento, estorno e devolução de estoque
    @Transactional
    public PedidoDTO cancelarPedido(Long id, String motivo) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        if (pedido.getStatus() != StatusPedido.PENDING) {
            throw new RuntimeException("Apenas pedidos pendentes podem ser cancelados.");
        }

        // 1. Estorno de Saldo
        if (pedido.getPaymentMethod() == MetodoPagamento.BALANCE) {
            Usuario usuario = pedido.getUsuario();
            BigDecimal saldoAtual = (usuario.getBalance() != null) ? usuario.getBalance() : BigDecimal.ZERO;
            usuario.setBalance(saldoAtual.add(pedido.getTotal()));
            usuarioRepository.save(usuario);
        }

        // 2. Devolver Itens ao Estoque
        for (ItemPedido item : pedido.getItems()) {
            Produto produto = item.getProduto();
            produto.setStock(produto.getStock() + item.getQuantity());
            produto.setAvailable(true);
            produtoRepository.save(produto);
        }

        // 3. Atualizar Status e Motivo
        pedido.setStatus(StatusPedido.CANCELLED);
        pedido.setCancelReason(motivo);

        Pedido salvo = pedidoRepository.save(pedido);
        return convertToDTO(salvo);
    }

    private PedidoDTO convertToDTO(Pedido pedido) {
        List<ItemPedidoDTO> itemsDTO = pedido.getItems().stream()
                .map(item -> new ItemPedidoDTO(
                        item.getId(),
                        produtoService.convertToDTO(item.getProduto()),
                        item.getQuantity(),
                        item.getPriceUnit(),
                        (item.getSubtotal() != null) ? item.getSubtotal() : BigDecimal.ZERO
                ))
                .collect(Collectors.toList());

        // Adicionado o pedido.getCancelReason() no final do construtor do DTO
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
                pedido.getPickupCode(),
                pedido.getCancelReason()
        );
    }
}