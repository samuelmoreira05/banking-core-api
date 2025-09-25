package com.banco.api.banco.service;

import com.banco.api.banco.controller.conta.request.ContaCadastroDadosRequest;
import com.banco.api.banco.controller.conta.response.ContaListagemDadosResponse;
import com.banco.api.banco.controller.conta.response.ContaMostrarDadosResponse;
import com.banco.api.banco.model.entity.Cliente;
import com.banco.api.banco.model.entity.Conta;
import com.banco.api.banco.repository.ClienteRepository;
import com.banco.api.banco.repository.ContaRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.Builder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Builder
public class ContaService {

    private final ContaRepository repository;
    private final ClienteRepository clienteRepository;

    public ContaService(ContaRepository repository, ClienteRepository clienteRepository) {
        this.repository = repository;
        this.clienteRepository = clienteRepository;
    }

    @Transactional
    public ContaMostrarDadosResponse criarConta(ContaCadastroDadosRequest dados) {
        Cliente cliente = clienteRepository.findById(dados.clienteId())
                .orElseThrow(() -> new EntityNotFoundException("Cliente não foi encontrado"));

        Conta conta = Conta.builder()
                .cliente(cliente)
                .tipoConta(dados.tipo())
                .build();

        repository.save(conta);
        return new ContaMostrarDadosResponse(conta);
    }

    public Page<ContaListagemDadosResponse> listarConta(Pageable pageable) {
        Page<Conta> contaPage = repository.findAll(pageable);
        return contaPage.map(ContaListagemDadosResponse::new);
    }

    @Transactional
    public void encerraConta (Long id){
        Conta conta = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Conta não encontrada na base de dados: " + id));

            conta.encerraConta();
    }

    @Transactional
    public void suspendeConta(Long id){
        Conta conta = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Conta não encontrada na base de dados: " + id));

        conta.suspendeConta();
    }

    @Transactional
    public void  ativaConta(Long id){
        Conta conta = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Conta não encontrada na base de dados: " + id));

        conta.ativaConta();
    }
}
