package com.banco.api.banco.controller.transacao;

import com.banco.api.banco.controller.transacao.request.TransacaoEfetuarDadosRequest;
import com.banco.api.banco.controller.transacao.response.TransacaoMostrarDadosResponse;
import com.banco.api.banco.service.TransacaoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/transacoes")
public class TransacaoController {

    private final TransacaoService service;

    public TransacaoController(TransacaoService service) {
        this.service = service;
    }

    @PostMapping("/depositar")
    public ResponseEntity<TransacaoMostrarDadosResponse> deposito(
            @Valid
            @RequestBody TransacaoEfetuarDadosRequest dados
            ) {
        TransacaoMostrarDadosResponse response = service.deposito(dados);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
