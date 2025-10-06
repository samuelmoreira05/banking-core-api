package com.banco.api.banco.infra.exception;

public class RegraDeNegocioException extends RuntimeException{

    public RegraDeNegocioException(String mensagem){
        super(mensagem);
    }
}
