package com.br.face.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.br.face.models.Documento;

@Repository
public interface DocumentoRepository extends JpaRepository<Documento, Long>{

}
