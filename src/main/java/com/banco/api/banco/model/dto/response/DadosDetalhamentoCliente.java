package com.banco.api.banco.model.dto.response;

import com.banco.api.banco.enums.StatusCliente;
import com.banco.api.banco.model.dto.request.DadosAtualizarCliente;
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
        StatusCliente status
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
