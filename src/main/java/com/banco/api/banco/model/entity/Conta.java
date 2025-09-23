package com.banco.api.banco.model.entity;

import com.banco.api.banco.controller.conta.request.DadosCadastroContaRequest;
import com.banco.api.banco.enums.StatusConta;
import com.banco.api.banco.enums.TipoConta;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;


@Entity(name = "Conta")
@Table(name = "contas")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Conta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_conta", nullable = false, unique = true)
    private String numeroConta;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_conta", nullable = false)
    private TipoConta tipoConta;

    @Column(name = "agencia")
    private String agencia = "121";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @Builder.Default
    private BigDecimal saldo = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    private StatusConta status = StatusConta.ATIVO;

    @CreationTimestamp
    private LocalDate dataCriacao;

    public Conta(DadosCadastroContaRequest dados) {
        this.tipoConta = dados.tipo();
    }

    public Conta(DadosCadastroContaRequest dados, Cliente cliente) {
        if (dados.tipo() == null) {
            throw new IllegalArgumentException("Tipo de conta é obrigatorio");
        }
        this.tipoConta = dados.tipo();
        this.cliente = cliente;
        this.numeroConta = gerarNumero();
        this.saldo = BigDecimal.ZERO;
        this.status = StatusConta.ATIVO;
        this.agencia = "121";
    }

    private String gerarNumero() {
        return cliente.getId() + "-" + System.currentTimeMillis();
    }
}
