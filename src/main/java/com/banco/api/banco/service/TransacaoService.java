package com.banco.api.banco.service;

import com.banco.api.banco.controller.transacao.request.TransacaoEfetuarDadosRequest;
import com.banco.api.banco.controller.transacao.response.TransacaoMostrarDadosResponse;
import com.banco.api.banco.enums.StatusConta;
import com.banco.api.banco.infra.exception.RegraDeNegocioException;
import com.banco.api.banco.mapper.TransacaoMapper;
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
    private final TransacaoMapper transacaoMapper;

    public TransacaoService(TransacaoRepository repository, ContaRepository contaRepository, TransacaoMapper transacaoMapper) {
        this.repository = repository;
        this.contaRepository = contaRepository;
        this.transacaoMapper = transacaoMapper;
    }

    @Transactional
    public TransacaoMostrarDadosResponse efetuarTransacao(TransacaoEfetuarDadosRequest dados) {
        Conta conta = buscarContaPorId(dados.contaId());

        if(conta.getStatus() != StatusConta.ATIVO){
            throw new RegraDeNegocioException("Transacoes não são permitidas em contas com status: " + conta.getStatus());
        }

        BigDecimal saldoAntes = conta.getSaldo();

        conta.executarTransacao(dados.tipo(), dados.valor());

        Transacao transacao = salvarTransacao(conta, dados, saldoAntes);

        return transacaoMapper.toResponse(transacao);
    }

    private Conta buscarContaPorId(Long id) {
        return contaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Conta com ID " + id + " não encontrada."));
    }

    private Transacao salvarTransacao(Conta conta, TransacaoEfetuarDadosRequest dados, BigDecimal saldoAnterior){
        Transacao transacao = transacaoMapper.toEntity(conta, dados, saldoAnterior);
        return repository.save(transacao);
    }
}

