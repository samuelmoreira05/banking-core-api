package com.banco.api.banco.repository;

import com.banco.api.banco.model.entity.Cartao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartaoRepository extends JpaRepository<Cartao, Long> {
}
