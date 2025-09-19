package com.banco.api.banco.service;

import com.banco.api.banco.model.dto.Request.DadosCadastroRequest;
import com.banco.api.banco.model.dto.response.DadosCadastroResponse;
import com.banco.api.banco.model.dto.response.DadosListagemClientes;
import com.banco.api.banco.model.entity.Cliente;
import com.banco.api.banco.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ClienteService {

    @Autowired
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
}
