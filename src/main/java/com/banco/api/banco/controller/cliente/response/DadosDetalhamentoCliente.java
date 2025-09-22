package com.banco.api.banco.controller.cliente.response;

import com.banco.api.banco.enums.StatusConta;
import com.banco.api.banco.model.entity.Cliente;

import java.time.LocalDate;

public record DadosDetalhamentoCliente(
        Long id,
        String nome,
        String email,
        LocalDate dataNascimento,
        String cpf,
        String telefone,
        String endereco,
        StatusConta status
) {
    public DadosDetalhamentoCliente(Cliente cliente) {
        this(
                cliente.getId(),
                cliente.getNome(),
                cliente.getEmail(),
                cliente.getDataNascimento(),
                cliente.getCpf(),
                cliente.getTelefone(),
                cliente.getEndereco(),
                cliente.getStatus()
        );
    }
}
