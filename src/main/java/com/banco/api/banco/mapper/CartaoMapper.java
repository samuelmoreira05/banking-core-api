package com.banco.api.banco.mapper;

import com.banco.api.banco.controller.cartao.request.CartaoCreditoCriarDadosRequest;
import com.banco.api.banco.controller.cartao.request.CartaoDebitoCriarDadosRequest;
import com.banco.api.banco.controller.cartao.response.CartaoCreditoMostrarDadosResponse;
import com.banco.api.banco.controller.cartao.response.CartaoDebitoMostrarDadosResponse;
import com.banco.api.banco.enums.TipoCartao;
import com.banco.api.banco.model.entity.Cartao;
import com.banco.api.banco.model.entity.Conta;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.time.format.DateTimeFormatter;

@Component
@Slf4j
@AllArgsConstructor
public class CartaoMapper {

    private static final DateTimeFormatter FORMATADOR_DATA = DateTimeFormatter.ofPattern("MM/yy");

    public Cartao toEntity(CartaoDebitoCriarDadosRequest dados, Conta conta, String senhaCriptografada) {
        Cartao cartao = Cartao.builder()
                .conta(conta)
                .tipoCartao(TipoCartao.DEBITO)
                .senha(senhaCriptografada)
                .build();

        return cartao;
    }

    public Cartao toEntityCredito(CartaoCreditoCriarDadosRequest dados, Conta conta, String senhaCriptografada){
        Cartao cartao = Cartao.builder()
                .conta(conta)
                .tipoCartao(TipoCartao.CREDITO)
                .senha(senhaCriptografada)
                .build();

        return cartao;
    }

    public CartaoCreditoMostrarDadosResponse toCreditoResponse(Cartao cartao) {
        return new CartaoCreditoMostrarDadosResponse(
                cartao.getConta().getCliente().getNome(),
                cartao.getConta().getAgencia(),
                cartao.getConta().getNumeroConta(),
                cartao.getNumeroCartao(),
                cartao.getDataVencimento().format(FORMATADOR_DATA),
                cartao.getDiaVencimentoFatura(),
                cartao.getLimiteCredito()
        );
    }

    public CartaoDebitoMostrarDadosResponse  toDebitoResponse(Cartao cartao) {
        return new CartaoDebitoMostrarDadosResponse(
                cartao.getConta().getCliente().getNome(),
                cartao.getConta().getAgencia(),
                cartao.getConta().getNumeroConta(),
                cartao.getNumeroCartao(),
                cartao.getDataVencimento().format(FORMATADOR_DATA)
        );
    }
}
