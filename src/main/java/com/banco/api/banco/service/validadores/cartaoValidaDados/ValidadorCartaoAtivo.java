package com.banco.api.banco.service.validadores.cartaoValidaDados;

import com.banco.api.banco.enums.StatusCartao;
import com.banco.api.banco.enums.TipoCartao;
import com.banco.api.banco.infra.exception.RegraDeNegocioException;
import com.banco.api.banco.model.entity.Cartao;
import org.springframework.stereotype.Component;

@Component
public class ValidadorCartaoAtivo implements ValidadorDadosCartao{

    public void validarDados(Cartao cartao, String senhaDigitada, TipoCartao tipoCartao) {
        if (cartao.getStatus() != StatusCartao.CARTAO_ATIVO) {
            throw new RegraDeNegocioException("O cartão precisa estar ativo.");
        }
    }
}
