package com.banco.api.banco.service.validadores.cartaoCredito;

import com.banco.api.banco.infra.exception.RegraDeNegocioException;
import com.banco.api.banco.model.entity.Cliente;
import com.banco.api.banco.model.entity.Conta;
import org.springframework.stereotype.Component;

@Component
public class ValidadorIdade implements ValidadorSolicitacaoCredito{

    public void validar(Cliente cliente, Conta conta){
        if (cliente.getIdade() < 18){
            throw new RegraDeNegocioException("Cliente menor de 18 anos não pode solicitar cartão.");
        }
    }

}
