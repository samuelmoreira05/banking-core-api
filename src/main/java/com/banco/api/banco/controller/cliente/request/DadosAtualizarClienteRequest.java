package com.banco.api.banco.controller.cliente.request;

public record DadosAtualizarClienteRequest(
        String nome,
        String endereco,
        String email,
        String telefone
) {
}
