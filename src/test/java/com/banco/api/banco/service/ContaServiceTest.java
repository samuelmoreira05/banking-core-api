package com.banco.api.banco.service;

import com.banco.api.banco.controller.cliente.request.ClienteCadastroDadosRequest;
import com.banco.api.banco.controller.conta.request.ContaCadastroDadosRequest;
import com.banco.api.banco.controller.conta.response.ContaMostrarDadosResponse;
import com.banco.api.banco.enums.TipoConta;
import com.banco.api.banco.model.entity.Cliente;
import com.banco.api.banco.model.entity.Conta;
import com.banco.api.banco.repository.ClienteRepository;
import com.banco.api.banco.repository.ContaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
}