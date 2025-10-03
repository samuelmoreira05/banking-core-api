package com.banco.api.banco.controller.documentation;

import com.banco.api.banco.controller.conta.request.ContaCadastroDadosRequest;
import com.banco.api.banco.controller.conta.response.ContaListagemDadosResponse;
import com.banco.api.banco.controller.conta.response.ContaMostrarDadosResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Contas", description = "Endpoints para o gerenciamento de contas")
public interface ContaDocumentation {

    @Operation(summary = "Cria uma nova conta vinculada ao cliente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Conta criada com sucesso."),
            @ApiResponse(responseCode = "400", description = "Cliente não encontrado na base de dados."),
            @ApiResponse(responseCode = "500", description = "Internal error.")
    })
    ResponseEntity<ContaMostrarDadosResponse> cadastrar(
            @Valid
            @RequestBody ContaCadastroDadosRequest dados
            );

    @Operation(summary = "Lista as contas já cadastradas na base de dados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listagem das contas seguidas de dados dos clientes."),
            @ApiResponse(responseCode = "500", description = "Internal error.")
    })
    ResponseEntity<Page<ContaListagemDadosResponse>> listar(
            Pageable pageable
    );

    @Operation(summary = "Encerra a conta de um cliente por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conta encerrada com sucesso."),
            @ApiResponse(responseCode = "404", description = "Conta não enconrada pelo ID."),
            @ApiResponse(responseCode = "500", description = "Internal error."),
    })
    ResponseEntity<Void> encerrar(
            @PathVariable Long id
    );

    @Operation(summary = "Suspende a conta de um cliente por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conta suspensa com sucesso."),
            @ApiResponse(responseCode = "404", description = "Conta não enconrada pelo ID."),
            @ApiResponse(responseCode = "500", description = "Internal error."),
    })
    ResponseEntity<Void> suspender(
            @PathVariable Long id
    );

    @Operation(summary = "Ativa a conta de um cliente por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conta ativada com sucesso."),
            @ApiResponse(responseCode = "404", description = "Conta não enconrada pelo ID."),
            @ApiResponse(responseCode = "500", description = "Internal error."),
    })
    ResponseEntity<Void> ativar(
            @PathVariable Long id
    );
}
