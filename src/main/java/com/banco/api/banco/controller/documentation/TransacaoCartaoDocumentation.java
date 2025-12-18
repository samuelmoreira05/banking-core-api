package com.banco.api.banco.controller.documentation;

import com.banco.api.banco.controller.transacaoCartao.request.TransacaoCartaoEfetuarDadosRequest;
import com.banco.api.banco.controller.transacaoCartao.response.TransacaoCartaoMostrarDadosResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Transações de Cartão", description = "Endpoints para realização de transações via cartão (Crédito e Débito)")
public interface TransacaoCartaoDocumentation {

    @Operation(summary = "Realiza uma transação de débito",
            description = "Debita o valor diretamente da conta vinculada ao cartão, se houver saldo.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Transação de débito realizada com sucesso."),
            @ApiResponse(responseCode = "400", description = "Erro de validação (Saldo insuficiente, senha incorreta, cartão bloqueado, conta inativa)."),
            @ApiResponse(responseCode = "404", description = "Cartão não encontrado."),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor.")
    })
    ResponseEntity<TransacaoCartaoMostrarDadosResponse> transacaoCartaoDebito(
            @Valid @RequestBody TransacaoCartaoEfetuarDadosRequest dados
    );

    @Operation(summary = "Realiza uma transação de crédito",
            description = "Adiciona o valor à fatura do cartão, se houver limite disponível.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Transação de crédito realizada com sucesso."),
            @ApiResponse(responseCode = "400", description = "Erro de validação (Limite insuficiente, senha incorreta, cartão bloqueado)."),
            @ApiResponse(responseCode = "404", description = "Cartão não encontrado."),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor.")
    })
    ResponseEntity<TransacaoCartaoMostrarDadosResponse> transacaoCartaoCredito(
            @Valid @RequestBody TransacaoCartaoEfetuarDadosRequest dados
    );
}