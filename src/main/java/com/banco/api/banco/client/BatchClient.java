package com.banco.api.banco.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class BatchClient {

    private final RestClient restClient;

    public BatchClient(@Value("${app.batch.url}") String batchUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(batchUrl)
                .build();
    }

    public void solicitaGeracaoFatura(int mes, int ano) {
        String requestId = UUID.randomUUID().toString();

        System.out.println("Enviando solicitacao para o Batch, RequestID: " + requestId);

        var payload = new RequisicaoFaturaDTO(
                ano,
                mes,
                requestId,
                LocalDateTime.now()
        );

        try {
            String resposta = restClient.post()
                    .uri("/v1/faturas/gerar")
                    .body(payload)
                    .retrieve()
                    .body(String.class);

            System.out.println("Resposta do Batch: " + resposta);
        } catch (Exception e) {
            System.err.println("Erro ao chamr o Batch: " + e.getMessage());
        }
    }
    public record RequisicaoFaturaDTO(int ano, int mes, String requestId, LocalDateTime dataExecucao){}
}
