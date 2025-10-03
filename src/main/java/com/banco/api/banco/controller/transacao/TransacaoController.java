package com.banco.api.banco.controller.transacao;

import com.banco.api.banco.controller.documentation.TransacaoDocumentation;
import com.banco.api.banco.controller.transacao.request.TransacaoEfetuarDadosRequest;
import com.banco.api.banco.controller.transacao.response.TransacaoMostrarDadosResponse;
import com.banco.api.banco.service.TransacaoService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/transacoes")
@SecurityRequirement(name = "barer-token")
public class TransacaoController implements TransacaoDocumentation {

    private final TransacaoService service;

    public TransacaoController(TransacaoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<TransacaoMostrarDadosResponse> efetuarTransacao(
            @Valid @RequestBody TransacaoEfetuarDadosRequest dados
    ) {
        TransacaoMostrarDadosResponse response = service.efetuarTransacao(dados);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
