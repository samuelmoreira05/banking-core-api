package com.banco.api.banco.controller.transacaoCartao.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record TransacaoCartaoEfetuarDadosRequest(
        @NotBlank(message = "O número do cartão é obrigatório")
        String numeroCartao,

        @NotBlank(message = "A senha é obrigatória")
        @Pattern(regexp = "\\d{4,6}", message = "A senha deve conter apenas números (4 a 6 dígitos)")
        String senha,

        @NotNull(message = "O valor da transação é obrigatório")
        @Positive(message = "O valor da transação deve ser maior que zero")
        BigDecimal valor,

        String descricao
) {
}
