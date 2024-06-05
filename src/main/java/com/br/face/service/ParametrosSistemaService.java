package com.br.face.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.br.face.models.ParametrosSistema;
import com.br.face.repository.ParametrosSistemaRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ParametrosSistemaService {
	@Autowired
	private ParametrosSistemaRepository parametrosSistemaRepository;

	@Transactional(readOnly = true)
	public ParametrosSistema buscar() {
		return parametrosSistemaRepository.findTopByOrderByIdAsc()
				.orElseThrow(() -> new EntityNotFoundException("Nenhum parametro encontrado!"));
	}

}
