package com.banco.api.banco.service.BatchTesteJDBC;

import com.banco.api.banco.enums.TipoTransacao;
import com.banco.api.banco.model.entity.Conta;
import com.banco.api.banco.model.entity.Transacao;
import com.banco.api.banco.repository.ContaRepository;
import com.banco.api.banco.repository.jdbc.TransacaoJdbcRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class TransacaoBatchService {

    private final TransacaoJdbcRepository transacaoJdbcRepository;
    private final ContaRepository contaRepository;

    public TransacaoBatchService(TransacaoJdbcRepository transacaoJdbcRepository, ContaRepository contaRepository) {
        this.transacaoJdbcRepository = transacaoJdbcRepository;
        this.contaRepository = contaRepository;
    }

    public void criarCargaTeste(Long idConta, int quanridadeRegistros){
        Conta conta = contaRepository.findById(idConta)
                .orElseThrow(() -> new EntityNotFoundException("Conta nao encontrada na base de dados"));

        List<Transacao> loteTransacaoTeste = new ArrayList<>();
        Random random = new Random();

        System.out.println("Gerando " + quanridadeRegistros + "objetos na memoria");

        for (int i = 0; i < quanridadeRegistros; i++){
            Transacao transacao = Transacao.builder()
                    .conta(conta)
                    .tipo(TipoTransacao.DEPOSITO)
                    .valor(new BigDecimal(random.nextInt(1000)))
                    .saldoAnterior(BigDecimal.ZERO)
                    .descricao("Teste" + i)
                    .dataTransacao(LocalDateTime.now())
                    .fatura(null)
                    .build();

            loteTransacaoTeste.add(transacao);
        }
        System.out.println("Iniciando iserção no banco via JDBC");
        long inicio = System.currentTimeMillis();

        transacaoJdbcRepository.saveAll(loteTransacaoTeste);

        long fim = System.currentTimeMillis();
        System.out.println("Tempo total para inserir " + quanridadeRegistros + " registros: " + (fim - inicio) + "ms");
    }
}
