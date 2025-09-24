package com.banco.api.banco.service;

import com.banco.api.banco.controller.cliente.request.DadosAtualizarCliente;
import com.banco.api.banco.controller.cliente.request.DadosCadastroRequest;
import com.banco.api.banco.controller.cliente.response.DadosCadastroResponse;
import com.banco.api.banco.controller.cliente.response.DadosListagemClientes;
import com.banco.api.banco.model.entity.Cliente;
import com.banco.api.banco.repository.ClienteRepository;
import jakarta.persistence.EntityNotFoundException;
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

    public DadosCadastroResponse cadastraCliente(DadosCadastroRequest dados) {
        Cliente cliente = repository.save(new Cliente(dados));
        return new DadosCadastroResponse(cliente);
    }

    public Page<DadosListagemClientes> listaCliente(Pageable pageable){
        Page<Cliente> clientePage = repository.findAll(pageable);
        return clientePage.map(DadosListagemClientes::new);
    }

    public Cliente atualizarCliente (Long id,DadosAtualizarCliente dados){
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException ("Usuario não encontrado na base de dados!");
        }
        Cliente cliente = repository.getReferenceById(id);
        cliente.atualizarCliente(dados);
        return repository.save(cliente);
    }

    public void desativarCliente (Long id) {
        Optional<Cliente> optionalCliente = repository.findById(id);
        if (optionalCliente.isPresent()) {
            Cliente cliente = optionalCliente.get();
            cliente.desativar();
            repository.save(cliente);
        }
    }
}
