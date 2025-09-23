package com.banco.api.banco.controller.cliente.response;

import com.banco.api.banco.enums.StatusConta;
import com.banco.api.banco.model.entity.Cliente;

public record DadosMostrarCliente(
        Long id,
        String nome,
        String email,
        String telefone,
        StatusConta status
) {
    public DadosMostrarCliente(Cliente cliente) {
        this(
                cliente.getId(),
                cliente.getNome(),
                cliente.getEmail(),
                cliente.getTelefone(),
                cliente.getStatus()
        );
    }
}
