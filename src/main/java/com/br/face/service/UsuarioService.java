package com.br.face.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.br.face.models.Usuario;
import com.br.face.repository.UsuarioRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UsuarioService {

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Transactional
	public Usuario salvar() {
		return usuarioRepository.save(new Usuario());
	}

	@Transactional(readOnly = true)
	public List<Usuario> buscar() {
		return usuarioRepository.findAll();
	}

	@Transactional(readOnly = true)
	public Usuario buscarUsuarioPorId(Long id) {
		return usuarioRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Usuario não encontrado para o ID " + id));
	}

}
