package com.banco.api.banco.controller.cliente.response;

import com.banco.api.banco.enums.StatusConta;
import com.banco.api.banco.model.entity.Cliente;

import java.time.LocalDate;

public record DadosListagemClientes(
        Long id,
        String nome,
        String email,
        LocalDate dataNascimento,
        StatusConta status
) {
    public DadosListagemClientes(Cliente cliente) {
        this(cliente.getId(), cliente.getNome(), cliente.getEmail(), cliente.getDataNascimento(), cliente.getStatus());
    }
}
