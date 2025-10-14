package com.banco.api.banco.mapper;

import com.banco.api.banco.controller.cartao.request.CartaoDebitoCriarDadosRequest;
import com.banco.api.banco.enums.TipoCartao;
import com.banco.api.banco.model.entity.Cartao;
import com.banco.api.banco.model.entity.Conta;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class CartaoMapper {

    public Cartao toEntity(CartaoDebitoCriarDadosRequest dados, Conta conta) {
        Cartao cartao = Cartao.builder()
                .conta(conta)
                .tipoCartao(TipoCartao.DEBITO)
                .build();

        return cartao;
    }
}
