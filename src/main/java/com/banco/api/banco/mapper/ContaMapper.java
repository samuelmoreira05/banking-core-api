package com.banco.api.banco.mapper;

import com.banco.api.banco.controller.conta.request.ContaCadastroDadosRequest;
import com.banco.api.banco.controller.conta.response.ContaMostrarDadosResponse;
import com.banco.api.banco.model.entity.Cliente;
import com.banco.api.banco.model.entity.Conta;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class ContaMapper {

    public Conta toEntity(ContaCadastroDadosRequest dados, Cliente cliente){
        Conta conta = Conta.builder()
                .cliente(cliente)
                .tipoConta(dados.tipo())
                .build();

        return conta;
    }

    public ContaMostrarDadosResponse toContaResponse(Conta conta) {
        return new ContaMostrarDadosResponse(
                conta.getNumeroConta(),
                conta.getTipoConta(),
                conta.getAgencia(),
                conta.getSaldo(),
                conta.getStatus(),
                conta.getDataCriacao(),
                conta.getCliente().getId(),
                conta.getCliente().getNome()
        );
    }
}
