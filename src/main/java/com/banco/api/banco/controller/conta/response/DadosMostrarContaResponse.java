package com.banco.api.banco.controller.conta.response;

import com.banco.api.banco.enums.StatusCliente;
import com.banco.api.banco.enums.TipoConta;
import com.banco.api.banco.model.entity.Cliente;
import com.banco.api.banco.model.entity.Conta;
import java.math.BigDecimal;
import java.time.LocalDate;

public record DadosMostrarContaResponse(
        String numeroConta,
        TipoConta tipo,
        String agencia,
        BigDecimal saldo,
        StatusCliente status,
        LocalDate dataCriacao,
        Cliente cliente
) {
    public DadosMostrarContaResponse(Conta conta) {
        this(
                conta.getNumeroConta(),
                conta.getTipoConta(),
                conta.getAgencia(),
                conta.getSaldo(),
                conta.getCliente().getStatus(),
                conta.getDataCriacao(),
                conta.getCliente()
        );
    }
}
