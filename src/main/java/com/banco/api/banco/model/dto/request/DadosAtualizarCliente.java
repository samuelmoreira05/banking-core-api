package com.banco.api.banco.model.dto.request;

public record DadosAtualizarCliente(
        String nome,
        String endereco,
        String email,
        String telefone
) {
}
