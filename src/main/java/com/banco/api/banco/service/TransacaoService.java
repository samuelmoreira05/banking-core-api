package com.banco.api.banco.service;

import com.banco.api.banco.controller.transacao.request.TransacaoEfetuarDadosRequest;
import com.banco.api.banco.controller.transacao.response.TransacaoMostrarDadosResponse;
import com.banco.api.banco.model.entity.Conta;
import com.banco.api.banco.model.entity.Transacao;
import com.banco.api.banco.repository.ContaRepository;
import com.banco.api.banco.repository.TransacaoRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class TransacaoService {

    private final TransacaoRepository repository;
    private final ContaRepository contaRepository;

    public TransacaoService(TransacaoRepository repository, ContaRepository contaRepository) {
        this.repository = repository;
        this.contaRepository = contaRepository;
    }

    @Transactional
    public TransacaoMostrarDadosResponse deposito(TransacaoEfetuarDadosRequest dados){
        Conta conta = contaRepository.findById(dados.contaId())
                .orElseThrow(() -> new EntityNotFoundException("A conta não foi encontrada"));

        BigDecimal saldoAntes = conta.getSaldo();
        conta.depositar(dados.valor());
        Transacao transacao = Transacao.builder()
                .conta(conta)
                .tipo(dados.tipo())
                .valor(dados.valor())
                .saldoAnterior(saldoAntes)
                .build();

        repository.save(transacao);
        return new TransacaoMostrarDadosResponse(transacao);
    }

    @Transactional
    public TransacaoMostrarDadosResponse saque(TransacaoEfetuarDadosRequest dados){
        Conta conta = contaRepository.findById(dados.contaId())
                .orElseThrow(() -> new EntityNotFoundException("A conta não foi encontrada"));

        BigDecimal saldoAntes = conta.getSaldo();
        conta.sacar(dados.valor());
        Transacao transacao = Transacao.builder()
                .conta(conta)
                .tipo(dados.tipo())
                .valor(dados.valor())
                .saldoAnterior(saldoAntes)
                .build();

        repository.save(transacao);
        return new TransacaoMostrarDadosResponse(transacao);
    }
}

