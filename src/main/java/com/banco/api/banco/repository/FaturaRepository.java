package com.banco.api.banco.repository;

import com.banco.api.banco.enums.StatusFatura;
import com.banco.api.banco.model.entity.Cartao;
import com.banco.api.banco.model.entity.Fatura;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FaturaRepository extends JpaRepository<Fatura, Long> {

    Optional<Fatura> findByCartaoAndStatus(Cartao cartao, StatusFatura status);
}
