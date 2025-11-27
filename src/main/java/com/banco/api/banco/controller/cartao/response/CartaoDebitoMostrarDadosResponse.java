package com.banco.api.banco.controller.cartao.response;

import com.banco.api.banco.model.entity.Cartao;

import java.time.format.DateTimeFormatter;

public record CartaoDebitoMostrarDadosResponse(
        String nomeTitular,
        String numeroAgencia,
        String numeroConta,
        String numeroCartao,
        String dataVencimento
) {
}
