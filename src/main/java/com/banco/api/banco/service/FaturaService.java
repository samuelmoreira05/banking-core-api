package com.banco.api.banco.service;

import com.banco.api.banco.enums.StatusFatura;
import com.banco.api.banco.infra.exception.RegraDeNegocioException;
import com.banco.api.banco.mapper.FaturaMapper;
import com.banco.api.banco.model.entity.Cartao;
import com.banco.api.banco.model.entity.Fatura;
import com.banco.api.banco.repository.FaturaRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class FaturaService {

    private final FaturaRepository faturaRepository;
    private final FaturaMapper faturaMapper;

    public FaturaService(FaturaRepository faturaRepository,
                         FaturaMapper faturaMapper) {
        this.faturaRepository = faturaRepository;
        this.faturaMapper = faturaMapper;
    }

    @Transactional
    public Fatura processarCompraCredito(Cartao cartao, BigDecimal valorCompra) {
        Fatura fatura = getFaturaAtual(cartao);

        validarLimiteDisponivel(cartao, fatura, valorCompra);

        fatura.setValorTotal(fatura.getValorTotal().add(valorCompra));
        return faturaRepository.save(fatura);
    }

    public Fatura getFaturaAtual(Cartao cartao){
        return faturaRepository.findByCartaoAndStatus(cartao, StatusFatura.ABERTA)
                .orElseGet(() -> criaNovaFatura(cartao));
    }

    private Fatura criaNovaFatura(Cartao cartao){
        Fatura novaFatura = faturaMapper.toEntityFaturaInicial(cartao);
        return faturaRepository.save(novaFatura);
    }

    private void validarLimiteDisponivel(Cartao cartao, Fatura fatura, BigDecimal valorCompra) {
        BigDecimal limiteConsumido = fatura.getValorTotal();
        BigDecimal limiteDisponivel = cartao.getLimiteCredito().subtract(limiteConsumido);

        if (valorCompra.compareTo(limiteDisponivel) > 0) {
            throw new RegraDeNegocioException("Limite insuficiente. Disponível: " + limiteDisponivel);
        }
    }

}
