// ========== UsuarioService.java ==========
package com.projetofinal.sistemapedidos.service;

import com.projetofinal.sistemapedidos.dto.UsuarioDTO;
import com.projetofinal.sistemapedidos.entity.Usuario;
import com.projetofinal.sistemapedidos.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    
    private UsuarioDTO convertToDTO(Usuario usuario) {
        return new UsuarioDTO(
            usuario.getId(),
            usuario.getName(),
            usuario.getEmail(),
            usuario.getRole().name().toLowerCase(),
            usuario.getClassName(),
            usuario.getBalance(),
            usuario.getPhone()
        );
    }
}