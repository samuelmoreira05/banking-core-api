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
import com.banco.api.banco.service.calculadora.CalculadoraLimiteCartao;
import com.banco.api.banco.service.validadores.cartaoCredito.ValidadorSolicitacaoCredito;
import com.banco.api.banco.util.GeradorDeCartaoUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class CartaoService {

    private final CartaoRepository cartaoRepository;
    private final ContaService contaService;
    private final CartaoMapper cartaoMapper;
    private final PasswordEncoder passwordEncoder;
    private final GeradorDeCartaoUtil geradorDeCartaoUtil;
    private final CalculadoraLimiteCartao calculadoraLimiteCartao;
    private final List<ValidadorSolicitacaoCredito> validadores;


    public CartaoService(CartaoRepository cartaoRepository,
                         CartaoMapper cartaoMapper,
                         PasswordEncoder passwordEncoder,
                         ContaService contaService,
                         GeradorDeCartaoUtil geradorDeCartaoUtil,
                         CalculadoraLimiteCartao calculadoraLimiteCartao,
                         List<ValidadorSolicitacaoCredito> validadores) {
        this.cartaoRepository = cartaoRepository;
        this.cartaoMapper = cartaoMapper;
        this.passwordEncoder = passwordEncoder;
        this.contaService = contaService;
        this.geradorDeCartaoUtil = geradorDeCartaoUtil;
        this.calculadoraLimiteCartao = calculadoraLimiteCartao;
        this.validadores = validadores;
    }

    @Transactional
    public CartaoDebitoMostrarDadosResponse solicitaCartaoDebito(CartaoDebitoCriarDadosRequest dados) {
        Conta conta = contaService.buscarContaPorId(dados.idConta());

        Cliente cliente = conta.getCliente();

        if (cliente.getStatus() != StatusCliente.ATIVO){
            throw new RegraDeNegocioException("Para solicitar um cartao o cliente deve estar com Status de ativo, status atual: " + cliente.getStatus());
        }

        var senhaHash = passwordEncoder.encode(dados.senha());

        Cartao cartao = cartaoMapper.toEntity(dados, conta, senhaHash);

        cartao = finalizarCriacaoCartao(cartao);

        return cartaoMapper.toDebitoResponse(cartao);
    }

    @Transactional
    public CartaoCreditoMostrarDadosResponse solicitaCartaoCredito(CartaoCreditoCriarDadosRequest dados){
        Conta conta = contaService.buscarContaPorId(dados.idConta());
        Cliente cliente = conta.getCliente();

        validadores.forEach(v -> v.validar(cliente, conta));

        var senhaHash = passwordEncoder.encode(dados.senha());

        Cartao cartao = cartaoMapper.toEntityCredito(dados, conta, senhaHash);

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

    public Cartao buscaNumeroCartao(String numeroCartao){
        return cartaoRepository.findByNumeroCartao(numeroCartao)
                .orElseThrow(() -> new EntityNotFoundException("Cartão nao encontrado"));
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
