package com.banco.api.banco.model.dto.Request;

import java.time.LocalDate;

public record DadosCadastroRequest(
        String nome,
        String email,
        String cpf,
        String telefone,
        String endereco,
        LocalDate dataNascimento
) {
}
