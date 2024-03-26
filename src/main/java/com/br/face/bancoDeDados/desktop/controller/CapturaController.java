package com.br.face.bancoDeDados.desktop.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.bytedeco.javacv.FrameGrabber.Exception;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.br.face.bancoDeDados.TreinamentoBancoDados;
import com.br.face.bancoDeDados.desktop.CapturaDesktop;
import com.br.face.bancoDeDados.desktop.ReconhecimentoDesktop;
import com.br.face.models.Documento;
import com.br.face.models.Usuario;
import com.br.face.repository.DocumentoRepository;
import com.br.face.service.DocumentoService;
import com.br.face.service.UsuarioService;

@RestController
@RequestMapping("/capturar")
public class CapturaController {

	@Autowired
	private CapturaDesktop captura;

	@Autowired
	private TreinamentoBancoDados treinamentoBancoDados;

	@Autowired
	private ReconhecimentoDesktop reconhecimentoBancoDados;

	@Autowired
	private DocumentoRepository documentoRepository;

	@Autowired
	private DocumentoService documentoService;

	@Autowired
	private UsuarioService usuarioService;

	@GetMapping("/{id}/download")
	public ResponseEntity<byte[]> downloadDocumento(@PathVariable Long id) {
		Optional<Documento> documentoOptional = documentoRepository.findById(id);
		if (documentoOptional.isPresent()) {
			Documento documento = documentoOptional.get();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.parseMediaType(documento.getContentType()));
			headers.setContentDispositionFormData(documento.getNome(), documento.getNome());
			headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
			ResponseEntity<byte[]> responseEntity = new ResponseEntity<>(documento.getDocumento(), headers,
					HttpStatus.OK);
			return responseEntity;
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@GetMapping("/{idUsuario}")
	public ResponseEntity<?> capturar(@PathVariable Long idUsuario) throws Exception, InterruptedException {
		captura.capturar(idUsuario);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/treinar")
	public ResponseEntity<?> treinar() throws Exception, InterruptedException {
		treinamentoBancoDados.treinarModelo();
		return ResponseEntity.ok().build();
	}

	@GetMapping("/reconhecer")
	public ResponseEntity<?> reconhecer() throws Exception, InterruptedException {
		reconhecimentoBancoDados.reconhecer();
		return ResponseEntity.ok().build();
	}

	@PostMapping("/documentos/{idUsuario}")
	public ResponseEntity<String> cadastrarDocumentos(@RequestPart("files") List<MultipartFile> files,
			@PathVariable Long idUsuario) throws IOException {

		Usuario usuario = usuarioService.buscarUsuarioPorId(idUsuario);
		try {
			// Verifica se o número de documentos fornecidos é igual a 25
			if (files.size() != 25) {
				return ResponseEntity.badRequest().body("Número inválido de documentos. Deve ser 25.");
			}

			for (MultipartFile file : files) {
				documentoService.converterEmDocumento(file);
				Documento documento = new Documento(file.getOriginalFilename(), file.getContentType(), file.getBytes(),
						usuario);
				documentoRepository.save(documento);
			}
			treinamentoBancoDados.treinarModelo();
			return ResponseEntity.status(HttpStatus.CREATED).body("Documentos cadastrados com sucesso.");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Ocorreu um erro ao cadastrar os documentos.");
		}
	}

}
