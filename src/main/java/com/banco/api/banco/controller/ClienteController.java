package com.banco.api.banco.controller;

import com.banco.api.banco.model.dto.request.DadosAtualizarCliente;
import com.banco.api.banco.model.dto.request.DadosCadastroRequest;
import com.banco.api.banco.model.dto.response.DadosCadastroResponse;
import com.banco.api.banco.model.dto.response.DadosDetalhamentoCliente;
import com.banco.api.banco.model.dto.response.DadosListagemClientes;
import com.banco.api.banco.model.entity.Cliente;
import com.banco.api.banco.service.ClienteService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("api/clientes")
public class ClienteController {

    private final ClienteService service;

    public ClienteController(ClienteService service) {
        this.service = service;}

    @PostMapping("/cadastrar")
    @Transactional
    public ResponseEntity<DadosCadastroResponse> cadastroCliente(
            @Valid
            @RequestBody DadosCadastroRequest dados) {
        DadosCadastroResponse response = service.cadastraCliente(dados);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/listar")
    public ResponseEntity<Page<DadosListagemClientes>> listarCliente(
            Pageable pageable
    ){
        Page<DadosListagemClientes> listagem = service.listaCliente(pageable);
        return ResponseEntity.ok(listagem);
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity atualizarCliente(
            @Valid
            @PathVariable Long id,
            @RequestBody DadosAtualizarCliente dados) {
        Cliente cliente = service.atualizarCliente(id, dados);
        return ResponseEntity.ok(new DadosDetalhamentoCliente(cliente));
    }

    @DeleteMapping("/desativar/{id}")
    @Transactional
    public ResponseEntity<Void> desativarCliente(
            @PathVariable Long id
    ){
        service.desativarCliente(id);
        return ResponseEntity.noContent().build();
    }
}
