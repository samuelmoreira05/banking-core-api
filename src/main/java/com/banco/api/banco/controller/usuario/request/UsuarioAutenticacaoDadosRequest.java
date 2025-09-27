package com.banco.api.banco.controller.usuario.request;

public record UsuarioAutenticacaoDadosRequest(
        String login,
        String senha
) {
}
