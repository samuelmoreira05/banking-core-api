package com.banco.api.banco.service.validadores.cartaoValidaDados;

import com.banco.api.banco.enums.TipoCartao;
import com.banco.api.banco.infra.exception.RegraDeNegocioException;
import com.banco.api.banco.model.entity.Cartao;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class ValidadorSenhaIncorreta implements ValidadorDadosCartao{

    private final PasswordEncoder passwordEncoder;

    public ValidadorSenhaIncorreta(PasswordEncoder passwordEncoder) {this.passwordEncoder = passwordEncoder;}

    public void validarDados(Cartao cartao, String senhaDigitada, TipoCartao tipoCartao) {
        if (!passwordEncoder.matches(senhaDigitada, cartao.getSenha())) {
            throw new RegraDeNegocioException("Senha incorreta.");
        }
    }
}
