package com.banco.api.banco.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class GeradorDeContaUtil {

    private static final String AGENCIA_BANKING = "0001";
    private final SecureRandom random = new SecureRandom();

    public String gerarAgencia() {
        return AGENCIA_BANKING;
    }

    public String gerarNumeroConta() {
        int numero = 100000 + random.nextInt(900000);
        return String.valueOf(numero);
    }
}
