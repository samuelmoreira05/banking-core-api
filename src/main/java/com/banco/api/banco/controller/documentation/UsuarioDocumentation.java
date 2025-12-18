package com.banco.api.banco.controller.documentation;

import com.banco.api.banco.controller.usuario.request.UsuarioAutenticacaoDadosRequest;
import com.banco.api.banco.controller.usuario.response.GeraTokenResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Autenticação", description = "Endpoint responsável pelo login e geração de tokens JWT")
public interface UsuarioDocumentation {

    @Operation(summary = "Realiza o login do usuário",
            description = "Recebe login e senha, autentica no sistema e retorna um Token JWT para uso nas requisições protegidas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Autenticação realizada com sucesso. Token retornado."),
            @ApiResponse(responseCode = "400", description = "Dados de requisição inválidos (campos vazios)."),
            @ApiResponse(responseCode = "403", description = "Falha na autenticação (Usuário não encontrado ou senha incorreta)."),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor.")
    })
    ResponseEntity<GeraTokenResponse> loginUsuario(
            @Valid @RequestBody UsuarioAutenticacaoDadosRequest dados
    );
}