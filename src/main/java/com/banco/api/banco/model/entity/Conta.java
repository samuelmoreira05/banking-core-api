package com.banco.api.banco.model.entity;

import com.banco.api.banco.enums.StatusConta;
import com.banco.api.banco.enums.TipoConta;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;


@Entity
@Table(name = "Contas")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Conta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String numeroConta;

    @Enumerated(EnumType.STRING)
    private TipoConta tipoConta;

    private String agencia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    private BigDecimal saldo;

    @Enumerated(EnumType.STRING)
    private StatusConta status = StatusConta.ATIVO;

    @CreationTimestamp
    private LocalDate dataCriacao;

}
