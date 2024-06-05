package com.br.face.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.br.face.models.ParametrosSistema;

@Repository
public interface ParametrosSistemaRepository extends JpaRepository<ParametrosSistema, Long> {

	Optional<ParametrosSistema> findTopByOrderByIdAsc();

}
