package com.banco.api.banco.service;

import com.banco.api.banco.controller.conta.request.DadosCadastroContaRequest;
import com.banco.api.banco.controller.conta.response.DadosListagemContasResponse;
import com.banco.api.banco.controller.conta.response.DadosMostrarContaResponse;
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

import java.util.Optional;

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
    public DadosMostrarContaResponse criarConta(DadosCadastroContaRequest dados) {
        Cliente cliente = clienteRepository.findById(dados.clienteId())
                .orElseThrow(() -> new EntityNotFoundException("Cliente não foi encontrado"));

        Conta conta = Conta.builder()
                .cliente(cliente)
                .tipoConta(dados.tipo())
                .build();

        repository.save(conta);
        return new DadosMostrarContaResponse(conta);
    }

    public Page<DadosListagemContasResponse> listarConta(Pageable pageable) {
        Page<Conta> contaPage = repository.findAll(pageable);
        return contaPage.map(DadosListagemContasResponse::new);
    }

    @Transactional
    public void encerraConta (Long id){
        Conta conta = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Conta não encontrada na base de dados: " + id));

            conta.encerraConta();
            repository.save(conta);
    }

    @Transactional
    public void suspendeConta(Long id){
        Conta conta = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Conta não encontrada na base de dados: " + id));

        conta.suspendeConta();
        repository.save(conta);
    }

    @Transactional
    public void  ativaConta(Long id){
        Conta conta = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Conta não encontrada na base de dados: " + id));

        conta.ativaConta();
        repository.save(conta);
    }
}
