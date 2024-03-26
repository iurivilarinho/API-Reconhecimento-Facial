package com.br.face.bancoDeDados.api.controller;

import java.io.IOException;
import java.util.List;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.br.face.bancoDeDados.api.CapturaApi;
import com.br.face.bancoDeDados.api.ReconhecimentoApi;
import com.br.face.models.Usuario;

@RestController
@RequestMapping("/teste")
public class ReconheceController {

	@Autowired
	private ReconhecimentoApi reconhecimentoApi;

	@Autowired
	private CapturaApi capturaApi;

	@PostMapping(consumes = "multipart/form-data")
	public ResponseEntity<?> reconhecer(@RequestPart List<MultipartFile> files) throws IOException {

		if (files.size() > 3 || files.size() < 3) {

			throw new BadRequestException("o número de imagens deve ser igual a 3!");
		}

		Usuario usuario = reconhecimentoApi.reconhecer(files);
		if (usuario != null) {
			return ResponseEntity.ok(usuario);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhum usuário reconhecido.");
		}
	}

	@PostMapping(value = "/{idUsuario}", consumes = "multipart/form-data")
	public ResponseEntity<?> cadastrarImagens(@RequestPart List<MultipartFile> files, @PathVariable Long idUsuario)
			throws InterruptedException, IOException {
		if (files.size() > 2 || files.size() < 2) {

			throw new BadRequestException("o número de imagens deve ser igual a 50!");
		}

		capturaApi.capturar(files, idUsuario);
		return ResponseEntity.ok().build();
	}
}
