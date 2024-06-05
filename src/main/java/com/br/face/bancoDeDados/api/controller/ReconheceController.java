package com.br.face.bancoDeDados.api.controller;

import java.io.IOException;
import java.util.List;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.br.face.bancoDeDados.api.CapturaApi;
import com.br.face.bancoDeDados.api.ReconhecimentoApi;
import com.br.face.models.UsuarioDTO;
import com.br.face.service.ParametrosSistemaService;
import com.br.face.service.UsuarioService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/reconhecimento")
public class ReconheceController {

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private ParametrosSistemaService parametrosSistemaService;

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
	public ResponseEntity<UsuarioDTO> reconhecer(@RequestParam(required = false) Long idUsuario,
			@RequestParam(required = false) String idSessao, @RequestPart List<MultipartFile> files)
			throws IOException {

		Integer qtdImagensReconhecimento = parametrosSistemaService.buscar().getQtdImgReconhecimento();

		if (files.size() > qtdImagensReconhecimento || files.size() < qtdImagensReconhecimento) {

			throw new BadRequestException(
					"o número de imagens deve ser igual a " + qtdImagensReconhecimento.toString() + " !");
		}

		UsuarioDTO usuario = reconhecimentoApi.reconhecer(files);
		if (usuario != null && usuario.getIdUsuario().equals(idUsuario)) {
			usuario.setIdSessao(idSessao);
			usuario.setReconhecido(true);
			return ResponseEntity.ok(usuario);
		} else {
			usuario.setReconhecido(false);
			usuario.setIdUsuario(idUsuario);
			usuario.setNome(usuarioService.buscarUsuarioPorId(idUsuario).getNome());
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(usuario);
		}
	}

	@PostMapping(value = "/{idUsuario}", consumes = "multipart/form-data")
	@Operation(summary = "Cadastrar Imagens", description = "Endpoint para cadastrar imagens de treinamento para um usuário.")
	public ResponseEntity<?> cadastrarImagens(@RequestPart List<MultipartFile> files, @PathVariable Long idUsuario)
			throws InterruptedException, IOException {

		Integer qtdImagensTreinamento = parametrosSistemaService.buscar().getQtdImgCadastro();

		if (files.size() > qtdImagensTreinamento || files.size() < qtdImagensTreinamento) {

			throw new BadRequestException(
					"o número de imagens deve ser igual a " + qtdImagensTreinamento.toString() + " !");
		}

		capturaApi.capturar(files, idUsuario);
		return ResponseEntity.ok().build();
	}
}
