package com.banco.api.banco.infra;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class TratadorDeErros {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity tratar404(){
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity tratar400(MethodArgumentNotValidException e){
        var error = e.getFieldErrors();
        return ResponseEntity.badRequest().body(error.stream().map(ErroValidacaoDados::new).toList());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> tratarRegraDeNegocio(IllegalStateException e){
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> tratar500(Exception e){
        e.printStackTrace();
        return ResponseEntity.status(500).body("Erro inesperado: " + e.getMessage());
    }

    public record ErroValidacaoDados(String campo, String mensagem){
        public ErroValidacaoDados(FieldError erro){
            this(erro.getField(), erro.getDefaultMessage());
        }
    }
}
