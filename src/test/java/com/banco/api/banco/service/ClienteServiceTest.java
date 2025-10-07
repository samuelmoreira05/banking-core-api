package com.banco.api.banco.service;

import com.banco.api.banco.controller.cliente.request.ClienteCadastroDadosRequest;
import com.banco.api.banco.controller.cliente.response.ClienteMostrarDadosResponse;
import com.banco.api.banco.enums.StatusCliente;
import com.banco.api.banco.model.entity.Cliente;
import com.banco.api.banco.repository.ClienteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ClienteService clienteService;

    @Captor
    private ArgumentCaptor<Cliente> clienteArgumentCaptor;

    @Test
    void cricacaoDeCadastroDeCliente() {
        ClienteCadastroDadosRequest dados = new ClienteCadastroDadosRequest(
                "Samuel Garcia",
                "samuel.garcia@email.com",
                "49743844918",
                "19995542038",
                "rua itapeserica",
                LocalDate.of(2005, 4, 4),
                "samuel.g",
                "samu123456"
        );

        when(clienteRepository.existsByCpf(dados.cpf())).thenReturn(false);
        when(passwordEncoder.encode(dados.senha())).thenReturn("senha_cripto");

        ClienteMostrarDadosResponse response = clienteService.cadastraCliente(dados);

        assertNotNull(response);
        assertEquals("Samuel Garcia", response.nome());

        verify(clienteRepository).save(clienteArgumentCaptor.capture());

        Cliente clienteSalvo = clienteArgumentCaptor.getValue();

        assertNotNull(clienteSalvo.getUsuario());
        assertEquals("Samuel Garcia", clienteSalvo.getNome());
        assertEquals("49743844918", clienteSalvo.getCpf());
        assertEquals(StatusCliente.ATIVO, clienteSalvo.getStatus());
    }

}