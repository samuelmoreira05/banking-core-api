package com.banco.api.banco.controller.documentation;

// ... imports

import com.banco.api.banco.controller.cliente.request.ClienteAtualizarDadosRequest;
import com.banco.api.banco.controller.cliente.request.ClienteCadastroDadosRequest;
import com.banco.api.banco.controller.cliente.response.ClienteListagemDadosResponse;
import com.banco.api.banco.controller.cliente.response.ClienteMostrarDadosResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "Clientes", description = "Endpoints para o gerenciamento de clientes")
public interface ClienteDocumentation {

    @Operation(summary = "Cadastra um novo cliente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cliente cadastrado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos."),
            @ApiResponse(responseCode = "409", description = "Conflito: CPF ou e-mail já cadastrado.")
    })
    ResponseEntity<ClienteMostrarDadosResponse> cadastrar(
            @RequestBody ClienteCadastroDadosRequest dados);

    @Operation(summary = "Lista os clientes cadastrados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listagem de clientes retornada com sucesso."),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor.")
    })
    ResponseEntity<Page<ClienteListagemDadosResponse>> listar(
            Pageable pageable);

    @Operation(summary = "Atualiza um cliente por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente atualizado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos."),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado pelo ID informado.")
    })
    ResponseEntity<ClienteMostrarDadosResponse> atualizar(
            @PathVariable Long id,
            @RequestBody ClienteAtualizarDadosRequest dados);

    @Operation(summary = "Bloqueia um cliente por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cliente bloqueado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado pelo ID informado."),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor.")
    })
    ResponseEntity<Void> bloquear(
            @PathVariable Long id
    );

    @Operation(summary = "Marca um cliente como inadimplente por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Status de inadimplência do cliente atualizado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado pelo ID informado."),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor.")
    })
    ResponseEntity<Void> inadimplencia(
            @PathVariable Long id
    );
}