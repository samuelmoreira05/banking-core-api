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
    private static final DateTimeFormatter FORMATADOR_DATA = DateTimeFormatter.ofPattern("MM/yy");

    public CartaoDebitoMostrarDadosResponse(Cartao cartao){
        this(
                cartao.getConta().getCliente().getNome(),
                cartao.getConta().getAgencia(),
                cartao.getConta().getNumeroConta(),
                cartao.getNumeroCartao(),
                cartao.getDataVencimento().format(FORMATADOR_DATA)
        );
    }
}
