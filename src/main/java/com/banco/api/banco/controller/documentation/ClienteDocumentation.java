package com.banco.api.banco.controller.documentation;

import com.banco.api.banco.controller.cliente.request.ClienteAtualizarDadosRequest;
import com.banco.api.banco.controller.cliente.request.ClienteCadastroDadosRequest;
import com.banco.api.banco.controller.cliente.response.ClienteListagemDadosResponse;
import com.banco.api.banco.controller.cliente.response.ClienteMostrarDadosResponse;
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

@Tag(name = "Clientes", description = "Endpoints para o gerenciamento de clientes")
public interface ClienteDocumentation {

    @Operation(
            summary = "Cadastra um novo cliente",
            description = "Este endpoint cria um novo cliente no sistema a partir dos dados fornecidos."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Cliente cadastrado com sucesso."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Requisição inválida. Verifique os dados enviados."
            ),
    })
    ResponseEntity<ClienteMostrarDadosResponse> cadastrar(
            @RequestBody ClienteCadastroDadosRequest dados);

    @Operation(
            summary = "Lista os clientes cadastrados",
            description = "Este endpoint lista todos os clientes que ja foram cadastrados e estão na base de dados."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Clientes serao listados."
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal error."
            ),
    })
    ResponseEntity<Page<ClienteListagemDadosResponse>> listar(
            Pageable pageable);

    @Operation(
            summary = "Atualiza um cliente",
            description = "Este endpoint atualiza um cliente ja existente na base de dados com base no seu ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Cliente será atualizado"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados invalidos foram enviados."
            ),
    })
    ResponseEntity atualizar(
            @Valid
            @PathVariable Long id,
            @RequestBody ClienteAtualizarDadosRequest dados);

    @Operation(
            summary = "Bloqueia um cliente",
            description = "Este endpoint é responsavel por bloquear um cliente com base no seu ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Cliente teve seu status alterado para bloqueado"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal error."
            ),
    })
    ResponseEntity<Void> bloquear(
            @PathVariable Long id
    );

    @Operation(
            summary = "Deixa um cliente inadimplente",
            description = "Este endpoint é responsavel por deixar um cliente inadimplente com base no seu ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Cliente teve seu status alterado para inadimplente"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal error."
            ),
    })
    ResponseEntity inadimplencia(
            @PathVariable Long id
    );
}