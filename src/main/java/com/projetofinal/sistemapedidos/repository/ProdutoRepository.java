// ========== ProdutoRepository.java ==========
package com.projetofinal.sistemapedidos.repository;

import com.projetofinal.sistemapedidos.entity.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    List<Produto> findByCategory(String category);
    List<Produto> findByAvailableTrue();
    List<Produto> findByNameContainingIgnoreCase(String name);
}