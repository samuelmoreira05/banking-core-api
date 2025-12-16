package com.banco.api.banco.service;

import com.banco.api.banco.controller.transacaoCartao.request.TransacaoCartaoEfetuarDadosRequest;
import com.banco.api.banco.controller.transacaoCartao.response.TransacaoCartaoMostrarDadosResponse;
import com.banco.api.banco.enums.StatusCartao;
import com.banco.api.banco.enums.StatusConta;
import com.banco.api.banco.enums.StatusFatura;
import com.banco.api.banco.enums.TipoCartao;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransacaoCartaoServiceTest {

    @Mock private PasswordEncoder passwordEncoder;
    @Mock private CartaoRepository cartaoRepository;
    @Mock private TransacaoRepository transacaoRepository;
    @Mock private TransacaoMapper transacaoMapper;
    @Mock private FaturaRepository faturaRepository;
    @Mock private FaturaMapper faturaMapper;

    @InjectMocks private TransacaoCartaoService service;

    @Test
    void realizarCreditoComFaturaExistente() {
        String cartaoNum = "1234567890123456";
        String senha = "123";
        BigDecimal valorCompra = new BigDecimal("100.00");
        BigDecimal limiteCartao = new BigDecimal("1000.00");
        BigDecimal valorFaturaAtual = new BigDecimal("200.00");

        Conta conta = new Conta();
        conta.setStatus(StatusConta.ATIVO);

        Cartao cartao = new Cartao();
        cartao.setNumeroCartao(cartaoNum);
        cartao.setSenha("senhaEnc");
        cartao.setTipoCartao(TipoCartao.CREDITO);
        cartao.setStatus(StatusCartao.CARTAO_ATIVO);
        cartao.setLimiteCredito(limiteCartao);
        cartao.setConta(conta);

        Fatura faturaAberta = new Fatura();
        faturaAberta.setValorTotal(valorFaturaAtual);
        faturaAberta.setStatus(StatusFatura.ABERTA);
        faturaAberta.setCartao(cartao);

        TransacaoCartaoEfetuarDadosRequest request = new TransacaoCartaoEfetuarDadosRequest(cartaoNum, senha, valorCompra, "Teste");

        when(cartaoRepository.findByNumeroCartao(cartaoNum)).thenReturn(Optional.of(cartao));
        when(passwordEncoder.matches(senha, cartao.getSenha())).thenReturn(true);
        when(faturaRepository.findByCartaoAndStatus(cartao, StatusFatura.ABERTA)).thenReturn(Optional.of(faturaAberta));
        when(transacaoMapper.toEntityCredito(any(), any(), any())).thenReturn(new Transacao());
        when(transacaoRepository.save(any())).thenReturn(new Transacao());
        when(transacaoMapper.toCartaoResponse(any())).thenReturn(mock(TransacaoCartaoMostrarDadosResponse.class));

        service.realizarTransacaoCredito(request);

        assertEquals(new BigDecimal("300.00"), faturaAberta.getValorTotal());

        verify(faturaRepository).save(faturaAberta);
        verify(faturaMapper, never()).toEntityFaturaInicial(any());
        verify(transacaoRepository).save(any(Transacao.class));
    }

    @Test
    void realizarCreditoCriandoNovaFatura() {
        String cartaoNum = "1234";
        BigDecimal valorCompra = new BigDecimal("50.00");

        Conta conta = new Conta();
        conta.setStatus(StatusConta.ATIVO);

        Cartao cartao = new Cartao();
        cartao.setNumeroCartao(cartaoNum);
        cartao.setSenha("enc");
        cartao.setStatus(StatusCartao.CARTAO_ATIVO);
        cartao.setTipoCartao(TipoCartao.CREDITO);
        cartao.setLimiteCredito(new BigDecimal("500.00"));
        cartao.setConta(conta);

        Fatura novaFatura = new Fatura();
        novaFatura.setValorTotal(BigDecimal.ZERO);
        novaFatura.setStatus(StatusFatura.ABERTA);

        TransacaoCartaoEfetuarDadosRequest request = new TransacaoCartaoEfetuarDadosRequest(cartaoNum, "123", valorCompra, "Nova");

        when(cartaoRepository.findByNumeroCartao(cartaoNum)).thenReturn(Optional.of(cartao));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        when(faturaRepository.findByCartaoAndStatus(cartao, StatusFatura.ABERTA)).thenReturn(Optional.empty());
        when(faturaMapper.toEntityFaturaInicial(cartao)).thenReturn(novaFatura);
        when(faturaRepository.save(novaFatura)).thenReturn(novaFatura);
        when(transacaoMapper.toEntityCredito(any(), any(), any())).thenReturn(new Transacao());
        when(transacaoMapper.toCartaoResponse(any())).thenReturn(mock(TransacaoCartaoMostrarDadosResponse.class));

        service.realizarTransacaoCredito(request);

        verify(faturaMapper).toEntityFaturaInicial(cartao);
        assertEquals(valorCompra, novaFatura.getValorTotal());
        verify(faturaRepository, atLeastOnce()).save(novaFatura);
    }

    @Test
    void erroLimiteInsuficiente() {
        BigDecimal limiteCartao = new BigDecimal("100.00");
        BigDecimal gastoAtualFatura = new BigDecimal("80.00");
        BigDecimal valorCompra = new BigDecimal("30.00");

        Cartao cartao = new Cartao();
        cartao.setNumeroCartao("123");
        cartao.setSenha("enc");
        cartao.setStatus(StatusCartao.CARTAO_ATIVO);
        cartao.setTipoCartao(TipoCartao.CREDITO);
        cartao.setLimiteCredito(limiteCartao);
        cartao.setConta(new Conta());
        cartao.getConta().setStatus(StatusConta.ATIVO);

        Fatura fatura = new Fatura();
        fatura.setValorTotal(gastoAtualFatura);
        fatura.setStatus(StatusFatura.ABERTA);

        TransacaoCartaoEfetuarDadosRequest request = new TransacaoCartaoEfetuarDadosRequest("123", "123", valorCompra, "Erro");

        when(cartaoRepository.findByNumeroCartao("123")).thenReturn(Optional.of(cartao));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        when(faturaRepository.findByCartaoAndStatus(cartao, StatusFatura.ABERTA)).thenReturn(Optional.of(fatura));

        assertThrows(RegraDeNegocioException.class, () -> service.realizarTransacaoCredito(request));

        verify(faturaRepository, never()).save(any());
        verify(transacaoRepository, never()).save(any());
    }

    @Test
    void erroCartaoTipoInvalido() {
        Cartao cartao = new Cartao();
        cartao.setNumeroCartao("123");
        cartao.setSenha("enc");
        cartao.setStatus(StatusCartao.CARTAO_ATIVO);
        cartao.setTipoCartao(TipoCartao.DEBITO);

        TransacaoCartaoEfetuarDadosRequest request = new TransacaoCartaoEfetuarDadosRequest("123", "123", BigDecimal.TEN, "Erro");

        when(cartaoRepository.findByNumeroCartao("123")).thenReturn(Optional.of(cartao));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);

        assertThrows(RegraDeNegocioException.class, () -> service.realizarTransacaoCredito(request));
    }
}