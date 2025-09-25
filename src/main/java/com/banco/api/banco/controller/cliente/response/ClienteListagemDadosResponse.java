package com.banco.api.banco.controller.cliente.response;

import com.banco.api.banco.enums.StatusCliente;
import com.banco.api.banco.model.entity.Cliente;

import java.time.LocalDate;

public record ClienteListagemDadosResponse(
        Long id,
        String nome,
        String email,
        LocalDate dataNascimento,
        StatusCliente status
) {
    public ClienteListagemDadosResponse(Cliente cliente) {
        this(cliente.getId(), cliente.getNome(), cliente.getEmail(), cliente.getDataNascimento(), cliente.getStatus());
    }
}
