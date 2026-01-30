package com.banco.api.banco.service.validadores.cartaoValidaDados;

import com.banco.api.banco.enums.TipoCartao;
import com.banco.api.banco.model.entity.Cartao;

public interface ValidadorDadosCartao {
    void validarDados(Cartao cartao, String senhaDigitada, TipoCartao tipoCartao);
}
