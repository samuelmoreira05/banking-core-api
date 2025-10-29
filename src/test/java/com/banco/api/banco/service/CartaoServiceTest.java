package com.banco.api.banco.service;

import com.banco.api.banco.controller.cartao.request.CartaoCreditoCriarDadosRequest;
import com.banco.api.banco.controller.cartao.request.CartaoDebitoCriarDadosRequest;
import com.banco.api.banco.controller.cartao.response.CartaoCreditoMostrarDadosResponse;
import com.banco.api.banco.enums.StatusCartao;
import com.banco.api.banco.enums.StatusCliente;
import com.banco.api.banco.enums.TipoCartao;
import com.banco.api.banco.infra.exception.RegraDeNegocioException;
import com.banco.api.banco.mapper.CartaoMapper;
import com.banco.api.banco.model.entity.Cartao;
import com.banco.api.banco.model.entity.Cliente;
import com.banco.api.banco.model.entity.Conta;
import com.banco.api.banco.repository.CartaoRepository;
import com.banco.api.banco.repository.ContaRepository;
import com.banco.api.banco.service.validadores.cartao.ValidadorSolicitacaoCredito;
import com.banco.api.banco.util.GeradorDeCartaoUtil;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartaoServiceTest {

    @Mock private ContaRepository contaRepository;
    @Mock private CartaoMapper cartaoMapper;
    @Mock private GeradorDeCartaoUtil geradorDeCartaoUtil;
    @Mock private List<ValidadorSolicitacaoCredito> validadores;
    @Mock private CartaoRepository cartaoRepository;
    @InjectMocks private CartaoService cartaoService;

    @Captor private ArgumentCaptor<Cartao> captor;

    @Test
    void solicitacaoCartaoDebitoSucesso() {
        var idConta = 1L;
        var idCliente = 2L;

        Cliente cliente = new Cliente();
        cliente.setStatus(StatusCliente.ATIVO);
        cliente.setId(idCliente);

        Conta conta = new Conta();
        conta.setId(idConta);
        conta.setCliente(cliente);

        CartaoDebitoCriarDadosRequest dadosSolicitacao = new CartaoDebitoCriarDadosRequest(
                conta.getId()
        );

        when(contaRepository.findById(idConta)).thenReturn(Optional.of(conta));
        when(cartaoMapper.toEntity(any(CartaoDebitoCriarDadosRequest.class), any(Conta.class)))
                .thenReturn(Cartao.builder()
                .conta(conta)
                .tipoCartao(TipoCartao.DEBITO)
                .build()
        );
        when(geradorDeCartaoUtil.geraNumeroCartao()).thenReturn("1111222233334444");
        when(geradorDeCartaoUtil.geraCvv()).thenReturn("123");
        when(cartaoRepository.save(any(Cartao.class))).thenAnswer(inv -> inv.getArgument(0));

        var response = cartaoService.solicitaCartaoDebito(dadosSolicitacao);

        assertNotNull(response);
        assertEquals("1111222233334444", response.numeroCartao());

        verify(cartaoRepository).save(captor.capture());
        Cartao cartaoSalvo = captor.getValue();

        assertNotNull(cartaoSalvo);
        assertEquals("1111222233334444", cartaoSalvo.getNumeroCartao());
        assertEquals("123", cartaoSalvo.getCvv());
        assertEquals(StatusCartao.CARTAO_ATIVO, cartaoSalvo.getStatus());
        assertNotNull(cartaoSalvo.getDataVencimento());
        assertEquals(conta, cartaoSalvo.getConta());
    }

    @Test
    void solicitacaoCartaoDebitoFalhaQuandoContaNaoEncontrada() {
        var idConta = 1L;
        CartaoDebitoCriarDadosRequest dadosSolicitacao = new CartaoDebitoCriarDadosRequest(idConta);

        when(contaRepository.findById(idConta)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            cartaoService.solicitaCartaoDebito(dadosSolicitacao);
        });

        verify(cartaoRepository, never()).save(any(Cartao.class));
    }

    @Test
    void solicitaCartaoCreditoQuandoClienteElegivelEntaoCriaCartao() {
        var idConta = 1L;
        var idCliente = 2L;
        var limiteEsperado = new BigDecimal("500.000");
        var numeroCartaoEsperado = "4444555566667777";
        var cvvEsperado = "321";
        var dataVencimentoEsperada = LocalDate.now().plusYears(5);

        Cliente cliente = new Cliente();
        cliente.setId(idCliente);
        cliente.setStatus(StatusCliente.ATIVO);
        cliente.setDataNascimento(LocalDate.now().minusYears(25));

        Conta conta = new Conta();
        conta.setId(idConta);
        conta.setCliente(cliente);
        conta.setSaldo(new BigDecimal("1000.00"));

        CartaoCreditoCriarDadosRequest dadosSolicitacao = new CartaoCreditoCriarDadosRequest(idConta);
        Cartao cartaoBase = Cartao.builder().conta(conta).tipoCartao(TipoCartao.CREDITO).build();
        CartaoCreditoMostrarDadosResponse mockResponse = new CartaoCreditoMostrarDadosResponse(
                cliente.getNome(), conta.getAgencia(), conta.getNumeroConta(), numeroCartaoEsperado, dataVencimentoEsperada.format(DateTimeFormatter.ofPattern("MM/yy")), 10, limiteEsperado
        );

        when(contaRepository.findById(idConta)).thenReturn(Optional.of(conta));

        when(cartaoMapper.toEntityCredito(dadosSolicitacao, conta)).thenReturn(cartaoBase);
        when(geradorDeCartaoUtil.geraNumeroCartao()).thenReturn(numeroCartaoEsperado);
        when(geradorDeCartaoUtil.geraCvv()).thenReturn(cvvEsperado);
        when(cartaoRepository.save(any(Cartao.class))).thenAnswer(inv -> inv.getArgument(0));
        when(cartaoMapper.toCreditoResponse(any(Cartao.class))).thenReturn(mockResponse);

        CartaoCreditoMostrarDadosResponse response = cartaoService.solicitaCartaoCredito(dadosSolicitacao);

        assertNotNull(response);
        assertEquals(mockResponse, response);

        verify(cartaoRepository).save(captor.capture());
        Cartao cartaoSalvo = captor.getValue();

        assertNotNull(cartaoSalvo);
        assertEquals(numeroCartaoEsperado, cartaoSalvo.getNumeroCartao());
        assertEquals(cvvEsperado, cartaoSalvo.getCvv());
        assertEquals(limiteEsperado, cartaoSalvo.getLimiteCredito());
        assertEquals(10, cartaoSalvo.getDiaVencimentoFatura());
        assertEquals(dataVencimentoEsperada, cartaoSalvo.getDataVencimento());
        assertEquals(StatusCartao.CARTAO_ATIVO, cartaoSalvo.getStatus());
        assertEquals(TipoCartao.CREDITO, cartaoSalvo.getTipoCartao());
        assertEquals(conta, cartaoSalvo.getConta());
    }

    @Test
    void solicitaCartaoCreditoQuandoContaNaoExisteEntaoLancaRegraDeNegocioException() {
        var idContaInexistente = 99L;
        CartaoCreditoCriarDadosRequest dadosSolicitacao = new CartaoCreditoCriarDadosRequest(idContaInexistente);
        when(contaRepository.findById(idContaInexistente)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            cartaoService.solicitaCartaoCredito(dadosSolicitacao);
        });

        verify(cartaoRepository, never()).save(any(Cartao.class));
        verify(validadores, never()).forEach(any());
    }
}
