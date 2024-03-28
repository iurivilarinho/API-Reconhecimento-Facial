package com.br.face.service;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.br.face.models.Documento;
import com.br.face.repository.DocumentoRepository;

@Service
public class DocumentoService {

	@Autowired
	private DocumentoRepository documentoRepository;

	public Documento converterEmDocumento(MultipartFile file) throws IOException {
		if (file != null) {
			return new Documento(file.getName(), file.getContentType(), file.getBytes());
		}
		return null;
	}

	public Set<Documento> converterEmListaDocumento(Set<MultipartFile> file) throws IOException {

		if (file != null) {
			Set<Documento> documentos = new HashSet<>();
			file.forEach(f -> {
				if (!f.isEmpty()) {
					try {
						documentos.add(new Documento(f.getName(), f.getContentType(), f.getBytes()));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
			return documentos;
		} else {
			return null;
		}

	}

	@Transactional
	public Documento salvar(Documento documento) {
		return documentoRepository.save(documento);
	}

	@Transactional
	public List<Documento> buscar() {
		// Buscar todos os documentos
		List<Documento> documentos = documentoRepository.findAll();

		// Ordenar os documentos por ID do usuário
		documentos = documentos.stream()
				.sorted(Comparator.comparing(doc -> ((Documento) doc).getUsuario().getId()).reversed())
				.collect(Collectors.toList());

		return documentos;
	}

	@Transactional
	public void deletar(Long idUsuario) {
		documentoRepository.deleteByUsuario_Id(idUsuario);
	}
}