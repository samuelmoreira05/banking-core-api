package com.banco.api.banco.service;

import com.banco.api.banco.controller.cartao.request.CartaoCreditoCriarDadosRequest;
import com.banco.api.banco.controller.cartao.request.CartaoDebitoCriarDadosRequest;
import com.banco.api.banco.controller.cartao.response.CartaoCreditoMostrarDadosResponse;
import com.banco.api.banco.controller.cartao.response.CartaoDebitoMostrarDadosResponse;
import com.banco.api.banco.enums.StatusCartao;
import com.banco.api.banco.enums.TipoCartao;
import com.banco.api.banco.mapper.CartaoMapper;
import com.banco.api.banco.model.entity.Cartao;
import com.banco.api.banco.model.entity.Cliente;
import com.banco.api.banco.model.entity.Conta;
import com.banco.api.banco.repository.CartaoRepository;
import com.banco.api.banco.service.calculadora.CalculadoraLimiteCartao;
import com.banco.api.banco.service.factory.CartaoFactory;
import com.banco.api.banco.service.validadores.cartaoCredito.ValidadorEmissaoCartao;
import com.banco.api.banco.util.GeradorDeCartaoUtil;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartaoServiceTest {

    @Mock
    private CartaoRepository cartaoRepository;
    @Mock
    private ContaService contaService;
    @Mock
    private CartaoMapper cartaoMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private GeradorDeCartaoUtil geradorDeCartaoUtil;
    @Mock
    private CalculadoraLimiteCartao calculadoraLimiteCartao;
    @Mock
    private ValidadorEmissaoCartao validador;
    @Mock
    private CartaoFactory cartaoFactory;

    private CartaoService cartaoService;

    @BeforeEach
    void setUp() {
        cartaoService = new CartaoService(
                cartaoRepository,
                cartaoMapper,
                passwordEncoder,
                contaService,
                geradorDeCartaoUtil,
                calculadoraLimiteCartao,
                List.of(validador),
                cartaoFactory
        );
    }

    @Test
    void deveSolicitarCartaoDebitoComSucesso() {
        Long idConta = 1L;
        CartaoDebitoCriarDadosRequest request = new CartaoDebitoCriarDadosRequest(idConta, "123456");

        Conta conta = new Conta();
        conta.setId(idConta);
        conta.setAgencia("0001");
        conta.setNumeroConta("12345-6");

        Cliente cliente = new Cliente();
        cliente.setNome("Cliente Teste");
        conta.setCliente(cliente);

        Cartao cartaoRascunho = new Cartao();
        Cartao cartaoPronto = new Cartao();
        cartaoPronto.setStatus(StatusCartao.CARTAO_ATIVO);

        CartaoDebitoMostrarDadosResponse responseEsperado = new CartaoDebitoMostrarDadosResponse(
                "Cliente Teste",
                "0001",
                "12345-6",
                "1111222233334444",
                "2030-01-01"
        );

        when(contaService.buscarContaPorId(idConta)).thenReturn(conta);
        when(passwordEncoder.encode(request.senha())).thenReturn("senhaHash");
        when(cartaoMapper.toEntity(request, conta, "senhaHash")).thenReturn(cartaoRascunho);

        when(cartaoFactory.finalizarCriacaoCartao(cartaoRascunho)).thenReturn(cartaoPronto);

        when(cartaoMapper.toDebitoResponse(cartaoPronto)).thenReturn(responseEsperado);

        var resultado = cartaoService.solicitaCartaoDebito(request);

        assertNotNull(resultado);
        assertEquals("Cliente Teste", resultado.nomeTitular());

        verify(validador).validar(cliente, conta, TipoCartao.DEBITO);
        verify(cartaoFactory).finalizarCriacaoCartao(cartaoRascunho);
    }

    @Test
    void deveSolicitarCartaoCreditoComSucesso() {
        Long idConta = 1L;
        CartaoCreditoCriarDadosRequest request = new CartaoCreditoCriarDadosRequest(idConta, "123456");

        Conta conta = new Conta();
        conta.setId(idConta);
        conta.setAgencia("0001");
        conta.setNumeroConta("99999-9");
        Cliente cliente = new Cliente();
        cliente.setNome("Cliente Teste");
        conta.setCliente(cliente);

        Cartao cartaoRascunho = new Cartao();
        Cartao cartaoPronto = new Cartao();
        cartaoPronto.setLimiteCredito(BigDecimal.valueOf(2000));
        cartaoPronto.setStatus(StatusCartao.CARTAO_ATIVO);

        CartaoCreditoMostrarDadosResponse responseEsperado = new CartaoCreditoMostrarDadosResponse(
                "Cliente Teste",
                "0001",
                "99999-9",
                "1111222233334444",
                "2030-01-01",
                10,
                BigDecimal.valueOf(2000)
        );

        when(contaService.buscarContaPorId(idConta)).thenReturn(conta);
        when(passwordEncoder.encode(request.senha())).thenReturn("senhaHash");
        when(cartaoMapper.toEntityCredito(request, conta, "senhaHash")).thenReturn(cartaoRascunho);
        when(calculadoraLimiteCartao.limite(conta)).thenReturn(BigDecimal.valueOf(2000));

        when(cartaoFactory.finalizarCriacaoCartao(cartaoRascunho)).thenReturn(cartaoPronto);

        when(cartaoMapper.toCreditoResponse(cartaoPronto)).thenReturn(responseEsperado);

        var resultado = cartaoService.solicitaCartaoCredito(request);

        assertNotNull(resultado);
        assertEquals("Cliente Teste", resultado.nomeTitular());
        assertEquals(BigDecimal.valueOf(2000), resultado.limiteCredito());

        verify(validador).validar(cliente, conta, TipoCartao.CREDITO);
        verify(cartaoFactory).finalizarCriacaoCartao(cartaoRascunho);
    }

    @Test
    void deveBloquearCartaoComSucesso() {
        Long idCartao = 1L;
        Cartao cartao = spy(new Cartao());
        cartao.setId(idCartao);

        when(cartaoRepository.findById(idCartao)).thenReturn(Optional.of(cartao));

        cartaoService.bloqueiaCartao(idCartao);

        verify(cartao).bloqueiaCartao();
        verify(cartaoRepository).save(cartao);
    }

    @Test
    void deveAtivarCartaoComSucesso() {
        Long idCartao = 1L;
        Cartao cartao = spy(new Cartao());
        cartao.setId(idCartao);

        when(cartaoRepository.findById(idCartao)).thenReturn(Optional.of(cartao));

        cartaoService.ativarCartao(idCartao);

        verify(cartao).ativaCartao();
        verify(cartaoRepository).save(cartao);
    }

    @Test
    void deveBuscarNumeroCartaoComSucesso() {
        String numero = "123456789";
        Cartao cartao = new Cartao();
        when(cartaoRepository.findByNumeroCartao(numero)).thenReturn(Optional.of(cartao));

        Cartao resultado = cartaoService.buscaNumeroCartao(numero);

        assertNotNull(resultado);
        assertEquals(cartao, resultado);
    }

    @Test
    void deveLancarExceptionAoBuscarNumeroCartaoInexistente() {
        String numero = "0000";
        when(cartaoRepository.findByNumeroCartao(numero)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> cartaoService.buscaNumeroCartao(numero));
    }

    @Test
    void deveLancarExceptionAoNaoEncontrarCartaoPorId() {
        Long id = 99L;
        when(cartaoRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> cartaoService.bloqueiaCartao(id));
    }
}