package com.banco.api.banco.service;

import com.banco.api.banco.controller.transacaoCartao.request.TransacaoCartaoEfetuarDadosRequest;
import com.banco.api.banco.enums.StatusCartao;
import com.banco.api.banco.enums.StatusConta;
import com.banco.api.banco.enums.TipoCartao;
import com.banco.api.banco.enums.TipoTransacao;
import com.banco.api.banco.infra.exception.RegraDeNegocioException;
import com.banco.api.banco.mapper.TransacaoMapper;
import com.banco.api.banco.model.entity.Cartao;
import com.banco.api.banco.model.entity.Conta;
import com.banco.api.banco.model.entity.Transacao;
import com.banco.api.banco.repository.CartaoRepository;
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

    public TransacaoCartaoService(PasswordEncoder passwordEncoder, CartaoRepository cartaoRepository, TransacaoRepository transacaoRepository, TransacaoMapper transacaoMapper) {
        this.passwordEncoder = passwordEncoder;
        this.cartaoRepository = cartaoRepository;
        this.transacaoRepository = transacaoRepository;
        this.transacaoMapper = transacaoMapper;
    }

    @Transactional
    public void realizarTransacaoDebito(TransacaoCartaoEfetuarDadosRequest dados){
        Cartao cartao = buscaNumeroCartao(dados.numeroCartao());

        validarOperacaoDebito(cartao, dados.senha());

        Conta conta = cartao.getConta();
        BigDecimal valorAntes = conta.getSaldo();

        conta.executarTransacao(TipoTransacao.SAQUE, dados.valor());

        Transacao transacao = transacaoMapper.toEntityDebito(conta, dados.valor(), valorAntes, dados.descricao());

        transacaoRepository.save(transacao);
    }

    private Cartao buscaNumeroCartao(String numeroCartao){
        return cartaoRepository.findByNumeroCartao(numeroCartao)
                .orElseThrow(() -> new EntityNotFoundException("Cartão nao encontrado"));
    }

    private void validarOperacaoDebito(Cartao cartao, String senhaDigitada) {
        if (cartao.getStatus() != StatusCartao.CARTAO_ATIVO) {
            throw new RegraDeNegocioException("O cartão precisa estar ativo.");
        }
        if (!passwordEncoder.matches(senhaDigitada, cartao.getSenha())) {
            throw new RegraDeNegocioException("Senha incorreta.");
        }
        if (cartao.getTipoCartao() != TipoCartao.DEBITO) {
            throw new RegraDeNegocioException("Cartão inválido para débito.");
        }
        if (cartao.getConta().getStatus() != StatusConta.ATIVO) {
            throw new RegraDeNegocioException("Conta vinculada inativa.");
        }
    }
}
