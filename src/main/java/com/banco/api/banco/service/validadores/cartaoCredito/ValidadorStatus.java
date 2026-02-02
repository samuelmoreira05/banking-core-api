package com.banco.api.banco.service.validadores.cartaoCredito;

import com.banco.api.banco.enums.StatusCliente;
import com.banco.api.banco.enums.TipoCartao;
import com.banco.api.banco.infra.exception.RegraDeNegocioException;
import com.banco.api.banco.model.entity.Cliente;
import com.banco.api.banco.model.entity.Conta;
import org.springframework.stereotype.Component;

@Component
public class ValidadorStatus implements ValidadorEmissaoCartao {
    public void validar(Cliente cliente, Conta conta, TipoCartao tipoCartao){
        if (cliente.getStatus() != StatusCliente.ATIVO){
            throw new RegraDeNegocioException("Para solicitar um cartao o cliente deve estar com Status de ativo, status atual: " + cliente.getStatus());
        }
    }
}
