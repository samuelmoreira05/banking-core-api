package com.banco.api.banco.service;

import com.banco.api.banco.controller.cartao.request.CartaoDebitoCriarDadosRequest;
import com.banco.api.banco.controller.cartao.response.CartaoDebitoMostrarDadosResponse;
import com.banco.api.banco.enums.StatusCartao;
import com.banco.api.banco.enums.StatusCliente;
import com.banco.api.banco.infra.exception.RegraDeNegocioException;
import com.banco.api.banco.mapper.CartaoMapper;
import com.banco.api.banco.model.entity.Cartao;
import com.banco.api.banco.model.entity.Cliente;
import com.banco.api.banco.model.entity.Conta;
import com.banco.api.banco.repository.CartaoRepository;
import com.banco.api.banco.repository.ContaRepository;
import com.banco.api.banco.util.GeradorDeCartaoUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class CartaoService {

    private final CartaoRepository cartaoRepository;
    private final CartaoMapper cartaoMapper;
    private final ContaRepository contaRepository;
    public final GeradorDeCartaoUtil geradorDeCartaoUtil;


    public CartaoService(CartaoRepository cartaoRepository,
                         CartaoMapper cartaoMapper,
                         ContaRepository contaRepository,
                         GeradorDeCartaoUtil geradorDeCartaoUtil) {
        this.cartaoRepository = cartaoRepository;
        this.cartaoMapper = cartaoMapper;
        this.contaRepository = contaRepository;
        this.geradorDeCartaoUtil = geradorDeCartaoUtil;
    }

    @Transactional
    public CartaoDebitoMostrarDadosResponse solicitaCartaoDebito(CartaoDebitoCriarDadosRequest dados) {
        Conta conta = contaRepository.findById(dados.idConta())
                .orElseThrow(() -> new EntityNotFoundException("A conta não foi encontrada na base de dados"));

        Cliente cliente = conta.getCliente();

        if (cliente.getStatus() != StatusCliente.ATIVO){
            throw new RegraDeNegocioException("PAra solicitar um cartao o cliente deve estar com Status de ativo, status atual: " + cliente.getStatus());
        }

        Cartao cartao = cartaoMapper.toEntity(dados, conta);

        String numeroGerado = GeradorDeCartaoUtil.geraNumeroCartao();
        String cvvGerado = GeradorDeCartaoUtil.geraCvv();

        cartao.setNumeroCartao(numeroGerado);
        cartao.setCvv(cvvGerado);
        cartao.setStatusCartao(StatusCartao.CARTAO_ATIVO);

        cartaoRepository.save(cartao);

        return new CartaoDebitoMostrarDadosResponse(cartao);
    }

    //TODO Precisa validar se o cliente existe e tem um Status_ativo
    //TODO O cliente pode escolher solicitar um cartão de credito ou debito

    private Conta buscarContaPorId(Long id) {
        return contaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Conta com ID " + id + " não encontrada."));

    }
}
