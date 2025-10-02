package com.banco.api.banco.service;

import com.banco.api.banco.controller.transacao.request.TransacaoEfetuarDadosRequest;
import com.banco.api.banco.controller.transacao.response.TransacaoMostrarDadosResponse;
import com.banco.api.banco.enums.TipoTransacao;
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
     public TransacaoMostrarDadosResponse efetuarTransacao(TransacaoEfetuarDadosRequest dados){
        if (dados.valor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor da transacao deve ser positivo");
        }

        Conta conta = contaRepository.findById(dados.contaId())
                .orElseThrow(() -> new IllegalStateException("A conta não foi encontrada na base de dados"));

        BigDecimal saldoAntes = conta.getSaldo();

        if (dados.tipo() == TipoTransacao.DEPOSITO){
            conta.depositar(dados.valor());
        } else if (dados.tipo() == TipoTransacao.SAQUE) {
            conta.sacar(dados.valor());
        }else {
            throw new UnsupportedOperationException("Tipo de transação não suportada: " + dados.tipo());
        }

        Transacao transacao = salvarTransacao(conta, dados, saldoAntes);

        return new TransacaoMostrarDadosResponse(transacao);
    }

    private Transacao salvarTransacao(Conta conta, TransacaoEfetuarDadosRequest dados, BigDecimal saldoAnterior){
        Transacao transacao = Transacao.builder()
                .conta(conta)
                .tipo(dados.tipo())
                .valor(dados.valor())
                .saldoAnterior(saldoAnterior)
                .build();
        return repository.save(transacao);
    }
}

