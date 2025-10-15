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

    @Column(name = "agencia")
    private String agencia;

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
            throw new RegraDeNegocioException("A conta já está encerrada.");
        }
        if (saldo.compareTo(BigDecimal.ZERO) != 0) {
            throw new RegraDeNegocioException("A conta não pode ser encerrada com saldo diferente de zero.");
        }
        this.status = StatusConta.ENCERRADA;
    }

    public void suspendeConta(){
        if (this.status == StatusConta.SUSPENSA || this.status == StatusConta.ENCERRADA) {
            throw new RegraDeNegocioException("A conta não pode ser suspensa.");
        }
        this.status = StatusConta.SUSPENSA;
    }

    public void ativaConta(){
        if (this.status == StatusConta.ATIVO) {
            throw new RegraDeNegocioException("A conta já está ativa.");
        }
        if (this.status == StatusConta.ENCERRADA) {
            throw new RegraDeNegocioException("Não é possível reativar uma conta encerrada.");
        }
        this.status = StatusConta.ATIVO;
    }

    public void executarTransacao(TipoTransacao tipo, BigDecimal valor) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraDeNegocioException("O valor da transação deve ser positivo.");
        }

        if (tipo == TipoTransacao.DEPOSITO) {
            this.saldo = this.saldo.add(valor);
        } else if (tipo == TipoTransacao.SAQUE) {
            if (this.saldo.compareTo(valor) < 0) {
                throw new RegraDeNegocioException("Saldo insuficiente para realizar o saque.");
            }
            this.saldo = this.saldo.subtract(valor);
        } else {
            throw new RegraDeNegocioException("Tipo de transação não suportado.");
        }
    }
}