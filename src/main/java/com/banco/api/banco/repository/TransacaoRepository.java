package com.banco.api.banco.repository;

import com.banco.api.banco.model.entity.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransacaoRepository extends JpaRepository<Transacao, Long>{
}
