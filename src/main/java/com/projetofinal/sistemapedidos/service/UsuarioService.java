package com.projetofinal.sistemapedidos.service;

import com.projetofinal.sistemapedidos.dto.UsuarioDTO;
import com.projetofinal.sistemapedidos.entity.Usuario;
import com.projetofinal.sistemapedidos.entity.enums.RoleUsuario;
import com.projetofinal.sistemapedidos.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<UsuarioDTO> listarTodos() {
        return usuarioRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public UsuarioDTO buscarPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        return convertToDTO(usuario);
    }

    public UsuarioDTO buscarPorEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        return convertToDTO(usuario);
    }

    /**
     * Registrar um novo usuário vindo do formulário de cadastro (React).
     */
    @Transactional
    public UsuarioDTO registrar(UsuarioDTO dto) {
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Este e-mail já está cadastrado.");
        }

        Usuario novoUsuario = new Usuario();
        novoUsuario.setName(dto.getName());
        novoUsuario.setEmail(dto.getEmail());
        novoUsuario.setPassword(dto.getPassword());
        novoUsuario.setPhone(dto.getPhone());
        novoUsuario.setClassName(dto.getClassName());
        novoUsuario.setRegistration(dto.getRegistration());

        // Regra de saldo inicial: se não informado, começa com 0.00
        if (dto.getBalance() != null) {
            novoUsuario.setBalance(dto.getBalance());
        } else {
            novoUsuario.setBalance(BigDecimal.ZERO);
        }

        novoUsuario.setRole(RoleUsuario.STUDENT);

        Usuario salvo = usuarioRepository.save(novoUsuario);
        return convertToDTO(salvo);
    }

    /**
     * Método Criar: Utilizado para persistir a entidade Usuario diretamente.
     * Resolve o erro "cannot find symbol: method criar".
     */
    @Transactional
    public UsuarioDTO criar(Usuario usuario) {
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new RuntimeException("Email já cadastrado");
        }

        if (usuario.getBalance() == null) {
            usuario.setBalance(BigDecimal.ZERO);
        }

        Usuario salvo = usuarioRepository.save(usuario);
        return convertToDTO(salvo);
    }

    @Transactional
    public UsuarioDTO atualizar(Long id, Usuario usuarioAtualizado) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        usuario.setName(usuarioAtualizado.getName());
        usuario.setEmail(usuarioAtualizado.getEmail());
        usuario.setClassName(usuarioAtualizado.getClassName());
        usuario.setBalance(usuarioAtualizado.getBalance());
        usuario.setPhone(usuarioAtualizado.getPhone());
        usuario.setRegistration(usuarioAtualizado.getRegistration());

        Usuario salvo = usuarioRepository.save(usuario);
        return convertToDTO(salvo);
    }

    public UsuarioDTO autenticar(String email, String password) {
        Usuario usuario = usuarioRepository.findByEmailAndPassword(email, password)
                .orElse(null);
        return usuario != null ? convertToDTO(usuario) : null;
    }

    public void deletar(Long id) {
        usuarioRepository.deleteById(id);
    }

    /**
     * Converte a Entidade do Banco (Usuario) para o Objeto de Transferência (UsuarioDTO).
     * GARANTE que o saldo e a matrícula do banco cheguem ao React.
     */
    private UsuarioDTO convertToDTO(Usuario usuario) {
        return new UsuarioDTO(
                usuario.getId(),
                usuario.getName(),
                usuario.getEmail(),
                usuario.getRole() != null ? usuario.getRole().name() : "STUDENT",
                usuario.getClassName(),
                usuario.getBalance(), // Valor real da coluna 'balance'
                usuario.getPhone(),
                usuario.getRegistration() // Valor real da coluna 'registration'
        );
    }
}