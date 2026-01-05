// ========== ProdutoService.java ==========
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
        return produtoRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<ProdutoDTO> listarDisponiveis() {
        return produtoRepository.findByAvailableTrue().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<ProdutoDTO> buscarPorCategoria(String categoria) {
        return produtoRepository.findByCategory(categoria).stream()
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
        Produto salvo = produtoRepository.save(produto);
        return convertToDTO(salvo);
    }
    
    @Transactional
    public ProdutoDTO atualizar(Long id, Produto produtoAtualizado) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
        
        produto.setName(produtoAtualizado.getName());
        produto.setDescription(produtoAtualizado.getDescription());
        produto.setPrice(produtoAtualizado.getPrice());
        produto.setCategory(produtoAtualizado.getCategory());
        produto.setImage(produtoAtualizado.getImage());
        produto.setAvailable(produtoAtualizado.getAvailable());
        produto.setStock(produtoAtualizado.getStock());
        
        Produto salvo = produtoRepository.save(produto);
        return convertToDTO(salvo);
    }
    
    @Transactional
    public ProdutoDTO atualizarEstoque(Long id, Integer novoEstoque) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
        
        produto.setStock(novoEstoque);
        produto.setAvailable(novoEstoque > 0);
        
        Produto salvo = produtoRepository.save(produto);
        return convertToDTO(salvo);
    }
    
    public void deletar(Long id) {
        produtoRepository.deleteById(id);
    }
    
    private ProdutoDTO convertToDTO(Produto produto) {
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
    
    Produto convertToEntity(ProdutoDTO dto) {
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