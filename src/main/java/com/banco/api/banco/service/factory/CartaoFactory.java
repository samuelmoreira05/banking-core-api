package com.banco.api.banco.service.factory;

import com.banco.api.banco.enums.StatusCartao;
import com.banco.api.banco.model.entity.Cartao;
import com.banco.api.banco.repository.CartaoRepository;
import com.banco.api.banco.util.GeradorDeCartaoUtil;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class CartaoFactory {

    private final GeradorDeCartaoUtil geradorDeCartaoUtil;
    private final CartaoRepository cartaoRepository;

    public CartaoFactory(GeradorDeCartaoUtil geradorDeCartaoUtil, CartaoRepository cartaoRepository) {
        this.geradorDeCartaoUtil = geradorDeCartaoUtil;
        this.cartaoRepository = cartaoRepository;
    }

    public Cartao finalizarCriacaoCartao (Cartao cartao) {
        String numeroGerado;
        do {
            numeroGerado = geradorDeCartaoUtil.geraNumeroCartao();
        }while (cartaoRepository.existsByNumeroCartao(numeroGerado));

        cartao.setStatus(StatusCartao.CARTAO_ATIVO);
        cartao.setNumeroCartao(numeroGerado);
        cartao.setDataVencimento(LocalDate.now().plusYears(5));
        cartao.setCvv(geradorDeCartaoUtil.geraCvv());

        return cartaoRepository.save(cartao);
    }
}
