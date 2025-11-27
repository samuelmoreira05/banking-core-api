package com.banco.api.banco.service;

import com.banco.api.banco.controller.conta.request.ContaCadastroDadosRequest;
import com.banco.api.banco.controller.conta.response.ContaListagemDadosResponse;
import com.banco.api.banco.controller.conta.response.ContaMostrarDadosResponse;
import com.banco.api.banco.enums.StatusConta;
import com.banco.api.banco.mapper.ContaMapper;
import com.banco.api.banco.model.entity.Cliente;
import com.banco.api.banco.model.entity.Conta;
import com.banco.api.banco.repository.ClienteRepository;
import com.banco.api.banco.repository.ContaRepository;
import com.banco.api.banco.util.GeradorDeContaUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ContaService {

    private final ContaRepository repository;
    private final ClienteRepository clienteRepository;
    private final ContaMapper contaMapper;
    private final GeradorDeContaUtil geradorDeContaUtil;

    public ContaService(ContaRepository repository,
                        ClienteRepository clienteRepository,
                        ContaMapper contaMapper,
                        GeradorDeContaUtil geradorDeContaUtil) {
        this.repository = repository;
        this.clienteRepository = clienteRepository;
        this.contaMapper = contaMapper;
        this.geradorDeContaUtil = geradorDeContaUtil;
    }

    @Transactional
    public ContaMostrarDadosResponse criarConta(ContaCadastroDadosRequest dados) {
        Cliente cliente = clienteRepository.findById(dados.clienteId())
                .orElseThrow(() -> new EntityNotFoundException("Cliente não foi encontrado"));


        Conta conta = contaMapper.toEntity(dados, cliente);

        conta.setAgencia(geradorDeContaUtil.gerarAgencia());

        String numeroConta;
        do {
            numeroConta = geradorDeContaUtil.gerarNumeroConta();
        }while (repository.existsByNumeroConta(numeroConta));
        conta.setNumeroConta(numeroConta);

        conta.setStatus(StatusConta.ATIVO);

        repository.save(conta);

        return new ContaMostrarDadosResponse(conta);
    }

    public Page<ContaListagemDadosResponse> listarConta(Pageable pageable) {
        Page<Conta> contaPage = repository.findAll(pageable);
        return contaPage.map(ContaListagemDadosResponse::new);
    }

    @Transactional
    public void encerraConta (Long id){
        Conta conta = buscarContaPorId(id);

            conta.encerraConta();
    }

    @Transactional
    public void suspendeConta(Long id){
        Conta conta = buscarContaPorId(id);

        conta.suspendeConta();
    }

    @Transactional
    public void  ativaConta(Long id){
        Conta conta = buscarContaPorId(id);

        conta.ativaConta();
    }

    private Conta buscarContaPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Conta com ID " + id + " não encontrada."));

    }
}
