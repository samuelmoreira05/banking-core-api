package com.banco.api.banco.controller.cartao.response;


public record CartaoDebitoMostrarDadosResponse(
        String nomeTitular,
        String numeroAgencia,
        String numeroConta,
        String numeroCartao,
        String dataVencimento
) {
}
