package com.banco.api.banco.service;

import com.banco.api.banco.controller.transacao.request.TransacaoEfetuarDadosRequest;
import com.banco.api.banco.controller.transacao.response.TransacaoMostrarDadosResponse;
import com.banco.api.banco.enums.StatusConta;
import com.banco.api.banco.infra.exception.RegraDeNegocioException;
import com.banco.api.banco.mapper.TransacaoMapper;
import com.banco.api.banco.model.entity.Conta;
import com.banco.api.banco.model.entity.Transacao;
import com.banco.api.banco.repository.TransacaoRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class TransacaoService {

    private final ContaService contaService;
    private final TransacaoRepository repository;
    private final TransacaoMapper transacaoMapper;

    public TransacaoService(TransacaoRepository repository,
                            ContaService contaService,
                            TransacaoMapper transacaoMapper) {
        this.repository = repository;
        this.contaService = contaService;
        this.transacaoMapper = transacaoMapper;
    }

    @Transactional
    public TransacaoMostrarDadosResponse efetuarTransacao(TransacaoEfetuarDadosRequest dados) {
        Conta conta = contaService.buscarContaPorId(dados.contaId());

        validarStatusConta(conta);

        BigDecimal saldoAntes = conta.getSaldo();
        conta.executarTransacao(dados.tipo(), dados.valor());

        Transacao transacao = transacaoMapper.toEntity(conta, dados, saldoAntes);

        transacao = salvar(transacao);

        return transacaoMapper.toResponse(transacao);
    }

    @Transactional
    public Transacao salvar(Transacao transacao) {
        return repository.save(transacao);
    }

    private void validarStatusConta(Conta conta) {
        if (conta.getStatus() != StatusConta.ATIVO){
            throw new RegraDeNegocioException("Transações não são permitidas em contas com status: " + conta.getStatus());
        }
    }
}

