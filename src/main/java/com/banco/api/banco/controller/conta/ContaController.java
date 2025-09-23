package com.banco.api.banco.controller.conta;

import com.banco.api.banco.controller.conta.request.DadosCadastroConta;
import com.banco.api.banco.controller.conta.response.DadosMostrarConta;
import com.banco.api.banco.repository.ContaRepository;
import com.banco.api.banco.service.ContaService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/contas")
public class ContaController {

    private final ContaService service;

    public ContaController(ContaService service) {
        this.service = service;
    }

    @PostMapping("/cadastro")
    @Transactional
    public ResponseEntity<DadosMostrarConta> cadastroConta(
            @Valid
            @RequestBody DadosCadastroConta dados){
        DadosMostrarConta response = service.criarConta(dados);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
