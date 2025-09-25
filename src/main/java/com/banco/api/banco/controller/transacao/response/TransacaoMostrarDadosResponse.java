package com.banco.api.banco.controller.transacao.response;

import com.banco.api.banco.enums.TipoTransacao;
import com.banco.api.banco.model.entity.Transacao;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransacaoMostrarDadosResponse(
        Long idTransacao,
        TipoTransacao tipo,
        LocalDateTime dataTransacao,
        BigDecimal valor,
        TransacaoContaResumidaDadosResponse conta
) {
    public TransacaoMostrarDadosResponse(Transacao transacao) {
        this(
                transacao.getId(),
                transacao.getTipo(),
                transacao.getDataTransacao(),
                transacao.getValor(),
                new TransacaoContaResumidaDadosResponse(
                        transacao.getConta().getNumeroConta(),
                        transacao.getConta().getAgencia()
                )
        );
    }
}

