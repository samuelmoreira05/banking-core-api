package com.banco.api.banco.scheduler;

import com.banco.api.banco.client.BatchClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class FaturaAgendadorScheduler {

    private final BatchClient batchClient;

    public FaturaAgendadorScheduler(BatchClient batchClient) {
        this.batchClient = batchClient;
    }

    @Scheduled(cron = "{app.scheduler.faturas-cron}", zone = "America/Sao_Paulo")
    public void agendarFechamentoFaturas() {
        System.out.println("Iniciando scheduler: Solicitando fechamento de fatura");

        LocalDate dataReferencia = LocalDate.now().minusMonths(1);

        int mes = dataReferencia.getMonthValue();
        int ano = dataReferencia.getYear();

        System.out.println("Referencia: Mes " + mes + "/" + ano);

        batchClient.solicitaGeracaoFatura(mes, ano);
    }
}
