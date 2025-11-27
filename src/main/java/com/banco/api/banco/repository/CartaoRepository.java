package com.banco.api.banco.repository;

import com.banco.api.banco.enums.TipoCartao;
import com.banco.api.banco.model.entity.Cartao;
import com.banco.api.banco.model.entity.Conta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartaoRepository extends JpaRepository<Cartao, Long> {

    boolean existsByContaAndTipoCartao(Conta conta, TipoCartao tipo);

    boolean existsByNumeroCartao(String numeroCartao);
}
