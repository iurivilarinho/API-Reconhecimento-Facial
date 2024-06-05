package com.br.face.bancoDeDados.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.br.face.models.ParametrosSistema;
import com.br.face.service.ParametrosSistemaService;

@RequestMapping("/parametros")
@RestController
public class ParametrosSistemaController {

	@Autowired
	private ParametrosSistemaService parametrosSistemaService;

	@GetMapping
	public ResponseEntity<ParametrosSistema> buscar() {
		return ResponseEntity.ok(parametrosSistemaService.buscar());
	}

}
