
// ========== ItemPedidoRepository.java ==========
package com.projetofinal.sistemapedidos.repository;

import com.projetofinal.sistemapedidos.entity.ItemPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemPedidoRepository extends JpaRepository<ItemPedido, Long> {
}