package com.banco.api.banco.controller.conta.response;

import com.banco.api.banco.enums.StatusConta;
import com.banco.api.banco.enums.TipoConta;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ContaMostrarDadosResponse(
        String numeroConta,
        TipoConta tipo,
        String agencia,
        BigDecimal saldo,
        StatusConta status,
        LocalDateTime dataCriacao,
        Long idCliente,
        String nomeCliente
) {
}
