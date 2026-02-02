package com.banco.api.banco.service.validadores.cartaoCredito;

import com.banco.api.banco.enums.TipoCartao;
import com.banco.api.banco.model.entity.Cliente;
import com.banco.api.banco.model.entity.Conta;

public interface ValidadorEmissaoCartao {
    void validar(Cliente cliente, Conta conta, TipoCartao tipoCartao);
}
