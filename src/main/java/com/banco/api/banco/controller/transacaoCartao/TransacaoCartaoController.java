package com.banco.api.banco.controller.transacaoCartao;

import com.banco.api.banco.controller.transacaoCartao.request.TransacaoCartaoEfetuarDadosRequest;
import com.banco.api.banco.controller.transacaoCartao.response.TransacaoCartaoMostrarDadosResponse;
import com.banco.api.banco.service.TransacaoCartaoService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/transacao-cartao")
@SecurityRequirement(name = "barer-token")
public class TransacaoCartaoController {

    private final TransacaoCartaoService transacaoCartaoService;

    public TransacaoCartaoController(TransacaoCartaoService transacaoCartaoService) {
        this.transacaoCartaoService = transacaoCartaoService;
    }

    @PostMapping("/debito")
    @SecurityRequirement(name = "barer-token")
    public ResponseEntity<TransacaoCartaoMostrarDadosResponse> transacaoCartaoDebito(
            @Valid @RequestBody TransacaoCartaoEfetuarDadosRequest dados){
        TransacaoCartaoMostrarDadosResponse response = transacaoCartaoService.realizarTransacaoDebito(dados);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
