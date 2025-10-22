package com.banco.api.banco.service;

import com.banco.api.banco.controller.cartao.request.CartaoDebitoCriarDadosRequest;
import com.banco.api.banco.enums.StatusCartao;
import com.banco.api.banco.enums.StatusCliente;
import com.banco.api.banco.enums.TipoCartao;
import com.banco.api.banco.infra.exception.RegraDeNegocioException;
import com.banco.api.banco.mapper.CartaoMapper;
import com.banco.api.banco.model.entity.Cartao;
import com.banco.api.banco.model.entity.Cliente;
import com.banco.api.banco.model.entity.Conta;
import com.banco.api.banco.repository.CartaoRepository;
import com.banco.api.banco.repository.ContaRepository;
import com.banco.api.banco.util.GeradorDeCartaoUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartaoServiceTest {

    @Mock private ContaRepository contaRepository;
    @Mock private CartaoMapper cartaoMapper;
    @Mock private GeradorDeCartaoUtil geradorDeCartaoUtil;
    @Mock private CartaoRepository cartaoRepository;

    @InjectMocks private CartaoService cartaoService;

    @Captor private ArgumentCaptor<Cartao> captor;

    @Test
    void solicitacaoCartaoDebitoSucesso() {
        var idConta = 1L;
        var idCliente = 2L;

        Cliente cliente = new Cliente();
        cliente.setStatus(StatusCliente.ATIVO);
        cliente.setId(idCliente);

        Conta conta = new Conta();
        conta.setId(idConta);
        conta.setCliente(cliente);

        CartaoDebitoCriarDadosRequest dadosSolicitacao = new CartaoDebitoCriarDadosRequest(
                conta.getId()
        );

        when(contaRepository.findById(idConta)).thenReturn(Optional.of(conta));
        when(cartaoMapper.toEntity(any(CartaoDebitoCriarDadosRequest.class), any(Conta.class)))
                .thenReturn(Cartao.builder()
                .conta(conta)
                .tipoCartao(TipoCartao.DEBITO)
                .build()
        );
        when(geradorDeCartaoUtil.geraNumeroCartao()).thenReturn("1111222233334444");
        when(geradorDeCartaoUtil.geraCvv()).thenReturn("123");
        when(cartaoRepository.save(any(Cartao.class))).thenAnswer(inv -> inv.getArgument(0));

        var response = cartaoService.solicitaCartaoDebito(dadosSolicitacao);

        assertNotNull(response);
        assertEquals("1111222233334444", response.numeroCartao());

        verify(cartaoRepository).save(captor.capture());
        Cartao cartaoSalvo = captor.getValue();

        assertNotNull(cartaoSalvo);
        assertEquals("1111222233334444", cartaoSalvo.getNumeroCartao());
        assertEquals("123", cartaoSalvo.getCvv());
        assertEquals(StatusCartao.CARTAO_ATIVO, cartaoSalvo.getStatus());
        assertNotNull(cartaoSalvo.getDataVencimento());
        assertEquals(conta, cartaoSalvo.getConta());
    }

    @Test
    void solicitacaoCartaoDebitoFalhaQuandoContaNaoEncontrada() {
        var idConta = 1L;
        CartaoDebitoCriarDadosRequest dadosSolicitacao = new CartaoDebitoCriarDadosRequest(idConta);

        when(contaRepository.findById(idConta)).thenReturn(Optional.empty());

        assertThrows(RegraDeNegocioException.class, () -> {
            cartaoService.solicitaCartaoDebito(dadosSolicitacao);
        });

        verify(cartaoRepository, never()).save(any(Cartao.class));
    }
}