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
    private final FaturaService faturaService;
    private final TransacaoMapper transacaoMapper;
    private final List<ValidadorDadosCartao> validarDadosCartao;
    private final TransacaoService transacaoService;

    public TransacaoCartaoService(CartaoService cartaoService, FaturaService faturaService, TransacaoMapper transacaoMapper, List<ValidadorDadosCartao> validarDadosCartao, TransacaoService transacaoService) {
        this.cartaoService = cartaoService;
        this.faturaService = faturaService;
        this.transacaoMapper = transacaoMapper;
        this.validarDadosCartao = validarDadosCartao;
        this.transacaoService = transacaoService;
    }

    @Transactional
    public TransacaoCartaoMostrarDadosResponse realizarTransacaoDebito(TransacaoCartaoEfetuarDadosRequest dados){
        Cartao cartao = cartaoService.buscaNumeroCartao(dados.numeroCartao());

        validarDadosCartao.forEach(v -> v.validarDados(cartao, dados.senha(), TipoCartao.DEBITO));

        Conta conta = cartao.getConta();
        BigDecimal valorAntes = conta.getSaldo();

        conta.executarTransacao(TipoTransacao.SAQUE, dados.valor());

        Transacao transacao = transacaoMapper.toEntityDebito(conta, dados.valor(), valorAntes, dados.descricao());

        transacao = transacaoService.salvar(transacao);

        return transacaoMapper.toCartaoResponse(transacao);
    }

    @Transactional
    public TransacaoCartaoMostrarDadosResponse realizarTransacaoCredito(TransacaoCartaoEfetuarDadosRequest dados) {
        Cartao cartao = cartaoService.buscaNumeroCartao(dados.numeroCartao());

        validarDadosCartao.forEach(v -> v.validarDados(cartao, dados.senha(),TipoCartao.CREDITO));

        Fatura faturaAtualizada = faturaService.processarCompraCredito(cartao, dados.valor());

        Transacao transacao = transacaoMapper.toEntityCredito(faturaAtualizada, dados.valor(), dados.descricao());
        transacao = transacaoService.salvar(transacao);

        return transacaoMapper.toCartaoResponse(transacao);
    }



}
