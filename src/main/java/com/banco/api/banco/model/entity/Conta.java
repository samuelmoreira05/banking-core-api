package com.banco.api.banco.model.entity;

import com.banco.api.banco.enums.StatusConta;
import com.banco.api.banco.enums.TipoConta;
import com.banco.api.banco.enums.TipoTransacao;
import com.banco.api.banco.infra.exception.RegraDeNegocioException;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;


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

    @Builder.Default
    @Column(name = "agencia")
    private String agencia = "121";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @Builder.Default
    @Column(nullable = false)
    private BigDecimal saldo = BigDecimal.ZERO;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private StatusConta status = StatusConta.ATIVO;

    @CreationTimestamp
    private LocalDateTime dataCriacao;

    public void encerraConta(){
        if (this.status == StatusConta.ENCERRADA) {
            throw new IllegalStateException("A conta já está encerrada.");
        }
        if (saldo.compareTo(BigDecimal.ZERO) != 0) {
            throw new IllegalStateException("A conta não pode ser encerrada com saldo diferente de zero.");
        }
        this.status = StatusConta.ENCERRADA;
    }

    public void suspendeConta(){
        if (this.status == StatusConta.SUSPENSA || this.status == StatusConta.ENCERRADA) {
            throw new IllegalStateException("A conta não pode ser suspensa.");
        }
        this.status = StatusConta.SUSPENSA;
    }

    public void ativaConta(){
        if (this.status == StatusConta.ATIVO) {
            throw new IllegalStateException("A conta já está ativa.");
        }
        this.status = StatusConta.ATIVO;
    }

    public void sacar(BigDecimal valor){
        if (this.status != StatusConta.ATIVO){
            throw new IllegalStateException("A conta precisa estar ATIVA para realizar saques.");
        }
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor do saque deve ser positivo.");
        }
        if (this.saldo.compareTo(valor) < 0){
            throw new IllegalStateException("Saldo insuficiente.");
        }
        this.saldo = this.saldo.subtract(valor);
    }

    public void depositar(BigDecimal valor) {
        if (this.status != StatusConta.ATIVO){
            throw new IllegalStateException("A conta precisa estar ATIVA para receber depósitos.");
        }
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor do depósito deve ser positivo.");
        }
        this.saldo = this.saldo.add(valor);
    }

    @PrePersist
    public void antesDeSalvar() {
        if (this.numeroConta == null) {
            this.numeroConta = gerarNumero();
        }
    }

    public void executarTransacao(TipoTransacao tipo, BigDecimal valor) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraDeNegocioException("O valor da transação deve ser positivo.");
        }

        if (tipo == TipoTransacao.DEPOSITO) {
            this.saldo = this.saldo.add(valor);
        } else if (tipo == TipoTransacao.SAQUE) {
            if (this.saldo.compareTo(valor) < 0) {
                throw new IllegalStateException("Saldo insuficiente para realizar o saque.");
            }
            this.saldo = this.saldo.subtract(valor);
        } else {
            throw new IllegalArgumentException("Tipo de transação não suportado.");
        }
    }

    private String gerarNumero() {
        return "ACC-" + UUID.randomUUID().toString().toUpperCase().substring(0, 8);
    }
}