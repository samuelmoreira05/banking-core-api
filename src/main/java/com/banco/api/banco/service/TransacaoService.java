package com.banco.api.banco.service;

import com.banco.api.banco.controller.transacao.request.DadosEfetuarTransacaoRequest;
import com.banco.api.banco.controller.transacao.response.DadosMostrarTransacaoResponse;
import com.banco.api.banco.enums.StatusConta;
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
    public DadosMostrarTransacaoResponse deposito(DadosEfetuarTransacaoRequest dados){
        Conta conta = contaRepository.findById(dados.contaId())
                .orElseThrow(() -> new EntityNotFoundException("A conta não foi encontrada na base de dados"));

        if (conta.getStatus() != StatusConta.ATIVO) {
            throw new IllegalStateException("Conta não pode efetuar transação(nào está ativa)");
        }

        Transacao transacao = repository.save(new Transacao(dados, conta));

        BigDecimal novoSaldo = conta.getSaldo().add(dados.valor());
        conta.setSaldo(novoSaldo);
        contaRepository.save(conta);

        return new DadosMostrarTransacaoResponse(transacao);
    }
}
