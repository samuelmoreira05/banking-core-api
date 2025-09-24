package com.banco.api.banco.controller.conta.response;

import com.banco.api.banco.controller.cliente.response.DadosMostrarClienteResponse;
import com.banco.api.banco.enums.TipoConta;
import com.banco.api.banco.model.entity.Conta;

import java.math.BigDecimal;

public record DadosListagemContasResponse(
        long id,
        String numeroConta,
        TipoConta tipo,
        String agencia,
        BigDecimal saldo,
        DadosMostrarClienteResponse dado
) {
    public DadosListagemContasResponse(Conta conta) {
        this(conta.getId(), conta.getNumeroConta(), conta.getTipoConta(), conta.getAgencia(), conta.getSaldo(), new DadosMostrarClienteResponse(conta.getCliente()));
    }
}
