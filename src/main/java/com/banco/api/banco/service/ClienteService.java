package com.banco.api.banco.service;

import com.banco.api.banco.controller.cliente.request.ClienteAtualizarDadosRequest;
import com.banco.api.banco.controller.cliente.request.ClienteCadastroDadosRequest;
import com.banco.api.banco.controller.cliente.response.ClienteMostrarDadosResponse;
import com.banco.api.banco.controller.cliente.response.ClienteListagemDadosResponse;
import com.banco.api.banco.enums.StatusCliente;
import com.banco.api.banco.model.entity.Cliente;
import com.banco.api.banco.repository.ClienteRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ClienteService {

    private final ClienteRepository repository;

    public ClienteService(ClienteRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public ClienteMostrarDadosResponse cadastraCliente(ClienteCadastroDadosRequest dados) {

        Cliente cliente = Cliente.builder()
                .nome(dados.nome())
                .cpf(dados.cpf())
                .email(dados.email())
                .dataNascimento(dados.dataNascimento())
                .endereco(dados.endereco())
                .telefone(dados.telefone())
                .status(StatusCliente.ATIVO)
                .build();
        repository.save(cliente);
        return new ClienteMostrarDadosResponse(cliente);
    }

    public Page<ClienteListagemDadosResponse> listaCliente(Pageable pageable){
        return repository.findAll(pageable).map(ClienteListagemDadosResponse::new);
    }

    @Transactional
    public Cliente atualizarCliente (Long id, ClienteAtualizarDadosRequest dados){
        Cliente cliente = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado na base de dados!"));

        if (dados.nome() != null) {
            cliente.setNome(dados.nome());
        }
        if (dados.endereco() != null) {
            cliente.setEndereco(dados.endereco());
        }
        if (dados.email() != null) {
            cliente.setEmail(dados.email());
        }
        if (dados.telefone() != null) {
            cliente.setTelefone(dados.telefone());
        }

        return cliente;
    }

    @Transactional
    public void desativarCliente (Long id) {
        Optional<Cliente> optionalCliente = repository.findById(id);
        if (optionalCliente.isPresent()) {
            Cliente cliente = optionalCliente.get();
            cliente.bloquear();
            repository.save(cliente);
        }
    }
}
