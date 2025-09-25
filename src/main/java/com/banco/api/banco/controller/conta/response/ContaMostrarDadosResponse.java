package com.banco.api.banco.controller.conta.response;

import com.banco.api.banco.enums.StatusCliente;
import com.banco.api.banco.enums.TipoConta;
import com.banco.api.banco.model.entity.Cliente;
import com.banco.api.banco.model.entity.Conta;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ContaMostrarDadosResponse(
        String numeroConta,
        TipoConta tipo,
        String agencia,
        BigDecimal saldo,
        StatusCliente status,
        LocalDateTime dataCriacao,
        Cliente cliente
) {
    public ContaMostrarDadosResponse(Conta conta) {
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
