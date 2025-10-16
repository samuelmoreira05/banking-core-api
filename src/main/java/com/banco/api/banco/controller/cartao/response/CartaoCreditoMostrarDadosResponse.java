package com.banco.api.banco.controller.cartao.response;

import com.banco.api.banco.model.entity.Cartao;

import java.time.format.DateTimeFormatter;

public record CartaoCreditoMostrarDadosResponse(
        String nomeTitular,
        String numeroAgencia,
        String numeroConta,
        String numeroCartao,
        String dataVencimento,
        int diaVencimentoFatura
) {

    private static final DateTimeFormatter FORMATADOR_DATA = DateTimeFormatter.ofPattern("MM/yy");

    public CartaoCreditoMostrarDadosResponse(Cartao cartao){
        this(
                cartao.getConta().getCliente().getNome(),
                cartao.getConta().getAgencia(),
                cartao.getConta().getNumeroConta(),
                cartao.getNumeroCartao(),
                cartao.getDataVencimento().format(FORMATADOR_DATA),
                cartao.getDiaVencimentoFatura()
        );
    }
}
