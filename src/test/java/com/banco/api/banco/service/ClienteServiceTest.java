package com.banco.api.banco.service;

import com.banco.api.banco.controller.cliente.request.ClienteAtualizarDadosRequest;
import com.banco.api.banco.controller.cliente.request.ClienteCadastroDadosRequest;
import com.banco.api.banco.controller.cliente.response.ClienteListagemDadosResponse;
import com.banco.api.banco.controller.cliente.response.ClienteMostrarDadosResponse;
import com.banco.api.banco.enums.StatusCliente;
import com.banco.api.banco.model.entity.Cliente;
import com.banco.api.banco.repository.ClienteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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
    void cricacaoDeCadastroDeClienteSucesso() {
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

    @Test
    void atualizarClienteSucesso() {
        var id = 1L;

        ClienteAtualizarDadosRequest dadosatualizados = new ClienteAtualizarDadosRequest(
                "Matheus Almeida",
                "rua martins",
                "mateus@email.com",
                "19997534011"
        );

        Cliente clienteAntes = new Cliente();
        clienteAntes.setId(1L);
        clienteAntes.setNome("Samuel Garcia");
        clienteAntes.setEndereco("rua itapeserica");
        clienteAntes.setEmail("samuel.garcia@email.com");
        clienteAntes.setTelefone("19995542038");

        when(clienteRepository.findById(id)).thenReturn(Optional.of(clienteAntes));

        ClienteMostrarDadosResponse response = clienteService.atualizarCliente(id, dadosatualizados);

        assertNotNull(response);
        assertEquals("Matheus Almeida", response.nome());
        assertEquals("rua martins", response.endereco());
        assertEquals("mateus@email.com", response.email());
        assertEquals("19997534011", response.telefone());

        assertEquals("Matheus Almeida", clienteAntes.getNome());
        assertEquals("rua martins", clienteAntes.getEndereco());
        assertEquals("mateus@email.com", clienteAntes.getEmail());
        assertEquals("19997534011", clienteAntes.getTelefone());
    }

    @Test
    void listarClientesSucesso() {
        Pageable pageable = PageRequest.of(0, 10);

        Cliente cliente1 = new Cliente();
        cliente1.setId(1L);
        cliente1.setNome("Samuel Garcia");

        Cliente cliente2 = new Cliente();
        cliente2.setId(2L);
        cliente2.setNome("Rafael Moreira");

        Page<Cliente> paginaCLiente = new PageImpl<>(List.of(cliente1, cliente2), pageable, 2);

        when(clienteRepository.findAll(pageable)).thenReturn(paginaCLiente);

        Page<ClienteListagemDadosResponse> response = clienteService.listaCliente(pageable);

        assertNotNull(response);
        assertEquals(2, response.getTotalElements());
        assertEquals(2, response.getContent().size());
        assertEquals("Samuel Garcia", response.getContent().get(0).nome());
        assertEquals("Rafael Moreira", response.getContent().get(1).nome());
    }

    @Test
    void bloquearClientePorIdSucesso() {
        var id = 1L;

        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setStatus(StatusCliente.ATIVO);

        when(clienteRepository.findById(id)).thenReturn(Optional.of(cliente));

        clienteService.bloquear(id);

        assertEquals(StatusCliente.BLOQUEADO, cliente.getStatus());
    }

    @Test
    void inadimplenteClientePorSucesso() {
        var id = 1L;

        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setStatus(StatusCliente.ATIVO);

        when(clienteRepository.findById(id)).thenReturn(Optional.of(cliente));

        clienteService.inadimplencia(id);

        assertEquals(StatusCliente.INADIMPLENTE, cliente.getStatus());
    }

}