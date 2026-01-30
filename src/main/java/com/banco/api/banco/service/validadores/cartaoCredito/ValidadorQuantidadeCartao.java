package com.banco.api.banco.service.validadores.cartao;

import com.banco.api.banco.enums.TipoCartao;
import com.banco.api.banco.infra.exception.RegraDeNegocioException;
import com.banco.api.banco.model.entity.Cartao;
import com.banco.api.banco.model.entity.Cliente;
import com.banco.api.banco.model.entity.Conta;
import com.banco.api.banco.repository.CartaoRepository;
import org.springframework.stereotype.Component;

@Component
public class ValidadorQuantidadeCartao implements ValidadorSolicitacaoCredito{
    private final CartaoRepository cartaoRepository;

    public ValidadorQuantidadeCartao(CartaoRepository cartaoRepository) {
        this.cartaoRepository = cartaoRepository;
    }

    public void validar(Cliente cliente, Conta conta){
        if (cartaoRepository.existsByContaAndTipoCartao(conta, TipoCartao.CREDITO)){
            throw new RegraDeNegocioException("O cliente ja tem um cartão de credito");
        }
    }
}
