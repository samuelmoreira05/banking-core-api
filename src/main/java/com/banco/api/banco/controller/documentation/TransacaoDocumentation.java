package com.banco.api.banco.controller.documentation;

import com.banco.api.banco.controller.transacao.request.TransacaoEfetuarDadosRequest;
import com.banco.api.banco.controller.transacao.response.TransacaoMostrarDadosResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ResourceBundle;

@Tag(name = "Transacoes", description = "Endpoints que gerenciam as transacoes")
public interface TransacaoDocumentation {

    @Operation(summary = "Responsavel por efetuar transacao de saque ou deposito")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Transacao bem sucedida."),
            @ApiResponse(responseCode = "400", description = "Operação não condiz com as disponiveis."),
            @ApiResponse(responseCode = "500", description = "Internal error.")
    })
    ResponseEntity<TransacaoMostrarDadosResponse> efetuarTransacao(
            @Valid
            @RequestBody TransacaoEfetuarDadosRequest dados
    );
}
