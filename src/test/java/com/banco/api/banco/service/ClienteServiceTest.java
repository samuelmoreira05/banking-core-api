package com.banco.api.banco.service;

import com.banco.api.banco.controller.cliente.request.ClienteAtualizarDadosRequest;
import com.banco.api.banco.controller.cliente.request.ClienteCadastroDadosRequest;
import com.banco.api.banco.controller.cliente.response.ClienteListagemDadosResponse;
import com.banco.api.banco.controller.cliente.response.ClienteMostrarDadosResponse;
import com.banco.api.banco.enums.StatusCliente;
import com.banco.api.banco.enums.UserRole;
import com.banco.api.banco.infra.exception.RegraDeNegocioException;
import com.banco.api.banco.mapper.ClienteMapper;
import com.banco.api.banco.model.entity.Cliente;
import com.banco.api.banco.model.entity.Usuario;
import com.banco.api.banco.repository.ClienteRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @InjectMocks
    private ClienteService clienteService;

    @Mock
    private ClienteRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ClienteMapper clienteMapper;

    @Test
    void deveCadastrarClienteComSucesso() {
        ClienteCadastroDadosRequest dados = new ClienteCadastroDadosRequest(
                "Nome", "email@teste.com", "12345678900", "99999999",
                "Rua A", LocalDate.of(1990, 1, 1), "login", "senha"
        );

        Usuario usuario = new Usuario();
        Cliente cliente = new Cliente();
        cliente.setUsuario(usuario);
        cliente.setDataNascimento(LocalDate.of(1990, 1, 1));

        ClienteMostrarDadosResponse responseEsperado = new ClienteMostrarDadosResponse(
                1L, "Nome", "Rua A", "email@teste.com", "99999999", StatusCliente.ATIVO
        );

        when(repository.existsByCpf(dados.cpf())).thenReturn(false);
        when(repository.existsByEmail(dados.email())).thenReturn(false);
        when(clienteMapper.toEntity(dados, null)).thenReturn(cliente);
        when(passwordEncoder.encode(dados.senha())).thenReturn("senhaHash");
        when(repository.save(cliente)).thenReturn(cliente);
        when(clienteMapper.toClienteResponse(cliente)).thenReturn(responseEsperado);

        var resultado = clienteService.cadastraCliente(dados);

        assertNotNull(resultado);
        assertEquals(StatusCliente.ATIVO, cliente.getStatus());
        assertEquals(UserRole.USER, cliente.getUsuario().getRole());
        assertEquals("senhaHash", cliente.getUsuario().getSenha());
        verify(repository).save(cliente);
    }

    @Test
    void deveLancarExceptionQuandoClienteMenorDeIdade() {
        ClienteCadastroDadosRequest dados = new ClienteCadastroDadosRequest(
                "Nome", "email@teste.com", "12345678900", "99999999",
                "Rua A", LocalDate.now().minusYears(17), "login", "senha"
        );

        Cliente cliente = new Cliente();
        cliente.setDataNascimento(LocalDate.now().minusYears(17));

        when(repository.existsByCpf(dados.cpf())).thenReturn(false);
        when(repository.existsByEmail(dados.email())).thenReturn(false);
        when(clienteMapper.toEntity(dados, null)).thenReturn(cliente);

        assertThrows(RegraDeNegocioException.class, () -> clienteService.cadastraCliente(dados));

        verify(passwordEncoder, never()).encode(any());
        verify(repository, never()).save(any());
    }

    @Test
    void deveLancarExceptionQuandoCpfJaExiste() {
        ClienteCadastroDadosRequest dados = new ClienteCadastroDadosRequest(
                "Nome", "email@teste.com", "12345678900", "99999999",
                "Rua A", LocalDate.of(1990, 1, 1), "login", "senha"
        );

        when(repository.existsByCpf(dados.cpf())).thenReturn(true);

        assertThrows(RegraDeNegocioException.class, () -> clienteService.cadastraCliente(dados));
        verify(repository, never()).save(any());
    }

    @Test
    void deveListarClientes() {
        Pageable pageable = Pageable.unpaged();
        Cliente cliente = new Cliente();
        Page<Cliente> page = new PageImpl<>(List.of(cliente));
        ClienteListagemDadosResponse responseListagem = new ClienteListagemDadosResponse(cliente);

        when(repository.findAll(pageable)).thenReturn(page);
        when(clienteMapper.toClienteListagemResponse(cliente)).thenReturn(responseListagem);

        var resultado = clienteService.listaCliente(pageable);

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
    }

    @Test
    void deveAtualizarClienteComSucesso() {
        Long id = 1L;
        ClienteAtualizarDadosRequest dados = new ClienteAtualizarDadosRequest(
                "Novo Nome", "novo@email.com", "99999999", "Nova Rua"
        );
        Cliente cliente = spy(new Cliente());
        cliente.setId(id);
        ClienteMostrarDadosResponse response = new ClienteMostrarDadosResponse(
                id, "Novo Nome", "Nova Rua", "novo@email.com", "99999999", StatusCliente.ATIVO
        );

        when(repository.findById(id)).thenReturn(Optional.of(cliente));
        when(clienteMapper.toClienteResponse(cliente)).thenReturn(response);

        var resultado = clienteService.atualizarCliente(id, dados);

        assertNotNull(resultado);
        verify(cliente).atualizaCliente(dados);
    }

    @Test
    void deveBloquearClienteComSucesso() {
        Long id = 1L;
        Cliente cliente = new Cliente();
        cliente.setId(id);
        cliente.setStatus(StatusCliente.ATIVO);

        when(repository.findById(id)).thenReturn(Optional.of(cliente));

        clienteService.bloquear(id);

        assertEquals(StatusCliente.BLOQUEADO, cliente.getStatus());
    }

    @Test
    void deveLancarExceptionAoBloquearClienteJaBloqueado() {
        Long id = 1L;
        Cliente cliente = new Cliente();
        cliente.setId(id);
        cliente.setStatus(StatusCliente.BLOQUEADO);

        when(repository.findById(id)).thenReturn(Optional.of(cliente));

        assertThrows(RegraDeNegocioException.class, () -> clienteService.bloquear(id));
    }

    @Test
    void deveTornarClienteInadimplenteComSucesso() {
        Long id = 1L;
        Cliente cliente = new Cliente();
        cliente.setId(id);
        cliente.setStatus(StatusCliente.ATIVO);

        when(repository.findById(id)).thenReturn(Optional.of(cliente));

        clienteService.inadimplencia(id);

        assertEquals(StatusCliente.INADIMPLENTE, cliente.getStatus());
    }

    @Test
    void deveAtivarClienteComSucesso() {
        Long id = 1L;
        Cliente cliente = new Cliente();
        cliente.setId(id);
        cliente.setStatus(StatusCliente.BLOQUEADO);

        when(repository.findById(id)).thenReturn(Optional.of(cliente));

        clienteService.ativaCliente(id);

        assertEquals(StatusCliente.ATIVO, cliente.getStatus());
    }

    @Test
    void deveLancarExceptionAoBuscarClienteInexistente() {
        Long id = 99L;
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> clienteService.buscarClientePorId(id));
    }
}