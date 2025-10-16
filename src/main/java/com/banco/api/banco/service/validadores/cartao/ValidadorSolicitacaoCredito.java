package com.banco.api.banco.service.validadores.cartao;

import com.banco.api.banco.model.entity.Cliente;
import com.banco.api.banco.model.entity.Conta;

public interface ValidadorSolicitacaoCredito {
    void validar(Cliente cliente, Conta conta);
}
