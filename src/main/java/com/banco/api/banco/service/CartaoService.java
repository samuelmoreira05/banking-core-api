package com.banco.api.banco.service;

import com.banco.api.banco.controller.cartao.request.CartaoCreditoCriarDadosRequest;
import com.banco.api.banco.controller.cartao.request.CartaoDebitoCriarDadosRequest;
import com.banco.api.banco.controller.cartao.response.CartaoCreditoMostrarDadosResponse;
import com.banco.api.banco.controller.cartao.response.CartaoDebitoMostrarDadosResponse;
import com.banco.api.banco.enums.TipoCartao;
import com.banco.api.banco.mapper.CartaoMapper;
import com.banco.api.banco.model.entity.Cartao;
import com.banco.api.banco.model.entity.Cliente;
import com.banco.api.banco.model.entity.Conta;
import com.banco.api.banco.repository.CartaoRepository;
import com.banco.api.banco.service.calculadora.CalculadoraLimiteCartao;
import com.banco.api.banco.service.factory.CartaoFactory;
import com.banco.api.banco.service.validadores.cartaoCredito.ValidadorEmissaoCartao;
import com.banco.api.banco.util.GeradorDeCartaoUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CartaoService {

    private final CartaoRepository cartaoRepository;
    private final ContaService contaService;
    private final CartaoMapper cartaoMapper;
    private final PasswordEncoder passwordEncoder;
    private final CalculadoraLimiteCartao calculadoraLimiteCartao;
    private final List<ValidadorEmissaoCartao> validadores;
    private final CartaoFactory cartaoFactory;

    public CartaoService(CartaoRepository cartaoRepository,
                         CartaoMapper cartaoMapper,
                         PasswordEncoder passwordEncoder,
                         ContaService contaService,
                         GeradorDeCartaoUtil geradorDeCartaoUtil,
                         CalculadoraLimiteCartao calculadoraLimiteCartao,
                         List<ValidadorEmissaoCartao> validadores,
                         CartaoFactory cartaoFactory) {
        this.cartaoRepository = cartaoRepository;
        this.cartaoMapper = cartaoMapper;
        this.passwordEncoder = passwordEncoder;
        this.contaService = contaService;
        this.calculadoraLimiteCartao = calculadoraLimiteCartao;
        this.validadores = validadores;
        this.cartaoFactory = cartaoFactory;
    }

    @Transactional
    public CartaoDebitoMostrarDadosResponse solicitaCartaoDebito(CartaoDebitoCriarDadosRequest dados) {
        Conta conta = buscarEValidarConta(dados.idConta(), TipoCartao.DEBITO);

        var senhaHash = passwordEncoder.encode(dados.senha());

        Cartao cartao = cartaoMapper.toEntity(dados, conta, senhaHash);

        cartao = cartaoFactory.finalizarCriacaoCartao(cartao);

        return cartaoMapper.toDebitoResponse(cartao);
    }

    @Transactional
    public CartaoCreditoMostrarDadosResponse solicitaCartaoCredito(CartaoCreditoCriarDadosRequest dados){
        Conta conta = buscarEValidarConta(dados.idConta(), TipoCartao.CREDITO);

        var senhaHash = passwordEncoder.encode(dados.senha());

        Cartao cartao = cartaoMapper.toEntityCredito(dados, conta, senhaHash);

        BigDecimal limiteCartao = calculadoraLimiteCartao.limite(conta);
        cartao.setLimiteCredito(limiteCartao);
        cartao.setDiaVencimentoFatura(10);

        cartao = cartaoFactory.finalizarCriacaoCartao(cartao);

        return cartaoMapper.toCreditoResponse(cartao);
    }

    @Transactional
    public void bloqueiaCartao(Long id) {
        Cartao cartao = buscarCartaoPorId(id);
        cartao.bloqueiaCartao();
        cartaoRepository.save(cartao);
    }

    @Transactional
    public void ativarCartao(Long id) {
        Cartao cartao = buscarCartaoPorId(id);
        cartao.ativaCartao();
        cartaoRepository.save(cartao);
    }

    public Cartao buscaNumeroCartao(String numeroCartao){
        return cartaoRepository.findByNumeroCartao(numeroCartao)
                .orElseThrow(() -> new EntityNotFoundException("Cartão nao encontrado"));
    }

    private Cartao buscarCartaoPorId(Long id) {
        return cartaoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cartao não encontrado na base de dados!"));
    }

    private Conta buscarEValidarConta(Long idConta, TipoCartao tipoCartao) {
        Conta conta = contaService.buscarContaPorId(idConta);
        Cliente cliente = conta.getCliente();

        validadores.forEach(v -> v.validar(cliente, conta, tipoCartao));
        return conta;
    }
}