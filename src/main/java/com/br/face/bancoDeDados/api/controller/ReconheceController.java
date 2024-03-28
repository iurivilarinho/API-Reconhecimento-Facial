package com.br.face.bancoDeDados.api.controller;

import java.io.IOException;
import java.util.List;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.br.face.bancoDeDados.api.CapturaApi;
import com.br.face.bancoDeDados.api.ReconhecimentoApi;
import com.br.face.models.UsuarioDTO;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/reconhecimento")
public class ReconheceController {

	@Autowired
	private ReconhecimentoApi reconhecimentoApi;

	@Autowired
	private CapturaApi capturaApi;

	@Value("${qtd.imagens.treinamento}")
	private Integer qtdImagensTreinamento;

	@Value("${qtd.imagens.reconhecimento}")
	private Integer qtdImagensReconhecimento;

	@PostMapping(consumes = "multipart/form-data")
	@Operation(summary = "Reconhecer Usuário", description = "Endpoint para reconhecer um usuário com base nas imagens fornecidas.")
	public ResponseEntity<UsuarioDTO> reconhecer(@RequestPart List<MultipartFile> files) throws IOException {

		if (files.size() > qtdImagensReconhecimento || files.size() < qtdImagensReconhecimento) {

			throw new BadRequestException(
					"o número de imagens deve ser igual a " + qtdImagensReconhecimento.toString() + " !");
		}

		UsuarioDTO usuario = reconhecimentoApi.reconhecer(files);
		if (usuario != null) {
			return ResponseEntity.ok(usuario);
		} else {
			throw new EntityNotFoundException("Nenhum usuário reconhecido.");
		}
	}

	@PostMapping(value = "/{idUsuario}", consumes = "multipart/form-data")
	@Operation(summary = "Cadastrar Imagens", description = "Endpoint para cadastrar imagens de treinamento para um usuário.")
	public ResponseEntity<?> cadastrarImagens(@RequestPart List<MultipartFile> files, @PathVariable Long idUsuario)
			throws InterruptedException, IOException {
		if (files.size() > qtdImagensTreinamento || files.size() < qtdImagensTreinamento) {

			throw new BadRequestException(
					"o número de imagens deve ser igual a " + qtdImagensTreinamento.toString() + " !");
		}

		capturaApi.capturar(files, idUsuario);
		return ResponseEntity.ok().build();
	}
}
