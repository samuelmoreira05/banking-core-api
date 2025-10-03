package com.banco.api.banco.controller.conta;

import com.banco.api.banco.controller.conta.request.ContaCadastroDadosRequest;
import com.banco.api.banco.controller.conta.response.ContaListagemDadosResponse;
import com.banco.api.banco.controller.conta.response.ContaMostrarDadosResponse;
import com.banco.api.banco.controller.documentation.ContaDocumentation;
import com.banco.api.banco.service.ContaService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/contas")
@SecurityRequirement(name = "barer-token")
public class ContaController implements ContaDocumentation {

    private final ContaService service;

    public ContaController(ContaService service) {
        this.service = service;
    }

    @PostMapping("/cadastro")
    public ResponseEntity<ContaMostrarDadosResponse> cadastrar(
            @Valid
            @RequestBody ContaCadastroDadosRequest dados){
        ContaMostrarDadosResponse response = service.criarConta(dados);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/listar")
    public ResponseEntity<Page<ContaListagemDadosResponse>> listar(
            Pageable pageable
    ){
        Page<ContaListagemDadosResponse> listagem = service.listarConta(pageable);
        return ResponseEntity.ok(listagem);
    }

    @DeleteMapping("/encerrar")
    public ResponseEntity<Void> encerrar(
            @PathVariable Long id
    ){
        service.encerraConta(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/suspender")
    public ResponseEntity<Void> suspender(
            @PathVariable Long id
    ){
        service.suspendeConta(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/ativar")
    public ResponseEntity<Void> ativar(
            @PathVariable Long id
    ){
        service.ativaConta(id);
        return ResponseEntity.ok().build();
    }
}
