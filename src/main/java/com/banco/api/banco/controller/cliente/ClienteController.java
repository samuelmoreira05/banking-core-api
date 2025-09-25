package com.banco.api.banco.controller.cliente;

import com.banco.api.banco.controller.cliente.request.DadosAtualizarClienteRequest;
import com.banco.api.banco.controller.cliente.request.DadosCadastroClienteRequest;
import com.banco.api.banco.controller.cliente.response.DadosMostrarClienteResponse;
import com.banco.api.banco.controller.cliente.response.DadosDetalhamentoClienteResponse;
import com.banco.api.banco.controller.cliente.response.DadosListagemClienteResponse;
import com.banco.api.banco.model.entity.Cliente;
import com.banco.api.banco.service.ClienteService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/clientes")
public class ClienteController {

    private final ClienteService service;

    public ClienteController(ClienteService service) {
        this.service = service;}

    @PostMapping("/cadastrar")
    public ResponseEntity<DadosMostrarClienteResponse> cadastroCliente(
            @Valid
            @RequestBody DadosCadastroClienteRequest dados) {
        DadosMostrarClienteResponse response = service.cadastraCliente(dados);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/listar")
    public ResponseEntity<Page<DadosListagemClienteResponse>> listarCliente(
            Pageable pageable
    ){
        Page<DadosListagemClienteResponse> listagem = service.listaCliente(pageable);
        return ResponseEntity.ok(listagem);
    }

    @PutMapping("/{id}")
    public ResponseEntity atualizarCliente(
            @Valid
            @PathVariable Long id,
            @RequestBody DadosAtualizarClienteRequest dados) {
        Cliente cliente = service.atualizarCliente(id, dados);
        return ResponseEntity.ok(new DadosDetalhamentoClienteResponse(cliente));
    }

    @DeleteMapping("/desativar/{id}")
    public ResponseEntity<Void> desativarCliente(
            @PathVariable Long id
    ){
        service.desativarCliente(id);
        return ResponseEntity.noContent().build();
    }
}
