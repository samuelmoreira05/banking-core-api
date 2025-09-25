package com.banco.api.banco.controller.conta;

import com.banco.api.banco.controller.conta.request.DadosCadastroContaRequest;
import com.banco.api.banco.controller.conta.response.DadosListagemContasResponse;
import com.banco.api.banco.controller.conta.response.DadosMostrarContaResponse;
import com.banco.api.banco.service.ContaService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public ResponseEntity<DadosMostrarContaResponse> cadastroConta(
            @Valid
            @RequestBody DadosCadastroContaRequest dados){
        DadosMostrarContaResponse response = service.criarConta(dados);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/listar")
    public ResponseEntity<Page<DadosListagemContasResponse>> listarConta(
            Pageable pageable
    ){
        Page<DadosListagemContasResponse> listagem = service.listarConta(pageable);
        return ResponseEntity.ok(listagem);
    }

    @DeleteMapping("/encerrar")
    public ResponseEntity<Void> encerrarConta(
            @PathVariable Long id
    ){
        service.encerraConta(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/suspender")
    public ResponseEntity<Void> suspendeConta(
            @PathVariable Long id
    ){
        service.suspendeConta(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/ativar")
    public ResponseEntity<Void> ativaConta(
            @PathVariable Long id
    ){
        service.ativaConta(id);
        return ResponseEntity.ok().build();
    }
}
