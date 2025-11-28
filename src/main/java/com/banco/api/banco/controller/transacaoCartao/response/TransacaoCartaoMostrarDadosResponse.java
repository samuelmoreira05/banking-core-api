package com.banco.api.banco.controller.transacaoCartao.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransacaoCartaoMostrarDadosResponse(
        Long idTransacao,
        BigDecimal valor,
        String descricao,
        LocalDateTime dataHora,
        BigDecimal saldoAtualizado
) {
}
