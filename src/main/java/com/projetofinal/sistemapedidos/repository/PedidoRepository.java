// ========== PedidoRepository.java ==========
package com.projetofinal.sistemapedidos.repository;

import com.projetofinal.sistemapedidos.entity.Pedido;
import com.projetofinal.sistemapedidos.entity.enums.StatusPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByUsuarioId(Long usuarioId);
    List<Pedido> findByStatus(StatusPedido status);
    Optional<Pedido> findByPickupCode(String pickupCode);
    List<Pedido> findByOrderByCreatedAtDesc();
}