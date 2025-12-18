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
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class TransacaoCartaoService {

    private final PasswordEncoder passwordEncoder;
    private final CartaoRepository cartaoRepository;
    private final TransacaoRepository transacaoRepository;
    private final TransacaoMapper transacaoMapper;
    private final FaturaRepository faturaRepository;
    private final FaturaMapper faturaMapper;

    public TransacaoCartaoService(PasswordEncoder passwordEncoder, CartaoRepository cartaoRepository, TransacaoRepository transacaoRepository, TransacaoMapper transacaoMapper, FaturaRepository faturaRepository, FaturaMapper faturaMapper) {
        this.passwordEncoder = passwordEncoder;
        this.cartaoRepository = cartaoRepository;
        this.transacaoRepository = transacaoRepository;
        this.transacaoMapper = transacaoMapper;
        this.faturaRepository = faturaRepository;
        this.faturaMapper = faturaMapper;
    }

    @Transactional
    public TransacaoCartaoMostrarDadosResponse realizarTransacaoDebito(TransacaoCartaoEfetuarDadosRequest dados){
        Cartao cartao = buscaNumeroCartao(dados.numeroCartao());

        validarDadosCartao(cartao, dados.senha(), TipoCartao.DEBITO);

        Conta conta = cartao.getConta();
        BigDecimal valorAntes = conta.getSaldo();

        conta.executarTransacao(TipoTransacao.SAQUE, dados.valor());

        Transacao transacao = transacaoMapper.toEntityDebito(conta, dados.valor(), valorAntes, dados.descricao());

        transacao = transacaoRepository.save(transacao);

        return transacaoMapper.toCartaoResponse(transacao);
    }

    @Transactional
    public TransacaoCartaoMostrarDadosResponse realizarTransacaoCredito(TransacaoCartaoEfetuarDadosRequest dados) {
        Cartao cartao = buscaNumeroCartao(dados.numeroCartao());

        validarDadosCartao(cartao, dados.senha(), TipoCartao.CREDITO);

        Fatura fatura = buscaOuCriaFatura(cartao);

        validarLimiteDisponivel(cartao, fatura, dados.valor());

        fatura.setValorTotal(fatura.getValorTotal().add(dados.valor()));
        faturaRepository.save(fatura);

        Transacao transacao = transacaoMapper.toEntityCredito(fatura, dados.valor(), dados.descricao());
        transacao = transacaoRepository.save(transacao);

        return transacaoMapper.toCartaoResponse(transacao);
    }

    private Cartao buscaNumeroCartao(String numeroCartao){
        return cartaoRepository.findByNumeroCartao(numeroCartao)
                .orElseThrow(() -> new EntityNotFoundException("Cartão nao encontrado"));
    }

    private void validarDadosCartao(Cartao cartao, String senhaDigitada, TipoCartao tipoEsperado) {
        if (cartao.getStatus() != StatusCartao.CARTAO_ATIVO) {
            throw new RegraDeNegocioException("O cartão precisa estar ativo.");
        }
        if (!passwordEncoder.matches(senhaDigitada, cartao.getSenha())) {
            throw new RegraDeNegocioException("Senha incorreta.");
        }
        if (cartao.getConta().getStatus() != StatusConta.ATIVO) {
            throw new RegraDeNegocioException("Conta vinculada inativa.");
        }
        if (cartao.getTipoCartao() != tipoEsperado) {
            throw new RegraDeNegocioException("Tipo de cartão inválido para operação de " + tipoEsperado);
        }
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
