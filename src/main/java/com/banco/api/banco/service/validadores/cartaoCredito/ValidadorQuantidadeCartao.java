package com.banco.api.banco.service.validadores.cartaoCredito;

import com.banco.api.banco.enums.TipoCartao;
import com.banco.api.banco.infra.exception.RegraDeNegocioException;
import com.banco.api.banco.model.entity.Cliente;
import com.banco.api.banco.model.entity.Conta;
import com.banco.api.banco.repository.CartaoRepository;
import org.springframework.stereotype.Component;

@Component
public class ValidadorQuantidadeCartao implements ValidadorEmissaoCartao {
    private final CartaoRepository cartaoRepository;

    public ValidadorQuantidadeCartao(CartaoRepository cartaoRepository) {
        this.cartaoRepository = cartaoRepository;
    }

    public void validar(Cliente cliente, Conta conta, TipoCartao tipoCartao){
        if (cartaoRepository.existsByContaAndTipoCartao(conta, tipoCartao)){
            throw new RegraDeNegocioException("O cliente ja tem um cartão do tipo " + tipoCartao);
        }
    }
}
