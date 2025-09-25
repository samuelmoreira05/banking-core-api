package com.banco.api.banco.controller.transacao.response;

import com.banco.api.banco.enums.TipoTransacao;
import com.banco.api.banco.model.entity.Conta;
import com.banco.api.banco.model.entity.Transacao;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record DadosMostrarTransacaoResponse(
        Long idTransacao,
        TipoTransacao tipo,
        LocalDateTime dataTransacao,
        BigDecimal valor,
        DadosContaResumida conta
) {
    public DadosMostrarTransacaoResponse(Transacao transacao) {
        this(
                transacao.getId(),
                transacao.getTipo(),
                transacao.getDataTransacao(),
                transacao.getValor(),
                new DadosContaResumida(
                        transacao.getConta().getNumeroConta(),
                        transacao.getConta().getAgencia()
                )
        );
    }
}

