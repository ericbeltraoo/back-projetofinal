package com.projetofinal.sistemapedidos.service;

import com.projetofinal.sistemapedidos.dto.ProdutoDTO;
import com.projetofinal.sistemapedidos.entity.Produto;
import com.projetofinal.sistemapedidos.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    public List<ProdutoDTO> listarTodos() {
        return produtoRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ProdutoDTO> listarDisponiveis() {
        return produtoRepository.findByAvailableTrue()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ProdutoDTO> buscarPorCategoria(String categoria) {
        return produtoRepository.findByCategory(categoria)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ProdutoDTO buscarPorId(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
        return convertToDTO(produto);
    }

    @Transactional
    public ProdutoDTO criar(Produto produto) {
        if (produto.getStock() == null) produto.setStock(0);
        produto.setAvailable(produto.getStock() > 0);
        Produto salvo = produtoRepository.save(produto);
        return convertToDTO(salvo);
    }

    @Transactional
    public ProdutoDTO atualizar(Long id, Produto dados) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        produto.setName(dados.getName());
        produto.setDescription(dados.getDescription());
        produto.setPrice(dados.getPrice());
        produto.setCategory(dados.getCategory());
        produto.setImage(dados.getImage());
        produto.setStock(dados.getStock());
        produto.setAvailable(dados.getStock() > 0);

        return convertToDTO(produtoRepository.save(produto));
    }

    @Transactional
    public ProdutoDTO atualizarEstoque(Long id, Integer novoEstoque) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
        produto.setStock(novoEstoque);
        produto.setAvailable(novoEstoque > 0);
        return convertToDTO(produtoRepository.save(produto));
    }

    @Transactional
    public void deletar(Long id) {
        produtoRepository.deleteById(id);
    }

    // --- MÉTODOS DE CONVERSÃO ---

    public ProdutoDTO convertToDTO(Produto produto) {
        if (produto == null) return null;
        return new ProdutoDTO(
                produto.getId(),
                produto.getName(),
                produto.getDescription(),
                produto.getPrice(),
                produto.getCategory(),
                produto.getImage(),
                produto.getAvailable(),
                produto.getStock()
        );
    }

    // CORREÇÃO AQUI: Usando os getters gerados pelo @Data (Lombok)
    public Produto convertToEntity(ProdutoDTO dto) {
        if (dto == null) return null;
        Produto produto = new Produto();
        produto.setId(dto.getId());
        produto.setName(dto.getName());
        produto.setDescription(dto.getDescription());
        produto.setPrice(dto.getPrice());
        produto.setCategory(dto.getCategory());
        produto.setImage(dto.getImage());
        produto.setAvailable(dto.getAvailable());
        produto.setStock(dto.getStock());
        return produto;
    }
}