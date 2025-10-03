package com.banco.api.banco.controller.cliente;

import com.banco.api.banco.controller.cliente.request.ClienteAtualizarDadosRequest;
import com.banco.api.banco.controller.cliente.request.ClienteCadastroDadosRequest;
import com.banco.api.banco.controller.cliente.response.ClienteMostrarDadosResponse;
import com.banco.api.banco.controller.cliente.response.ClienteDetalhamentoDadosResponse;
import com.banco.api.banco.controller.cliente.response.ClienteListagemDadosResponse;
import com.banco.api.banco.controller.documentation.ClienteDocumentation;
import com.banco.api.banco.model.entity.Cliente;
import com.banco.api.banco.service.ClienteService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/clientes")
public class ClienteController implements ClienteDocumentation {

    private final ClienteService service;

    public ClienteController(ClienteService service) {
        this.service = service;}

    @PostMapping("/cadastrar")
    public ResponseEntity<ClienteMostrarDadosResponse> cadastrar(
            @Valid
            @RequestBody ClienteCadastroDadosRequest dados) {
        ClienteMostrarDadosResponse response = service.cadastraCliente(dados);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/listar")
    @SecurityRequirement(name = "barer-token")
    public ResponseEntity<Page<ClienteListagemDadosResponse>> listar(
            Pageable pageable
    ){
        Page<ClienteListagemDadosResponse> listagem = service.listaCliente(pageable);
        return ResponseEntity.ok(listagem);
    }

    @PutMapping("/{id}")
    @SecurityRequirement(name = "barer-token")
    public ResponseEntity atualizar(
            @Valid
            @PathVariable Long id,
            @RequestBody ClienteAtualizarDadosRequest dados) {
        Cliente cliente = service.atualizarCliente(id, dados);
        return ResponseEntity.ok(new ClienteDetalhamentoDadosResponse(cliente));
    }

    @DeleteMapping("/bloquear/{id}")
    @SecurityRequirement(name = "barer-token")
    public ResponseEntity<Void> bloquear(
            @PathVariable Long id
    ){
        service.bloquear(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/inadimplencia/{id}")
    @SecurityRequirement(name = "barer-token")
    public ResponseEntity inadimplencia(
            @PathVariable Long id
    ){
        service.inadimplencia(id);
        return ResponseEntity.noContent().build();
    }
}
