package com.banco.api.banco.mapper;

import com.banco.api.banco.controller.transacao.request.TransacaoEfetuarDadosRequest;
import com.banco.api.banco.controller.transacao.response.TransacaoMostrarDadosResponse;
import com.banco.api.banco.controller.transacaoCartao.response.TransacaoCartaoMostrarDadosResponse;
import com.banco.api.banco.enums.TipoTransacao;
import com.banco.api.banco.model.entity.Conta;
import com.banco.api.banco.model.entity.Fatura;
import com.banco.api.banco.model.entity.Transacao;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    public Transacao toEntityDebito(Conta conta, BigDecimal valor, BigDecimal saldoAnterior, String descricao) {
        Transacao transacao = Transacao.builder()
                .conta(conta)
                .valor(valor)
                .saldoAnterior(saldoAnterior)
                .tipo(TipoTransacao.SAQUE)
                .descricao(descricao)
                .dataTransacao(LocalDateTime.now())
                .build();

        return transacao;
    }

    public Transacao toEntityCredito(Fatura fatura, BigDecimal valor, String descricao){
        return Transacao.builder()
                .conta(fatura.getCartao().getConta())
                .fatura(fatura)
                .valor(valor)
                .tipo(TipoTransacao.CREDITO)
                .descricao(descricao)
                .dataTransacao(LocalDateTime.now())
                .saldoAnterior(BigDecimal.ZERO)
                .build();
    }

    public TransacaoCartaoMostrarDadosResponse toCartaoResponse(Transacao transacao) {
        return new TransacaoCartaoMostrarDadosResponse(
                transacao.getId(),
                transacao.getValor(),
                transacao.getDescricao(),
                transacao.getDataTransacao(),
                transacao.getConta().getSaldo()
        );
    }

    public TransacaoMostrarDadosResponse toResponse(Transacao transacao) {
        if (transacao == null) {
            return null;
        }
        return new TransacaoMostrarDadosResponse(transacao);
    }
}
