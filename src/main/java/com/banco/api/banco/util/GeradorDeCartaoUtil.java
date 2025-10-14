package com.banco.api.banco.util;

import java.security.SecureRandom;

public class GeradorDeCartaoUtil {

    private static final String BIN_BANKING = "121119";

    public static String geraNumeroCartao() {
        SecureRandom random = new SecureRandom();
        long numeroConta = random.nextLong(999_999_999);

        String numeroFormatado = String.format("%09d", numeroConta);

        String prefixo = BIN_BANKING + numeroFormatado;

        String digitoVerificador = calculadigitoVerificador(prefixo);

        return prefixo + digitoVerificador;
    }

    private static String calculadigitoVerificador(String numeroParcial) {
        int soma = 0;
        boolean isSecondDigit = false;
        for (int i = numeroParcial.length() - 1; i >= 0; i--) {
            int d = numeroParcial.charAt(i) - '0';

            if (isSecondDigit) {
                d = d * 2;
            }

            soma += d / 10;
            soma += d % 10;

            isSecondDigit = !isSecondDigit;
        }

        int digitoVerificador = (soma % 10 == 0) ? 0 : 10 - (soma % 10);
        return String.valueOf(digitoVerificador);
    }

    public static String geraCvv() {
        SecureRandom random = new SecureRandom();
        int cvv = random.nextInt(1000);
        return String.format("%03d", cvv);
    }
}
