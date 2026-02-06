package com.banco.api.banco.service.factory;

import com.banco.api.banco.repository.ContaRepository;
import com.banco.api.banco.util.GeradorDeContaUtil;
import org.springframework.stereotype.Component;

@Component
public class ContaFactory {

    private final GeradorDeContaUtil geradorDeContaUtil;
    private final ContaRepository repository;

    public ContaFactory(GeradorDeContaUtil geradorDeContaUtil, ContaRepository repository) {
        this.geradorDeContaUtil = geradorDeContaUtil;
        this.repository = repository;
    }

    public String gerarNumeroContaUnico() {
        String numeroConta;
        do {
            numeroConta = geradorDeContaUtil.gerarNumeroConta();
        }while (repository.existsByNumeroConta(numeroConta));
        return numeroConta;
    }
}
