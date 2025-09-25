package com.banco.api.banco.controller.conta.request;

import com.banco.api.banco.enums.TipoConta;

public record ContaCadastroDadosRequest(
        Long clienteId,
        TipoConta tipo
) {
}
