package com.banco.api.banco.service;

import com.banco.api.banco.controller.conta.request.ContaCadastroDadosRequest;
import com.banco.api.banco.controller.conta.response.ContaListagemDadosResponse;
import com.banco.api.banco.controller.conta.response.ContaMostrarDadosResponse;
import com.banco.api.banco.enums.StatusConta;
import com.banco.api.banco.enums.TipoConta;
import com.banco.api.banco.model.entity.Cliente;
import com.banco.api.banco.model.entity.Conta;
import com.banco.api.banco.repository.ClienteRepository;
import com.banco.api.banco.repository.ContaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContaServiceTest {

    @Mock private ContaRepository contaRepository;
    @Mock private ClienteRepository clienteRepository;
    @InjectMocks private ContaService contaService;
    @Captor private ArgumentCaptor<Conta> contaArgumentCaptor;

    @Test
    void criaContaComBaseNoIdCliente() {
        var idCliente = 1L;
        ContaCadastroDadosRequest dados = new ContaCadastroDadosRequest(idCliente, TipoConta.CONTA_CORRENTE);

        Cliente cliente = new Cliente();
        cliente.setId(idCliente);
        cliente.setNome("Samuel");

        when(clienteRepository.findById(idCliente)).thenReturn(Optional.of(cliente));

        ContaMostrarDadosResponse response = contaService.criarConta(dados);

        assertNotNull(response);
        assertEquals(TipoConta.CONTA_CORRENTE, response.tipo());
        assertEquals("Samuel", response.cliente().getNome());

        verify(contaRepository).save(contaArgumentCaptor.capture());
        Conta conta = contaArgumentCaptor.getValue();

        assertEquals(cliente, conta.getCliente());
        assertEquals(TipoConta.CONTA_CORRENTE, conta.getTipoConta());
    }

    @Test
    void criacaoContaClienteNaoEncontrado() {
        var idCliente = 1L;

        ContaCadastroDadosRequest dados = new ContaCadastroDadosRequest(idCliente, TipoConta.CONTA_CORRENTE);

        Cliente cliente = new Cliente();
        cliente.setId(idCliente);
        cliente.setNome("Samuel");

        when(clienteRepository.findById(idCliente)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            contaService.criarConta(dados);
        });

        verify(contaRepository, never()).save(any(Conta.class));
    }

    @Test
    void  listaContaSucesso() {
        Pageable pageable = PageRequest.of(0, 2);

        Cliente cliente1 = new Cliente();
        cliente1.setId(11L);
        cliente1.setNome("Samuel");

        Conta conta1 = new Conta();
        conta1.setId(1L);
        conta1.setCliente(cliente1);

        Cliente cliente2 = new Cliente();
        cliente2.setId(12L);
        cliente2.setNome("Rafael");

        Conta conta2 = new Conta();
        conta2.setId(2L);
        conta2.setCliente(cliente2);

        Page<Conta> contaPage = new PageImpl<>(List.of(conta1, conta2), pageable, 2);

        when(contaRepository.findAll(pageable)).thenReturn(contaPage);

        Page<ContaListagemDadosResponse> response = contaService.listarConta(pageable);

        assertNotNull(response);
        assertEquals(2, response.getTotalElements());
        assertEquals(2, response.getSize());
        assertEquals("Samuel", response.getContent().get(0).dado().nome());
        assertEquals("Rafael", response.getContent().get(1).dado().nome());
    }

    @Test
    void retornaPaginaVazia() {
        Pageable pageable = PageRequest.of(0, 10);

        when(contaRepository.findAll(pageable)).thenReturn(Page.empty());

        Page<ContaListagemDadosResponse> contaPage = contaService.listarConta(pageable);

        assertNotNull(contaPage);
        assertTrue(contaPage.isEmpty());
        assertEquals(0, contaPage.getTotalElements());
    }

    @Test
    void encerraContaClientePorId() {
        var idCliente = 2L;
        var idConta = 1L;

        Cliente cliente = new Cliente();
        cliente.setId(idCliente);

        Conta conta = new Conta();
        conta.setId(idConta);
        conta.setStatus(StatusConta.ATIVO);

        when(contaRepository.findById(idConta)).thenReturn(Optional.of(conta));

        contaService.encerraConta(idConta);

        assertEquals(StatusConta.ENCERRADA, conta.getStatus());
    }

    @Test
    void lancaExcecaoQuandoIdNaoExistirEncerrar() {
        var idConta = 2L;

        when(contaRepository.findById(idConta)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
           contaService.encerraConta(idConta);
        });
    }

    @Test
    void suspendeContaClientePorId() {
        var idCliente = 2L;
        var idConta = 1L;

        Cliente cliente = new Cliente();
        cliente.setId(idCliente);

        Conta conta = new Conta();
        conta.setId(idConta);
        conta.setStatus(StatusConta.ATIVO);

        when(contaRepository.findById(idConta)).thenReturn(Optional.of(conta));

        contaService.suspendeConta(idConta);

        assertEquals(StatusConta.SUSPENSA, conta.getStatus());
    }

    @Test
    void lancaExcecaoQuandoIdNaoExistirSuspender() {
        var idConta = 2L;

        when(contaRepository.findById(idConta)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            contaService.suspendeConta(idConta);
        });
    }

    @Test
    void ativaContaClientePorId() {
        var idCliente = 2L;
        var idConta = 1L;

        Cliente cliente = new Cliente();
        cliente.setId(idCliente);

        Conta conta = new Conta();
        conta.setId(idConta);
        conta.setStatus(StatusConta.SUSPENSA);

        when(contaRepository.findById(idConta)).thenReturn(Optional.of(conta));

        contaService.ativaConta(idConta);

        assertEquals(StatusConta.ATIVO, conta.getStatus());
    }

    @Test
    void lancaExcecaoQuandoIdNaoExistirAtivar() {
        var idConta = 2L;

        when(contaRepository.findById(idConta)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            contaService.ativaConta(idConta);
        });
    }
}