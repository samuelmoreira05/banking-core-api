package com.banco.api.banco.controller.documentation;

import com.banco.api.banco.controller.cartao.request.CartaoCreditoCriarDadosRequest;
import com.banco.api.banco.controller.cartao.request.CartaoDebitoCriarDadosRequest;
import com.banco.api.banco.controller.cartao.response.CartaoCreditoMostrarDadosResponse;
import com.banco.api.banco.controller.cartao.response.CartaoDebitoMostrarDadosResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Cartões", description = "Endpoints responsaveis por cartoes sendo eles de credito ou debito")
public interface CartaoDocumentation {

    @Operation(summary = "Solicita um cartao de debito")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cartao de debito solicitado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos."),
            @ApiResponse(responseCode = "500", description = "Internal error.")
    })
    ResponseEntity<CartaoDebitoMostrarDadosResponse> solicitaCartaoDebito(
            @RequestBody CartaoDebitoCriarDadosRequest dados);

    @Operation(summary = "Solicita um cartao de credito")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cartao de credito solicitado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos."),
            @ApiResponse(responseCode = "500", description = "Internal error.")
    })
    ResponseEntity<CartaoCreditoMostrarDadosResponse> solicitaCartaoCredito(
            @RequestBody CartaoCreditoCriarDadosRequest dados);
}
