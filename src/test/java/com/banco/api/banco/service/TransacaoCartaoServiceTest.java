package com.banco.api.banco.service;

import com.banco.api.banco.controller.transacaoCartao.request.TransacaoCartaoEfetuarDadosRequest;
import com.banco.api.banco.controller.transacaoCartao.response.TransacaoCartaoMostrarDadosResponse;
import com.banco.api.banco.enums.TipoCartao;
import com.banco.api.banco.mapper.TransacaoMapper;
import com.banco.api.banco.model.entity.Cartao;
import com.banco.api.banco.model.entity.Conta;
import com.banco.api.banco.model.entity.Fatura;
import com.banco.api.banco.model.entity.Transacao;
import com.banco.api.banco.service.validadores.cartaoValidaDados.ValidadorDadosCartao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransacaoCartaoServiceTest {

    private TransacaoCartaoService transacaoCartaoService;

    @Mock
    private CartaoService cartaoService;

    @Mock
    private FaturaService faturaService;

    @Mock
    private TransacaoMapper transacaoMapper;

    @Mock
    private TransacaoService transacaoService;

    @Mock
    private ValidadorDadosCartao validador;

    @BeforeEach
    void setUp() {
        transacaoCartaoService = new TransacaoCartaoService(
                cartaoService,
                faturaService,
                transacaoMapper,
                List.of(validador),
                transacaoService
        );
    }

    @Test
    void deveRealizarTransacaoDebitoComSucesso() {
        String numeroCartao = "123456789";
        String senha = "123";
        BigDecimal valor = BigDecimal.TEN;
        String descricao = "Debito Teste";

        TransacaoCartaoEfetuarDadosRequest dados = new TransacaoCartaoEfetuarDadosRequest(
                numeroCartao, senha, valor, descricao
        );

        Conta conta = new Conta();
        conta.setId(1L);
        conta.setSaldo(BigDecimal.valueOf(90));

        Cartao cartao = new Cartao();
        cartao.setConta(conta);

        Transacao transacao = new Transacao();
        transacao.setId(1L);

        TransacaoCartaoMostrarDadosResponse responseEsperado = new TransacaoCartaoMostrarDadosResponse(
                1L,
                valor,
                descricao,
                LocalDateTime.now(),
                BigDecimal.valueOf(90)
        );

        when(cartaoService.buscaNumeroCartao(numeroCartao)).thenReturn(cartao);
        when(transacaoMapper.toEntityDebito(eq(conta), eq(valor), any(BigDecimal.class), eq(descricao))).thenReturn(transacao);
        when(transacaoService.salvar(transacao)).thenReturn(transacao);
        when(transacaoMapper.toCartaoResponse(transacao)).thenReturn(responseEsperado);

        var resultado = transacaoCartaoService.realizarTransacaoDebito(dados);

        assertNotNull(resultado);
        assertEquals(BigDecimal.valueOf(90), resultado.saldoAtualizado());

        verify(validador).validarDados(cartao, senha, TipoCartao.DEBITO);
        verify(transacaoService).salvar(transacao);
    }

    @Test
    void deveRealizarTransacaoCreditoComSucesso() {
        String numeroCartao = "987654321";
        String senha = "321";
        BigDecimal valor = BigDecimal.valueOf(50);
        String descricao = "Credito Teste";

        TransacaoCartaoEfetuarDadosRequest dados = new TransacaoCartaoEfetuarDadosRequest(
                numeroCartao, senha, valor, descricao
        );

        Cartao cartao = new Cartao();
        cartao.setId(2L);

        Fatura fatura = new Fatura();
        fatura.setId(10L);

        Transacao transacao = new Transacao();
        transacao.setId(5L);

        TransacaoCartaoMostrarDadosResponse responseEsperado = new TransacaoCartaoMostrarDadosResponse(
                5L,
                valor,
                descricao,
                LocalDateTime.now(),
                null                    
        );

        when(cartaoService.buscaNumeroCartao(numeroCartao)).thenReturn(cartao);
        when(faturaService.processarCompraCredito(cartao, valor)).thenReturn(fatura);
        when(transacaoMapper.toEntityCredito(fatura, valor, descricao)).thenReturn(transacao);
        when(transacaoService.salvar(transacao)).thenReturn(transacao);
        when(transacaoMapper.toCartaoResponse(transacao)).thenReturn(responseEsperado);

        var resultado = transacaoCartaoService.realizarTransacaoCredito(dados);

        assertNotNull(resultado);
        assertEquals(5L, resultado.idTransacao());

        verify(validador).validarDados(cartao, senha, TipoCartao.CREDITO);
        verify(faturaService).processarCompraCredito(cartao, valor);
        verify(transacaoService).salvar(transacao);
    }
}