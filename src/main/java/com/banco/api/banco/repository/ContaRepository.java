package com.banco.api.banco.repository;

import com.banco.api.banco.model.entity.Conta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContaRepository extends JpaRepository<Conta, Long> {

    boolean existsByNumeroConta(String numeroConta);
}
