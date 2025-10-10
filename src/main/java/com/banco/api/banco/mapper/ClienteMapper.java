package com.banco.api.banco.mapper;

import com.banco.api.banco.controller.cliente.request.ClienteCadastroDadosRequest;
import com.banco.api.banco.model.entity.Cliente;
import com.banco.api.banco.model.entity.Usuario;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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
}
