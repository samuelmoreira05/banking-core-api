package com.banco.api.banco.controller.transacaoCartao;

import com.banco.api.banco.service.TransacaoCartaoService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/transacao-cartao")
@SecurityRequirement(name = "barer-token")
public class TransacaoCartaoController {

    private final TransacaoCartaoService transacaoCartaoService;

    public TransacaoCartaoController(TransacaoCartaoService transacaoCartaoService) {
        this.transacaoCartaoService = transacaoCartaoService;
    }


}
