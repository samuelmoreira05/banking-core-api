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
import com.banco.api.banco.service.validadores.cartaoCredito.ValidadorEmissaoCartao;
import com.banco.api.banco.util.GeradorDeCartaoUtil;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
                List.of(validador)
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

        Cartao cartao = new Cartao();
        cartao.setStatus(StatusCartao.CARTAO_ATIVO);

        CartaoDebitoMostrarDadosResponse responseEsperado = new CartaoDebitoMostrarDadosResponse(
                "Cliente Teste",
                "0001",
                "12345-6",
                "1111222233334444",
                "2030-01-01"
        );

        when(contaService.buscarContaPorId(idConta)).thenReturn(conta);
        when(passwordEncoder.encode(request.senha())).thenReturn("senhaHash");
        when(cartaoMapper.toEntity(request, conta, "senhaHash")).thenReturn(cartao);
        when(geradorDeCartaoUtil.geraNumeroCartao()).thenReturn("1111222233334444");
        when(cartaoRepository.existsByNumeroCartao(anyString())).thenReturn(false);
        when(geradorDeCartaoUtil.geraCvv()).thenReturn("123");
        when(cartaoRepository.save(any(Cartao.class))).thenReturn(cartao);
        when(cartaoMapper.toDebitoResponse(any(Cartao.class))).thenReturn(responseEsperado);

        var resultado = cartaoService.solicitaCartaoDebito(request);

        assertNotNull(resultado);

        assertEquals("Cliente Teste", resultado.nomeTitular());

        verify(validador).validar(cliente, conta, TipoCartao.DEBITO);
        verify(cartaoRepository).save(cartao);
        assertEquals(StatusCartao.CARTAO_ATIVO, cartao.getStatus());
        assertNotNull(cartao.getDataVencimento());
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

        Cartao cartao = new Cartao();
        cartao.setId(10L); // ID arbitrário
        cartao.setLimiteCredito(BigDecimal.valueOf(2000));
        cartao.setDiaVencimentoFatura(10);
        cartao.setStatus(StatusCartao.CARTAO_ATIVO);

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
        when(cartaoMapper.toEntityCredito(request, conta, "senhaHash")).thenReturn(cartao);
        when(calculadoraLimiteCartao.limite(conta)).thenReturn(BigDecimal.valueOf(2000));
        when(geradorDeCartaoUtil.geraNumeroCartao()).thenReturn("1111222233334444");
        when(cartaoRepository.existsByNumeroCartao(anyString())).thenReturn(false);
        when(geradorDeCartaoUtil.geraCvv()).thenReturn("999");

        when(cartaoRepository.save(any(Cartao.class))).thenReturn(cartao);

        when(cartaoMapper.toCreditoResponse(any(Cartao.class))).thenReturn(responseEsperado);

        var resultado = cartaoService.solicitaCartaoCredito(request);

        assertNotNull(resultado);
        assertEquals("Cliente Teste", resultado.nomeTitular());
        assertEquals(BigDecimal.valueOf(2000), resultado.limiteCredito());

        verify(validador).validar(cliente, conta, TipoCartao.CREDITO);
        verify(cartaoRepository).save(cartao);
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

    @Test
    void deveGerarNovoNumeroSeJaExistirNoBanco() {
        Long idConta = 1L;
        CartaoDebitoCriarDadosRequest request = new CartaoDebitoCriarDadosRequest(idConta, "123456");
        Conta conta = new Conta();
        conta.setCliente(new Cliente());
        Cartao cartao = new Cartao();

        when(contaService.buscarContaPorId(idConta)).thenReturn(conta);
        when(passwordEncoder.encode(any())).thenReturn("hash");
        when(cartaoMapper.toEntity(any(), any(), any())).thenReturn(cartao);

        when(geradorDeCartaoUtil.geraNumeroCartao())
                .thenReturn("1111")
                .thenReturn("2222");

        when(cartaoRepository.existsByNumeroCartao("1111")).thenReturn(true);
        when(cartaoRepository.existsByNumeroCartao("2222")).thenReturn(false);

        when(cartaoRepository.save(any())).thenReturn(cartao);

        cartaoService.solicitaCartaoDebito(request);

        ArgumentCaptor<Cartao> captor = ArgumentCaptor.forClass(Cartao.class);
        verify(cartaoRepository).save(captor.capture());

        assertEquals("2222", captor.getValue().getNumeroCartao());
        verify(cartaoRepository, times(2)).existsByNumeroCartao(anyString());
    }
}