package com.banco.api.banco.mapper;

import com.banco.api.banco.controller.transacao.request.TransacaoEfetuarDadosRequest;
import com.banco.api.banco.controller.transacao.response.TransacaoMostrarDadosResponse;
import com.banco.api.banco.model.entity.Conta;
import com.banco.api.banco.model.entity.Transacao;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Slf4j
@AllArgsConstructor
public class TransacaoMapper {

    public Transacao toEntity(Conta conta, TransacaoEfetuarDadosRequest dados, BigDecimal saldoAnterior) {
        Transacao transacao = Transacao.builder()
                .conta(conta)
                .tipo(dados.tipo())
                .valor(dados.valor())
                .saldoAnterior(saldoAnterior)
                .build();

        return transacao;
    }

    public TransacaoMostrarDadosResponse toResponse(Transacao transacao) {
        if (transacao == null) {
            return null;
        }
        return new TransacaoMostrarDadosResponse(transacao);
    }
}
