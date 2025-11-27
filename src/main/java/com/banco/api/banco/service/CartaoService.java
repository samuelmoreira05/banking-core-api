package com.banco.api.banco.service;

import com.banco.api.banco.controller.cartao.request.CartaoCreditoCriarDadosRequest;
import com.banco.api.banco.controller.cartao.request.CartaoDebitoCriarDadosRequest;
import com.banco.api.banco.controller.cartao.response.CartaoCreditoMostrarDadosResponse;
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
import com.banco.api.banco.service.calculadora.CalculadoraLimiteCartao;
import com.banco.api.banco.service.validadores.cartao.ValidadorSolicitacaoCredito;
import com.banco.api.banco.util.GeradorDeCartaoUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class CartaoService {

    private final CartaoRepository cartaoRepository;
    private final CartaoMapper cartaoMapper;
    private final ContaRepository contaRepository;
    private final GeradorDeCartaoUtil geradorDeCartaoUtil;
    private final CalculadoraLimiteCartao calculadoraLimiteCartao;
    private final List<ValidadorSolicitacaoCredito> validadores;


    public CartaoService(CartaoRepository cartaoRepository,
                         CartaoMapper cartaoMapper,
                         ContaRepository contaRepository,
                         GeradorDeCartaoUtil geradorDeCartaoUtil, CalculadoraLimiteCartao calculadoraLimiteCartao,
                         List<ValidadorSolicitacaoCredito> validadores) {
        this.cartaoRepository = cartaoRepository;
        this.cartaoMapper = cartaoMapper;
        this.contaRepository = contaRepository;
        this.geradorDeCartaoUtil = geradorDeCartaoUtil;
        this.calculadoraLimiteCartao = calculadoraLimiteCartao;
        this.validadores = validadores;
    }

    @Transactional
    public CartaoDebitoMostrarDadosResponse solicitaCartaoDebito(CartaoDebitoCriarDadosRequest dados) {
        Conta conta = buscarContaPorId(dados.idConta());

        Cliente cliente = conta.getCliente();

        if (cliente.getStatus() != StatusCliente.ATIVO){
            throw new RegraDeNegocioException("Para solicitar um cartao o cliente deve estar com Status de ativo, status atual: " + cliente.getStatus());
        }

        Cartao cartao = cartaoMapper.toEntity(dados, conta);

        cartao = finalizarCriacaoCartao(cartao);

        return cartaoMapper.toDebitoResponse(cartao);
    }

    @Transactional
    public CartaoCreditoMostrarDadosResponse solicitaCartaoCredito(CartaoCreditoCriarDadosRequest dados){
        Conta conta = buscarContaPorId(dados.idConta());
        Cliente cliente = conta.getCliente();

        validadores.forEach(v -> v.validar(cliente, conta));

        Cartao cartao = cartaoMapper.toEntityCredito(dados, conta);

        BigDecimal limiteCartao = calculadoraLimiteCartao.limite(conta);

        cartao.setLimiteCredito(limiteCartao);

        cartao.setDiaVencimentoFatura(10);

        cartao = finalizarCriacaoCartao(cartao);

        return cartaoMapper.toCreditoResponse(cartao);
    }

    @Transactional
    public void bloqueiaCartao(Long id) {
        Cartao cartao = buscarCartaoPorId(id);

        cartao.bloqueiaCartao();
    }

    @Transactional
    public void ativarCartao(Long id) {
        Cartao cartao = buscarCartaoPorId(id);

        cartao.ativaCartao();
    }

    private Conta buscarContaPorId(Long id) {
        return contaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Conta com ID " + id + " não encontrada."));
    }

    private Cartao buscarCartaoPorId(Long id) {
        return cartaoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cartao não encontrado na base de dados!"));
    }

    private Cartao finalizarCriacaoCartao (Cartao cartao) {
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
