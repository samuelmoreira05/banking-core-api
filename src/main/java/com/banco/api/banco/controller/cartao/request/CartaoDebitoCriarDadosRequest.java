package com.banco.api.banco.controller.cartao.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CartaoDebitoCriarDadosRequest(
        Long idConta,

        @NotBlank(message = "A senha é obrigatória")
        @Pattern(regexp = "\\d{4,6}", message = "A senha deve conter entre 4 e 6 dígitos numéricos")
        String senha
) {
}