package com.banco.api.banco.controller.conta.response;

import com.banco.api.banco.controller.cliente.response.ClienteMostrarDadosResponse;
import com.banco.api.banco.enums.TipoConta;
import com.banco.api.banco.model.entity.Conta;

import java.math.BigDecimal;

public record ContaListagemDadosResponse(
        long id,
        String numeroConta,
        TipoConta tipo,
        String agencia,
        BigDecimal saldo,
        ClienteMostrarDadosResponse dado
) {
    public ContaListagemDadosResponse(Conta conta) {
        this(conta.getId(), conta.getNumeroConta(), conta.getTipoConta(), conta.getAgencia(), conta.getSaldo(), new ClienteMostrarDadosResponse(conta.getCliente()));
    }
}
