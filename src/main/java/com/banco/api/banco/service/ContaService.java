package com.banco.api.banco.service;

import com.banco.api.banco.controller.conta.request.DadosCadastroConta;
import com.banco.api.banco.controller.conta.response.DadosMostrarConta;
import com.banco.api.banco.model.entity.Cliente;
import com.banco.api.banco.model.entity.Conta;
import com.banco.api.banco.repository.ClienteRepository;
import com.banco.api.banco.repository.ContaRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ContaService {

    private final ContaRepository repository;
    private final ClienteRepository clienteRepository;

    public ContaService(ContaRepository repository, ClienteRepository clienteRepository) {
        this.repository = repository;
        this.clienteRepository = clienteRepository;
    }

    public DadosMostrarConta criarConta(DadosCadastroConta dados) {
        Conta conta = repository.save(new Conta(dados));
        return new DadosMostrarConta(conta);
    }
}
