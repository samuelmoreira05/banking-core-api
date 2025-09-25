package com.banco.api.banco.controller.cliente.request;

public record ClienteAtualizarDadosRequest(
        String nome,
        String endereco,
        String email,
        String telefone
) {
}
