package com.banco.api.banco.controller.cartao;

import com.banco.api.banco.controller.cartao.request.CartaoDebitoCriarDadosRequest;
import com.banco.api.banco.controller.cartao.response.CartaoDebitoMostrarDadosResponse;
import com.banco.api.banco.service.CartaoService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/cartoes")
public class CartaoController {

    private final CartaoService cartaoService;

    public CartaoController(CartaoService cartaoService) {
        this.cartaoService = cartaoService;
    }

    @PostMapping("/solicita/debito")
    @SecurityRequirement(name = "barer-token")
    public ResponseEntity<CartaoDebitoMostrarDadosResponse> solicitaCartaoDebito(
            @Valid
            @RequestBody CartaoDebitoCriarDadosRequest dados){
        CartaoDebitoMostrarDadosResponse response = cartaoService.solicitaCartaoDebito(dados);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
