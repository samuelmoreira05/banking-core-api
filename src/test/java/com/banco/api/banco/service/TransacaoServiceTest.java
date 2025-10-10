package com.banco.api.banco.service;

import com.banco.api.banco.controller.transacao.request.TransacaoEfetuarDadosRequest;
import com.banco.api.banco.controller.transacao.response.TransacaoMostrarDadosResponse;
import com.banco.api.banco.enums.TipoTransacao;
import com.banco.api.banco.model.entity.Conta;
import com.banco.api.banco.model.entity.Transacao;
import com.banco.api.banco.repository.ContaRepository;
import com.banco.api.banco.repository.TransacaoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.query.sqm.EntityTypeException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransacaoServiceTest {

    @Mock private TransacaoRepository transacaoRepository;
    @Mock private ContaRepository contaRepository;
    @InjectMocks private TransacaoService transacaoService;
    @Captor private ArgumentCaptor<Transacao>  transacaoCaptor;

    @Test
    void transacaoEfetuadaDepositoComSucesso() {
        Long idConta = 1L;

        Conta conta = new Conta();
        conta.setId(idConta);
        conta.setSaldo(BigDecimal.valueOf(1000));

        TransacaoEfetuarDadosRequest transacao = new TransacaoEfetuarDadosRequest(
                idConta,
                TipoTransacao.DEPOSITO,
                BigDecimal.valueOf(100)
        );

        when(contaRepository.findById(idConta)).thenReturn(Optional.of(conta));
        when(transacaoRepository.save(any(Transacao.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        TransacaoMostrarDadosResponse response = transacaoService.efetuarTransacao(transacao);

        assertNotNull(response);
        assertEquals(BigDecimal.valueOf(100), response.valor());
        assertEquals(TipoTransacao.DEPOSITO, response.tipo());

        verify(transacaoRepository).save(transacaoCaptor.capture());
        Transacao transacaoSalva = transacaoCaptor.getValue();

        assertEquals(BigDecimal.valueOf(1000), transacaoSalva.getSaldoAnterior());
        assertEquals(BigDecimal.valueOf(100), transacaoSalva.getValor());
        assertEquals(BigDecimal.valueOf(1100), conta.getSaldo());
    }

    @Test
    void transacaoEfetuadaSaqueComSucesso() {
        var idConta = 1L;

        Conta conta = new Conta();
        conta.setId(idConta);
        conta.setSaldo(BigDecimal.valueOf(1000));

        TransacaoEfetuarDadosRequest transacao = new TransacaoEfetuarDadosRequest(
                idConta,
                TipoTransacao.SAQUE,
                BigDecimal.valueOf(100)
        );

        when(contaRepository.findById(idConta)).thenReturn(Optional.of(conta));
        when(transacaoRepository.save(any(Transacao.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        TransacaoMostrarDadosResponse response = transacaoService.efetuarTransacao(transacao);

        assertNotNull(response);
        assertEquals(BigDecimal.valueOf(100), response.valor());
        assertEquals(TipoTransacao.SAQUE, response.tipo());

        verify(transacaoRepository).save(transacaoCaptor.capture());
        Transacao transacaoSalva = transacaoCaptor.getValue();

        assertEquals(BigDecimal.valueOf(1000), transacaoSalva.getSaldoAnterior());
        assertEquals(BigDecimal.valueOf(100), transacaoSalva.getValor());
        assertEquals(BigDecimal.valueOf(900), conta.getSaldo());
    }

    @Test
    void transacaoNaoEfetuadaSemIdDaConta() {
        var idConta = 1L;

        Conta conta = new Conta();
        conta.setId(idConta);
        conta.setSaldo(new BigDecimal(1000));

        TransacaoEfetuarDadosRequest dados = new TransacaoEfetuarDadosRequest(
                idConta,
                TipoTransacao.SAQUE,
                 BigDecimal.valueOf(200)
        );

        when(contaRepository.findById(idConta)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            transacaoService.efetuarTransacao(dados);
        });

        verify(transacaoRepository, never()).save(any(Transacao.class));
    }
}
