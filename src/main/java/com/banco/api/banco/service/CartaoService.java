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
    public final GeradorDeCartaoUtil geradorDeCartaoUtil;
    private final List<ValidadorSolicitacaoCredito> validadores;


    public CartaoService(CartaoRepository cartaoRepository,
                         CartaoMapper cartaoMapper,
                         ContaRepository contaRepository,
                         GeradorDeCartaoUtil geradorDeCartaoUtil,
                         List<ValidadorSolicitacaoCredito> validadores) {
        this.cartaoRepository = cartaoRepository;
        this.cartaoMapper = cartaoMapper;
        this.contaRepository = contaRepository;
        this.geradorDeCartaoUtil = geradorDeCartaoUtil;
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

        String numeroGerado = geradorDeCartaoUtil.geraNumeroCartao();
        String cvvGerado = geradorDeCartaoUtil.geraCvv();

        cartao.setNumeroCartao(numeroGerado);
        cartao.setCvv(cvvGerado);

        cartao.setDataVencimento(LocalDate.now().plusYears(5));

        cartao.setStatus(StatusCartao.CARTAO_ATIVO);

        cartaoRepository.save(cartao);

        return new CartaoDebitoMostrarDadosResponse(cartao);
    }

    @Transactional
    public CartaoCreditoMostrarDadosResponse solicitaCartaoCredito(CartaoCreditoCriarDadosRequest dados){
        Conta conta = buscarContaPorId(dados.idConta());
        Cliente cliente = conta.getCliente();

        validadores.forEach(v -> v.validar(cliente, conta));

        Cartao cartao = cartaoMapper.toEntityCredito(dados, conta);
        if (cartaoRepository.existsByContaAndTipoCartao(cartao, cartao.getTipoCartao())){
            throw new RegraDeNegocioException("O cliente ja tem um cartão de credito");
        }

        return new CartaoCreditoMostrarDadosResponse(cartao);
    }

    //TODO criar o mapper para fazer o builder do cartao
    //TODO setar o status em que o cartao ira ficar antes de ser aprovado (aguardando solicitacao)
    //TODO salvar

    private Conta buscarContaPorId(Long id) {
        return contaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Conta com ID " + id + " não encontrada."));
    }
}
