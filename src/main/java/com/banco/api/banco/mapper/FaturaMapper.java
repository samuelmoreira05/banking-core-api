package com.banco.api.banco.mapper;

import com.banco.api.banco.enums.StatusFatura;
import com.banco.api.banco.model.entity.Cartao;
import com.banco.api.banco.model.entity.Fatura;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
@Slf4j
@AllArgsConstructor
public class FaturaMapper {

    public Fatura toEntityFaturaInicial(Cartao cartao){
        LocalDate dataVencimento = LocalDate.now()
                .plusMonths(1)
                .withDayOfMonth(cartao.getDiaVencimentoFatura());

        return Fatura.builder()
                .cartao(cartao)
                .valorTotal(BigDecimal.ZERO)
                .status(StatusFatura.ABERTA)
                .dataVencimento(dataVencimento)
                .build();
    }
}
