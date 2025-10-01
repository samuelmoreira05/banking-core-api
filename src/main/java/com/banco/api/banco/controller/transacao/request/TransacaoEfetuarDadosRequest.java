package com.banco.api.banco.controller.transacao.request;

import com.banco.api.banco.enums.TipoTransacao;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TransacaoEfetuarDadosRequest(
        Long contaId,
        @NotNull
        TipoTransacao tipo,
        @NotNull
        BigDecimal valor
) {
}
