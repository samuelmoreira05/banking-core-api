package com.banco.api.banco.service;

import com.banco.api.banco.controller.transacao.request.TransacaoEfetuarDadosRequest;
import com.banco.api.banco.controller.transacao.response.TransacaoMostrarDadosResponse;
import com.banco.api.banco.enums.StatusConta;
import com.banco.api.banco.enums.TipoTransacao;
import com.banco.api.banco.infra.exception.RegraDeNegocioException;
import com.banco.api.banco.mapper.TransacaoMapper;
import com.banco.api.banco.model.entity.Conta;
import com.banco.api.banco.model.entity.Transacao;
import com.banco.api.banco.repository.TransacaoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransacaoServiceTest {

    @InjectMocks
    private TransacaoService transacaoService;

    @Mock
    private ContaService contaService;

    @Mock
    private TransacaoRepository repository;

    @Mock
    private TransacaoMapper transacaoMapper;

    @Test
    void deveEfetuarTransacaoComSucesso() {
        Long contaId = 1L;
        BigDecimal valor = BigDecimal.valueOf(100);

        TransacaoEfetuarDadosRequest dados = new TransacaoEfetuarDadosRequest(contaId, TipoTransacao.DEPOSITO, valor);

        Conta conta = new Conta();
        conta.setId(contaId);
        conta.setSaldo(BigDecimal.ZERO);
        conta.setStatus(StatusConta.ATIVO);

        Transacao transacao = new Transacao();
        transacao.setId(1L);
        transacao.setValor(valor);
        transacao.setTipo(TipoTransacao.DEPOSITO);

        TransacaoMostrarDadosResponse responseEsperado = new TransacaoMostrarDadosResponse(
                1L,
                TipoTransacao.DEPOSITO,
                LocalDateTime.now(),
                valor,
                BigDecimal.ZERO,
                BigDecimal.valueOf(100),
                null
        );

        when(contaService.buscarContaPorId(contaId)).thenReturn(conta);
        when(transacaoMapper.toEntity(eq(conta), eq(dados), any(BigDecimal.class))).thenReturn(transacao);
        when(repository.save(transacao)).thenReturn(transacao);
        when(transacaoMapper.toResponse(transacao)).thenReturn(responseEsperado);

        var resultado = transacaoService.efetuarTransacao(dados);

        assertNotNull(resultado);
        verify(repository).save(transacao);
        assertEquals(TipoTransacao.DEPOSITO, resultado.tipo());
        assertEquals(valor, resultado.valor());
    }

    @Test
    void deveLancarExceptionAoEfetuarTransacaoEmContaInativa() {
        Long contaId = 1L;

        TransacaoEfetuarDadosRequest dados = new TransacaoEfetuarDadosRequest(contaId, TipoTransacao.SAQUE, BigDecimal.TEN);

        Conta conta = new Conta();
        conta.setId(contaId);
        conta.setStatus(StatusConta.SUSPENSA);

        when(contaService.buscarContaPorId(contaId)).thenReturn(conta);

        assertThrows(RegraDeNegocioException.class, () -> transacaoService.efetuarTransacao(dados));

        verify(repository, never()).save(any());
    }

    @Test
    void deveSalvarTransacaoComSucesso() {
        Transacao transacao = new Transacao();
        transacao.setId(1L);
        transacao.setValor(BigDecimal.TEN);

        when(repository.save(transacao)).thenReturn(transacao);

        Transacao resultado = transacaoService.salvar(transacao);

        assertNotNull(resultado);
        assertEquals(transacao.getId(), resultado.getId());
        verify(repository).save(transacao);
    }
}