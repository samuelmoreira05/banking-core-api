package com.banco.api.banco.service;

import com.banco.api.banco.controller.conta.request.DadosCadastroContaRequest;
import com.banco.api.banco.controller.conta.response.DadosListagemContasResponse;
import com.banco.api.banco.controller.conta.response.DadosMostrarContaResponse;
import com.banco.api.banco.model.entity.Cliente;
import com.banco.api.banco.model.entity.Conta;
import com.banco.api.banco.repository.ClienteRepository;
import com.banco.api.banco.repository.ContaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ContaService {

    private final ContaRepository repository;
    private final ClienteRepository clienteRepository;

    public ContaService(ContaRepository repository, ClienteRepository clienteRepository) {
        this.repository = repository;
        this.clienteRepository = clienteRepository;
    }

    public DadosMostrarContaResponse criarConta(DadosCadastroContaRequest dados) {
        Cliente cliente = clienteRepository.findById(dados.clienteId())
                .orElseThrow(() -> new EntityNotFoundException("Cliente não foi encontrado"));

        Conta conta = repository.save(new Conta(dados, cliente));
        return new DadosMostrarContaResponse(conta);
    }

    public Page<DadosListagemContasResponse> listarConta(Pageable pageable) {
        Page<Conta> contaPage = repository.findAll(pageable);
        return contaPage.map(DadosListagemContasResponse::new);
    }
}
