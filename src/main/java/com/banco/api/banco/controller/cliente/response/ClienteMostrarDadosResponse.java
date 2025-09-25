package com.banco.api.banco.controller.cliente.response;

import com.banco.api.banco.enums.StatusCliente;
import com.banco.api.banco.model.entity.Cliente;

public record ClienteMostrarDadosResponse(
        Long id,
        String nome,
        String email,
        String telefone,
        StatusCliente status
) {
    public ClienteMostrarDadosResponse(Cliente cliente) {
        this(
                cliente.getId(),
                cliente.getNome(),
                cliente.getEmail(),
                cliente.getTelefone(),
                cliente.getStatus()
        );
    }
}
