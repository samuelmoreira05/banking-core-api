package com.banco.api.banco.controller.cliente.request;

public record DadosAtualizarCliente(
        String nome,
        String endereco,
        String email,
        String telefone
) {
}
