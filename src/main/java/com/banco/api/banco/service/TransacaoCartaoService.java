package com.banco.api.banco.service;

import com.banco.api.banco.controller.transacaoCartao.request.TransacaoCartaoEfetuarDadosRequest;
import com.banco.api.banco.controller.transacaoCartao.response.TransacaoCartaoMostrarDadosResponse;
import com.banco.api.banco.enums.*;
import com.banco.api.banco.infra.exception.RegraDeNegocioException;
import com.banco.api.banco.mapper.FaturaMapper;
import com.banco.api.banco.mapper.TransacaoMapper;
import com.banco.api.banco.model.entity.Cartao;
import com.banco.api.banco.model.entity.Conta;
import com.banco.api.banco.model.entity.Fatura;
import com.banco.api.banco.model.entity.Transacao;
import com.banco.api.banco.repository.CartaoRepository;
import com.banco.api.banco.repository.FaturaRepository;
import com.banco.api.banco.repository.TransacaoRepository;
import com.banco.api.banco.service.validadores.cartaoValidaDados.ValidadorDadosCartao;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TransacaoCartaoService {

    private final CartaoService cartaoService;
    private final TransacaoRepository transacaoRepository;
    private final TransacaoMapper transacaoMapper;
    private final FaturaRepository faturaRepository;
    private final FaturaMapper faturaMapper;
    private final List<ValidadorDadosCartao> validarDadosCartao;

    public TransacaoCartaoService(CartaoService cartaoService, TransacaoRepository transacaoRepository, TransacaoMapper transacaoMapper, FaturaRepository faturaRepository, FaturaMapper faturaMapper, List<ValidadorDadosCartao> validarDadosCartao) {
        this.cartaoService = cartaoService;
        this.transacaoRepository = transacaoRepository;
        this.transacaoMapper = transacaoMapper;
        this.faturaRepository = faturaRepository;
        this.faturaMapper = faturaMapper;
        this.validarDadosCartao = validarDadosCartao;
    }

    @Transactional
    public TransacaoCartaoMostrarDadosResponse realizarTransacaoDebito(TransacaoCartaoEfetuarDadosRequest dados){
        Cartao cartao = cartaoService.buscaNumeroCartao(dados.numeroCartao());

        validarDadosCartao.forEach(v -> v.validarDados(cartao, dados.senha(), TipoCartao.DEBITO));

        Conta conta = cartao.getConta();
        BigDecimal valorAntes = conta.getSaldo();

        conta.executarTransacao(TipoTransacao.SAQUE, dados.valor());

        Transacao transacao = transacaoMapper.toEntityDebito(conta, dados.valor(), valorAntes, dados.descricao());

        transacao = transacaoRepository.save(transacao);

        return transacaoMapper.toCartaoResponse(transacao);
    }

    @Transactional
    public TransacaoCartaoMostrarDadosResponse realizarTransacaoCredito(TransacaoCartaoEfetuarDadosRequest dados) {
        Cartao cartao = cartaoService.buscaNumeroCartao(dados.numeroCartao());

        validarDadosCartao.forEach(v -> v.validarDados(cartao, dados.senha(),TipoCartao.CREDITO));

        Fatura fatura = buscaOuCriaFatura(cartao);

        validarLimiteDisponivel(cartao, fatura, dados.valor());

        fatura.setValorTotal(fatura.getValorTotal().add(dados.valor()));
        faturaRepository.save(fatura);

        Transacao transacao = transacaoMapper.toEntityCredito(fatura, dados.valor(), dados.descricao());
        transacao = transacaoRepository.save(transacao);

        return transacaoMapper.toCartaoResponse(transacao);
    }

    private void validarLimiteDisponivel(Cartao cartao, Fatura fatura, BigDecimal valorCompra) {
        BigDecimal limiteConsumido = fatura.getValorTotal();
        BigDecimal limiteDisponivel = cartao.getLimiteCredito().subtract(limiteConsumido);

        if (valorCompra.compareTo(limiteDisponivel) > 0) {
            throw new RegraDeNegocioException("Limite insuficiente. Disponível: " + limiteDisponivel);
        }
    }

    private Fatura buscaOuCriaFatura(Cartao cartao){
        return faturaRepository.findByCartaoAndStatus(cartao, StatusFatura.ABERTA)
                .orElseGet(() -> criarNovaFatura(cartao));
    }

    private Fatura criarNovaFatura(Cartao cartao){
        Fatura novaFatura = faturaMapper.toEntityFaturaInicial(cartao);

        return faturaRepository.save(novaFatura);
    }
}
