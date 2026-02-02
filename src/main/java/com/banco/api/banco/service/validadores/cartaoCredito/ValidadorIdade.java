package com.banco.api.banco.service.validadores.cartaoCredito;

import com.banco.api.banco.enums.TipoCartao;
import com.banco.api.banco.infra.exception.RegraDeNegocioException;
import com.banco.api.banco.model.entity.Cliente;
import com.banco.api.banco.model.entity.Conta;
import org.springframework.stereotype.Component;

@Component
public class ValidadorIdade implements ValidadorEmissaoCartao {

    public void validar(Cliente cliente, Conta conta, TipoCartao tipoCartao){
        if (cliente.getIdade() < 18){
            throw new RegraDeNegocioException("Cliente menor de 18 anos não pode solicitar cartão.");
        }
    }

}
