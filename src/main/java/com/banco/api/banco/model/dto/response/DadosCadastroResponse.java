package com.banco.api.banco.model.dto.response;

import com.banco.api.banco.enums.StatusCliente;
import com.banco.api.banco.model.entity.Cliente;

public record DadosCadastroResponse(
        Long id,
        String nome,
        String email,
        String telefone,
        StatusCliente status
) {
    public DadosCadastroResponse(Cliente cliente) {
        this(
                cliente.getId(),
                cliente.getNome(),
                cliente.getEmail(),
                cliente.getTelefone(),
                cliente.getStatus()
        );
    }
}
