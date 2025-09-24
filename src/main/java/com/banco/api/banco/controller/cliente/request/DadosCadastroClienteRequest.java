package com.banco.api.banco.controller.cliente.request;

import java.time.LocalDate;

public record DadosCadastroClienteRequest(
        String nome,
        String email,
        String cpf,
        String telefone,
        String endereco,
        LocalDate dataNascimento
) {
}
