package com.br.face.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tbParametrosSistema")
public class ParametrosSistema {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Integer qtdImgCadastro;

	@Column(nullable = false)
	private Integer qtdImgReconhecimento;

	@Column(nullable = false)
	private Integer qtdTentativasReconhecimento;

	@Column(nullable = false)
	private String chaveAcesso;

	@Column(nullable = false)
	private String urlSucessoReconhecimento;

	@Column(nullable = false)
	private String urlSemSucessoReconhecimento;

	private String nomePrefeitura;

	private String urlImgPrefeitura;

	public ParametrosSistema() {

	}

	public ParametrosSistema(Integer qtdImgCadastro, Integer qtdImgReconhecimento, Integer qtdTentativasReconhecimento,
			String chaveAcesso, String urlSucessoReconhecimento, String urlSemSucessoReconhecimento,
			String nomePrefeitura, String urlImgPrefeitura) {

		this.qtdImgCadastro = qtdImgCadastro;
		this.qtdImgReconhecimento = qtdImgReconhecimento;
		this.qtdTentativasReconhecimento = qtdTentativasReconhecimento;
		this.chaveAcesso = chaveAcesso;
		this.urlSucessoReconhecimento = urlSucessoReconhecimento;
		this.urlSemSucessoReconhecimento = urlSemSucessoReconhecimento;
		this.nomePrefeitura = nomePrefeitura;
		this.urlImgPrefeitura = urlImgPrefeitura;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getQtdImgCadastro() {
		return qtdImgCadastro;
	}

	public void setQtdImgCadastro(Integer qtdImgCadastro) {
		this.qtdImgCadastro = qtdImgCadastro;
	}

	public Integer getQtdImgReconhecimento() {
		return qtdImgReconhecimento;
	}

	public void setQtdImgReconhecimento(Integer qtdImgReconhecimento) {
		this.qtdImgReconhecimento = qtdImgReconhecimento;
	}

	public Integer getQtdTentativasReconhecimento() {
		return qtdTentativasReconhecimento;
	}

	public void setQtdTentativasReconhecimento(Integer qtdTentativasReconhecimento) {
		this.qtdTentativasReconhecimento = qtdTentativasReconhecimento;
	}

	public String getChaveAcesso() {
		return chaveAcesso;
	}

	public void setChaveAcesso(String chaveAcesso) {
		this.chaveAcesso = chaveAcesso;
	}

	public String getUrlSucessoReconhecimento() {
		return urlSucessoReconhecimento;
	}

	public void setUrlSucessoReconhecimento(String urlSucessoReconhecimento) {
		this.urlSucessoReconhecimento = urlSucessoReconhecimento;
	}

	public String getUrlSemSucessoReconhecimento() {
		return urlSemSucessoReconhecimento;
	}

	public void setUrlSemSucessoReconhecimento(String urlSemSucessoReconhecimento) {
		this.urlSemSucessoReconhecimento = urlSemSucessoReconhecimento;
	}

	public String getNomePrefeitura() {
		return nomePrefeitura;
	}

	public void setNomePrefeitura(String nomePrefeitura) {
		this.nomePrefeitura = nomePrefeitura;
	}

	public String getUrlImgPrefeitura() {
		return urlImgPrefeitura;
	}

	public void setUrlImgPrefeitura(String urlImgPrefeitura) {
		this.urlImgPrefeitura = urlImgPrefeitura;
	}

}
