package com.banco.api.banco.service.validadores.cartaoValidaDados;

import com.banco.api.banco.enums.TipoCartao;
import com.banco.api.banco.infra.exception.RegraDeNegocioException;
import com.banco.api.banco.model.entity.Cartao;
import org.springframework.stereotype.Component;

@Component
public class ValidadorTipoCartao implements ValidadorDadosCartao{

    public void validarDados(Cartao cartao, String senhaDigitada, TipoCartao tipoCartao) {
        if (cartao.getTipoCartao() != tipoCartao) {
            throw new RegraDeNegocioException("Tipo de cartão inválido para operação de " + tipoCartao);
        }
    }
}
