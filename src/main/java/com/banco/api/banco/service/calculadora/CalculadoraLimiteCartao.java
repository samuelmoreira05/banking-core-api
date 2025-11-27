package com.banco.api.banco.service.calculadora;

import com.banco.api.banco.model.entity.Conta;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class CalculadoraLimiteCartao {
    private static final BigDecimal MULTIPLICADOR_LIMITE = new BigDecimal("0.5");

    public BigDecimal limite(Conta conta) {
        return conta.getSaldo().multiply(MULTIPLICADOR_LIMITE);
    }
}
