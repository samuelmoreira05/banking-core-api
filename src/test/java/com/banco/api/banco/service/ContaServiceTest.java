package com.banco.api.banco.service;

import com.banco.api.banco.controller.conta.request.ContaCadastroDadosRequest;
import com.banco.api.banco.controller.conta.response.ContaListagemDadosResponse;
import com.banco.api.banco.controller.conta.response.ContaMostrarDadosResponse;
import com.banco.api.banco.enums.StatusConta;
import com.banco.api.banco.enums.TipoConta;
import com.banco.api.banco.infra.exception.RegraDeNegocioException;
import com.banco.api.banco.mapper.ContaMapper;
import com.banco.api.banco.model.entity.Cliente;
import com.banco.api.banco.model.entity.Conta;
import com.banco.api.banco.repository.ContaRepository;
import com.banco.api.banco.util.GeradorDeContaUtil;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContaServiceTest {

    @InjectMocks
    private ContaService contaService;

    @Mock
    private ContaRepository repository;

    @Mock
    private ClienteService clienteService;

    @Mock
    private ContaMapper contaMapper;

    @Mock
    private GeradorDeContaUtil geradorDeContaUtil;

    @Test
    void deveCriarContaComSucesso() {
        Long clienteId = 1L;
        ContaCadastroDadosRequest dados = new ContaCadastroDadosRequest(clienteId, TipoConta.CONTA_CORRENTE);

        Cliente cliente = new Cliente();
        cliente.setId(clienteId);
        cliente.setNome("Cliente Teste");

        Conta conta = new Conta();
        conta.setSaldo(BigDecimal.ZERO);
        conta.setStatus(StatusConta.ATIVO);
        conta.setAgencia("0001");
        conta.setNumeroConta("12345");

        ContaMostrarDadosResponse responseEsperado = new ContaMostrarDadosResponse(
                "12345",
                TipoConta.CONTA_CORRENTE,
                "0001",
                BigDecimal.ZERO,
                StatusConta.ATIVO,
                LocalDateTime.now(),
                1L,
                "Cliente Teste"
        );

        when(clienteService.buscarClientePorId(clienteId)).thenReturn(cliente);
        when(contaMapper.toEntity(dados, cliente)).thenReturn(conta);
        when(geradorDeContaUtil.gerarAgencia()).thenReturn("0001");
        when(geradorDeContaUtil.gerarNumeroConta()).thenReturn("12345");
        when(repository.existsByNumeroConta("12345")).thenReturn(false);
        when(repository.save(conta)).thenReturn(conta);
        when(contaMapper.toContaResponse(conta)).thenReturn(responseEsperado);

        var resultado = contaService.criarConta(dados);

        assertNotNull(resultado);
        verify(repository).save(conta);
        assertEquals(StatusConta.ATIVO, conta.getStatus());
        assertEquals("0001", conta.getAgencia());
        assertEquals("12345", conta.getNumeroConta());
    }

    @Test
    void deveGerarNovoNumeroContaSeJaExistirAoCriar() {
        Long clienteId = 1L;
        ContaCadastroDadosRequest dados = new ContaCadastroDadosRequest(clienteId,TipoConta.CONTA_CORRENTE);
        Cliente cliente = new Cliente();
        Conta conta = new Conta();

        when(clienteService.buscarClientePorId(clienteId)).thenReturn(cliente);
        when(contaMapper.toEntity(dados, cliente)).thenReturn(conta);
        when(geradorDeContaUtil.gerarAgencia()).thenReturn("0001");

        when(geradorDeContaUtil.gerarNumeroConta())
                .thenReturn("11111")
                .thenReturn("22222");

        when(repository.existsByNumeroConta("11111")).thenReturn(true);
        when(repository.existsByNumeroConta("22222")).thenReturn(false);

        when(repository.save(conta)).thenReturn(conta);

        contaService.criarConta(dados);

        verify(repository, times(2)).existsByNumeroConta(anyString());
        assertEquals("22222", conta.getNumeroConta());
    }

    @Test
    void deveListarContas() {
        Pageable pageable = Pageable.unpaged();

        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("Cliente Teste");

        Conta conta = new Conta();
        conta.setId(1L);
        conta.setCliente(cliente);

        Page<Conta> page = new PageImpl<>(List.of(conta));

        when(repository.findAll(pageable)).thenReturn(page);

        var resultado = contaService.listarConta(pageable);

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
    }

    @Test
    void deveEncerrarContaComSaldoZero() {
        Long id = 1L;
        Conta conta = spy(new Conta());
        conta.setId(id);
        conta.setSaldo(BigDecimal.ZERO);
        conta.setStatus(StatusConta.ATIVO);

        when(repository.findById(id)).thenReturn(Optional.of(conta));

        contaService.encerraConta(id);

        verify(conta).encerraConta();
    }

    @Test
    void deveLancarExceptionAoEncerrarContaComSaldoPositivo() {
        Long id = 1L;
        Conta conta = new Conta();
        conta.setId(id);
        conta.setSaldo(new BigDecimal("10.00"));

        when(repository.findById(id)).thenReturn(Optional.of(conta));

        assertThrows(RegraDeNegocioException.class, () -> contaService.encerraConta(id));
        assertEquals(StatusConta.ATIVO, conta.getStatus() == null ? StatusConta.ATIVO : conta.getStatus());
    }

    @Test
    void deveLancarExceptionAoEncerrarContaComSaldoNegativo() {
        Long id = 1L;
        Conta conta = new Conta();
        conta.setId(id);
        conta.setSaldo(new BigDecimal("-50.00"));

        when(repository.findById(id)).thenReturn(Optional.of(conta));

        assertThrows(RegraDeNegocioException.class, () -> contaService.encerraConta(id));
    }

    @Test
    void deveSuspenderConta() {
        Long id = 1L;
        Conta conta = spy(new Conta());
        conta.setId(id);
        conta.setStatus(StatusConta.ATIVO);

        when(repository.findById(id)).thenReturn(Optional.of(conta));

        contaService.suspendeConta(id);

        verify(conta).suspendeConta();
    }

    @Test
    void deveAtivarConta() {
        Long id = 1L;
        Conta conta = spy(new Conta());
        conta.setId(id);
        conta.setStatus(StatusConta.SUSPENSA);

        when(repository.findById(id)).thenReturn(Optional.of(conta));

        contaService.ativaConta(id);

        verify(conta).ativaConta();
    }

    @Test
    void deveLancarExceptionAoBuscarContaInexistente() {
        Long id = 99L;
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> contaService.buscarContaPorId(id));
    }
}