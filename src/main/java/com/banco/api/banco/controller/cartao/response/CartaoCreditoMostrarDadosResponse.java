package com.banco.api.banco.controller.cartao.response;
import java.math.BigDecimal;

public record CartaoCreditoMostrarDadosResponse(
        String nomeTitular,
        String numeroAgencia,
        String numeroConta,
        String numeroCartao,
        String dataVencimento,
        int diaVencimentoFatura,
        BigDecimal limiteCredito
) {
}
