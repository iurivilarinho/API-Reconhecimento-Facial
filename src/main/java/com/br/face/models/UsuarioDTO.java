package com.br.face.models;

public class UsuarioDTO {

	private Long idUsuario;

	private String nome;

	private Integer confianca;

	private String idSessao;

	private Boolean reconhecido;

	public UsuarioDTO(Usuario usuario, Integer confianca, Boolean reconhecido) {
		this.idUsuario = usuario.getId();
		this.nome = usuario.getNome();
		this.confianca = confianca;
		this.reconhecido = reconhecido;
	}

	public Long getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(Long idUsuario) {
		this.idUsuario = idUsuario;
	}

	public String getIdSessao() {
		return idSessao;
	}

	public void setIdSessao(String idSessao) {
		this.idSessao = idSessao;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Integer getConfianca() {
		return confianca;
	}

	public void setConfianca(Integer confianca) {
		this.confianca = confianca;
	}

	public Boolean getReconhecido() {
		return reconhecido;
	}

	public void setReconhecido(Boolean reconhecido) {
		this.reconhecido = reconhecido;
	}

}
