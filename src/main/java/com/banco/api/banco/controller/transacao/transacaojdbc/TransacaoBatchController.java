package com.banco.api.banco.controller.transacao.transacaojdbc;

import com.banco.api.banco.service.batchTesteJDBC.TransacaoBatchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/teste-batch")
public class TransacaoBatchController {

    private final TransacaoBatchService transacaoBatchService;

    public TransacaoBatchController(TransacaoBatchService transacaoBatchService) {
        this.transacaoBatchService = transacaoBatchService;
    }

    @PostMapping("/{idConta}/{quantidade}")
    public ResponseEntity<String> gerarCarga(
            @PathVariable Long idConta,
            @PathVariable int quantidade){
        transacaoBatchService.criarCargaTeste(idConta, quantidade);
        return ResponseEntity.ok("Carga de " + quantidade + "transacao finalizada");
    }
}
