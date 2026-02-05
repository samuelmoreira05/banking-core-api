package com.banco.api.banco.service;

import com.banco.api.banco.enums.StatusFatura;
import com.banco.api.banco.infra.exception.RegraDeNegocioException;
import com.banco.api.banco.mapper.FaturaMapper;
import com.banco.api.banco.model.entity.Cartao;
import com.banco.api.banco.model.entity.Fatura;
import com.banco.api.banco.repository.FaturaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FaturaServiceTest {

    @InjectMocks
    private FaturaService faturaService;

    @Mock
    private FaturaRepository faturaRepository;

    @Mock
    private FaturaMapper faturaMapper;

    @Test
    void deveProcessarCompraCreditoEmFaturaExistenteComSucesso() {
        Cartao cartao = new Cartao();
        cartao.setLimiteCredito(BigDecimal.valueOf(1000));

        Fatura faturaExistente = new Fatura();
        faturaExistente.setValorTotal(BigDecimal.valueOf(200));
        faturaExistente.setStatus(StatusFatura.ABERTA);

        BigDecimal valorCompra = BigDecimal.valueOf(100);

        when(faturaRepository.findByCartaoAndStatus(cartao, StatusFatura.ABERTA))
                .thenReturn(Optional.of(faturaExistente));

        when(faturaRepository.save(faturaExistente)).thenReturn(faturaExistente);

        Fatura resultado = faturaService.processarCompraCredito(cartao, valorCompra);

        assertNotNull(resultado);
        assertEquals(BigDecimal.valueOf(300), resultado.getValorTotal());
        verify(faturaRepository).save(faturaExistente);
    }

    @Test
    void deveCriarNovaFaturaEProcessarCompraSeNaoHouverFaturaAberta() {
        Cartao cartao = new Cartao();
        cartao.setLimiteCredito(BigDecimal.valueOf(1000));

        BigDecimal valorCompra = BigDecimal.valueOf(100);

        Fatura novaFatura = new Fatura();
        novaFatura.setValorTotal(BigDecimal.ZERO);
        novaFatura.setStatus(StatusFatura.ABERTA);

        when(faturaRepository.findByCartaoAndStatus(cartao, StatusFatura.ABERTA))
                .thenReturn(Optional.empty());

        when(faturaMapper.toEntityFaturaInicial(cartao)).thenReturn(novaFatura);
        when(faturaRepository.save(novaFatura)).thenReturn(novaFatura);

        Fatura resultado = faturaService.processarCompraCredito(cartao, valorCompra);

        assertNotNull(resultado);
        assertEquals(BigDecimal.valueOf(100), resultado.getValorTotal());

        verify(faturaMapper).toEntityFaturaInicial(cartao);
        verify(faturaRepository, times(2)).save(novaFatura);
    }

    @Test
    void deveLancarExceptionQuandoLimiteInsuficiente() {
        Cartao cartao = new Cartao();
        cartao.setLimiteCredito(BigDecimal.valueOf(500));

        Fatura faturaExistente = new Fatura();
        faturaExistente.setValorTotal(BigDecimal.valueOf(450));

        BigDecimal valorCompra = BigDecimal.valueOf(100);

        when(faturaRepository.findByCartaoAndStatus(cartao, StatusFatura.ABERTA))
                .thenReturn(Optional.of(faturaExistente));

        assertThrows(RegraDeNegocioException.class, () ->
                faturaService.processarCompraCredito(cartao, valorCompra)
        );

        verify(faturaRepository, never()).save(any());
    }

    @Test
    void deveRetornarFaturaAtualExistente() {
        Cartao cartao = new Cartao();
        Fatura fatura = new Fatura();

        when(faturaRepository.findByCartaoAndStatus(cartao, StatusFatura.ABERTA))
                .thenReturn(Optional.of(fatura));

        Fatura resultado = faturaService.getFaturaAtual(cartao);

        assertNotNull(resultado);
        assertEquals(fatura, resultado);
        verify(faturaRepository, never()).save(any());
    }

    @Test
    void deveCriarFaturaSeNaoExistirAoBuscarAtual() {
        Cartao cartao = new Cartao();
        Fatura novaFatura = new Fatura();

        when(faturaRepository.findByCartaoAndStatus(cartao, StatusFatura.ABERTA))
                .thenReturn(Optional.empty());
        when(faturaMapper.toEntityFaturaInicial(cartao)).thenReturn(novaFatura);
        when(faturaRepository.save(novaFatura)).thenReturn(novaFatura);

        Fatura resultado = faturaService.getFaturaAtual(cartao);

        assertNotNull(resultado);
        assertEquals(novaFatura, resultado);
        verify(faturaRepository).save(novaFatura);
    }
}