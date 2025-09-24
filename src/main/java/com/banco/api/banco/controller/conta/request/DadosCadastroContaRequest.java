package com.banco.api.banco.controller.conta.request;

import com.banco.api.banco.enums.TipoConta;

public record DadosCadastroContaRequest(
        Long clienteId,
        TipoConta tipo
) {
}
