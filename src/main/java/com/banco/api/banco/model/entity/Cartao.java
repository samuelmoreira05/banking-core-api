package com.banco.api.banco.model.entity;

import com.banco.api.banco.enums.StatusCartao;
import com.banco.api.banco.enums.TipoCartao;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity(name = "Cartao")
@Table(name = "cartoes")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Cartao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 16)
    private String numeroCartao;

    @Column(nullable = false, length = 4)
    private String cvv;

    @Column(nullable = false)
    private LocalDate dataVencimento;

    @Enumerated(EnumType.STRING)
    private TipoCartao tipoCartao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conta_id", referencedColumnName = "id", nullable = false)
    private Conta conta;

    @Enumerated(EnumType.STRING)
    private StatusCartao status;

    private int diaVencimentoFatura;

    @Column(precision = 10, scale = 2)
    private BigDecimal limiteCredito;
}
