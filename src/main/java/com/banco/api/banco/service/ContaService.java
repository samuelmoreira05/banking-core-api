package com.banco.api.banco.service;

import com.banco.api.banco.controller.conta.request.ContaCadastroDadosRequest;
import com.banco.api.banco.controller.conta.response.ContaListagemDadosResponse;
import com.banco.api.banco.controller.conta.response.ContaMostrarDadosResponse;
import com.banco.api.banco.enums.StatusCliente;
import com.banco.api.banco.enums.StatusConta;
import com.banco.api.banco.infra.exception.RegraDeNegocioException;
import com.banco.api.banco.mapper.ContaMapper;
import com.banco.api.banco.model.entity.Cliente;
import com.banco.api.banco.model.entity.Conta;
import com.banco.api.banco.repository.ContaRepository;
import com.banco.api.banco.service.factory.ContaFactory;
import com.banco.api.banco.util.GeradorDeContaUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.function.Consumer;

@Service
public class ContaService {

    private final ContaRepository repository;
    private final ContaMapper contaMapper;
    private final ClienteService clienteService;
    private final GeradorDeContaUtil geradorDeContaUtil;
    private final ContaFactory contaFactory;

    public ContaService(ContaRepository repository,
                        ContaMapper contaMapper, ClienteService clienteService,
                        GeradorDeContaUtil geradorDeContaUtil, ContaFactory contaFactory) {
        this.repository = repository;
        this.contaMapper = contaMapper;
        this.clienteService = clienteService;
        this.geradorDeContaUtil = geradorDeContaUtil;
        this.contaFactory = contaFactory;
    }

    @Transactional
    public ContaMostrarDadosResponse criarConta(ContaCadastroDadosRequest dados) {
        Cliente cliente = clienteService.buscarClientePorId(dados.clienteId());

        Conta conta = contaMapper.toEntity(dados, cliente);

        conta.setAgencia(geradorDeContaUtil.gerarAgencia());
        conta.setNumeroConta(contaFactory.gerarNumeroContaUnico());

        conta.setStatus(StatusConta.ATIVO);

        repository.save(conta);

        return contaMapper.toContaResponse(conta);
    }

    public Page<ContaListagemDadosResponse> listarConta(Pageable pageable) {
        Page<Conta> contaPage = repository.findAll(pageable);
        return contaPage.map(ContaListagemDadosResponse::new);
    }

    @Transactional
    public void encerraConta (Long id){
        executarAcaoConta(id, conta -> {
            if (conta.getSaldo().compareTo(BigDecimal.ZERO) != 0) {
                throw new RegraDeNegocioException("Conta nao pode ser encerrada pois possui saldo pendente");
            }
            conta.encerraConta();
        });
    }

    @Transactional
    public void suspendeConta(Long id){
        executarAcaoConta(id, Conta::suspendeConta);
    }

    @Transactional
    public void  ativaConta(Long id){
        executarAcaoConta(id, Conta::ativaConta);
    }

    private void executarAcaoConta(Long id, java.util.function.Consumer<Conta> acao) {
        Conta conta = buscarContaPorId(id);
        acao.accept(conta);
    }

    public Conta buscarContaPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Conta com ID " + id + " não encontrada."));

    }
}
