// ========== UsuarioService.java ==========
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
     * Registrar um novo usuário vindo do formulário de cadastro.
     * Agora inclui o campo de matrícula (registration).
     */
    @Transactional
    public UsuarioDTO registrar(UsuarioDTO dto) {
        // 1. Validação de segurança: verificar se e-mail já existe
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Este e-mail já está cadastrado em nosso sistema.");
        }

        // 2. Criação da entidade e mapeamento dos dados do DTO
        Usuario novoUsuario = new Usuario();
        novoUsuario.setName(dto.getName());
        novoUsuario.setEmail(dto.getEmail());
        novoUsuario.setPassword(dto.getPassword());
        novoUsuario.setPhone(dto.getPhone());
        novoUsuario.setClassName(dto.getClassName());

        // MAPEAMENTO DA MATRÍCULA (Importante para a página de perfil)
        novoUsuario.setRegistration(dto.getRegistration());

        // 3. Regras de negócio automáticas
        novoUsuario.setRole(RoleUsuario.STUDENT);
        novoUsuario.setBalance(new BigDecimal("100.00"));

        // 4. Salvar no MySQL
        Usuario salvo = usuarioRepository.save(novoUsuario);
        return convertToDTO(salvo);
    }

    @Transactional
    public UsuarioDTO criar(Usuario usuario) {
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new RuntimeException("Email já cadastrado");
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

    public void deletar(Long id) {
        usuarioRepository.deleteById(id);
    }

    public UsuarioDTO autenticar(String email, String password) {
        Usuario usuario = usuarioRepository.findByEmailAndPassword(email, password)
                .orElse(null);
        return usuario != null ? convertToDTO(usuario) : null;
    }

    /**
     * Converte a Entidade do Banco para DTO
     * Inclui a matrícula para que o React possa exibi-la.
     */
    private UsuarioDTO convertToDTO(Usuario usuario) {
        return new UsuarioDTO(
                usuario.getId(),
                usuario.getName(),
                usuario.getEmail(),
                usuario.getRole().name(),
                usuario.getClassName(),
                usuario.getBalance(),
                usuario.getPhone(),
                usuario.getRegistration() // Adicionado aqui
        );
    }
}