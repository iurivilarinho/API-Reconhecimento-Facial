package com.br.face.models;

public class UsuarioDTO {

	private Long id;

	private String nome;

	private Integer confianca;

	public UsuarioDTO(Usuario usuario, Integer confianca) {
		this.id = usuario.getId();
		this.nome = usuario.getNome();
		this.confianca = confianca;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

}
