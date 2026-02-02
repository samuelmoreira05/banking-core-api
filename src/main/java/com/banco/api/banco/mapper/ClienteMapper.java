package com.banco.api.banco.mapper;

import com.banco.api.banco.controller.cliente.request.ClienteCadastroDadosRequest;
import com.banco.api.banco.controller.cliente.response.ClienteListagemDadosResponse;
import com.banco.api.banco.controller.cliente.response.ClienteMostrarDadosResponse;
import com.banco.api.banco.model.entity.Cliente;
import com.banco.api.banco.model.entity.Usuario;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.awt.print.Pageable;

@Component
@Slf4j
@AllArgsConstructor
public class ClienteMapper {

    public Cliente toEntity(ClienteCadastroDadosRequest dados, String senhaCriptografada){
        Usuario usuario = Usuario.builder()
                .login(dados.login())
                .senha(senhaCriptografada)
                .build();

        Cliente cliente = Cliente.builder()
                .nome(dados.nome())
                .email(dados.email())
                .cpf(dados.cpf())
                .telefone(dados.telefone())
                .endereco(dados.endereco())
                .dataNascimento(dados.dataNascimento())
                .usuario(usuario)
                .build();

        return cliente;
    }

    public ClienteMostrarDadosResponse toClienteResponse(Cliente cliente){
        return new ClienteMostrarDadosResponse(
                cliente.getId(),
                cliente.getNome(),
                cliente.getEndereco(),
                cliente.getEmail(),
                cliente.getTelefone(),
                cliente.getStatus()
        );
    }

    public ClienteListagemDadosResponse toClienteListagemResponse(Cliente cliente) {
        return new ClienteListagemDadosResponse(cliente);
    }
}
