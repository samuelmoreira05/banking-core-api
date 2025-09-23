package com.banco.api.banco.controller.conta.response;

import com.banco.api.banco.enums.StatusConta;
import com.banco.api.banco.enums.TipoConta;
import com.banco.api.banco.model.entity.Conta;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Locale;

public record DadosMostrarConta(
        String numeroConta,
        TipoConta tipo,
        String agencia,
        BigDecimal saldo,
        StatusConta status,
        LocalDate dataCriacao
) {
    public DadosMostrarConta(Conta conta) {
        this(
          conta.getNumeroConta(),
          conta.getTipoConta(),
          conta.getAgencia(),
          conta.getSaldo(),
          conta.getStatus(),
          conta.getDataCriacao()
        );
    }
}
