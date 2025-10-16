package com.banco.api.banco.controller.cartao.request;

import com.banco.api.banco.enums.TipoCartao;

public record CartaoDebitoCriarDadosRequest(
        Long idConta
) {
}
