package com.banco.api.banco.controller.transacao.request;

import com.banco.api.banco.enums.TipoTransacao;

import java.math.BigDecimal;

public record TransacaoEfetuarDadosRequest(
        Long contaId,
        TipoTransacao tipo,
        BigDecimal valor
) {
}
