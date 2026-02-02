package com.banco.api.banco.service.validadores.cartaoCredito;

import com.banco.api.banco.enums.TipoCartao;
import com.banco.api.banco.infra.exception.RegraDeNegocioException;
import com.banco.api.banco.model.entity.Cliente;
import com.banco.api.banco.model.entity.Conta;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ValidadorSaldo implements ValidadorEmissaoCartao {

    public void validar(Cliente cliente, Conta conta, TipoCartao tipoCartao){
        if (tipoCartao == TipoCartao.DEBITO) {
            return;
        }

        BigDecimal saldoMinimo = new BigDecimal(500);
        if (conta.getSaldo().compareTo(saldoMinimo) < 0) {
            throw new RegraDeNegocioException("Para solicitar o cartão de credito o saldo em conta deve ser maior que 500, saldo atual: " + conta.getSaldo());
        }
    }
}
