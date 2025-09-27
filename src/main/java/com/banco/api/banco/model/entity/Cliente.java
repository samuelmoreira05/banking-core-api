package com.banco.api.banco.model.entity;

import com.banco.api.banco.enums.StatusCliente;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

@Entity(name = "Cliente")
@Table(name = "clientes")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "O nome é obrigatorio")
    private String nome;

    @Column(unique = true, nullable = false, length = 11)
    @NotBlank(message = "O CPF é obrigatorio")
    private String cpf;

    @Column(nullable = false)
    @NotNull(message = "A data de nascimento é obrigatoria")
    private LocalDate dataNascimento;

    @CreationTimestamp
    private LocalDateTime dataCadastro;

    @Email(message = "O email deve ter um formato valido!")
    private String email;

    @Enumerated(EnumType.STRING)
    private StatusCliente status = StatusCliente.ATIVO;

    private String telefone;
    private String endereco;

    private LocalDate dataDesativacao;
    private LocalDate dataAtivacao;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "usuario_id", referencedColumnName = "id")
    private Usuario usuario;

    public void bloquear() {
        this.status = StatusCliente.BLOQUEADO;
        this.dataDesativacao = LocalDate.now();
    }

    public void ativar() {
        this.status = StatusCliente.ATIVO;
        this.dataAtivacao = LocalDate.now();
    }

    public int getIdade() {
        if (this.dataNascimento == null) {
            return 0;
        }
        return Period.between(this.dataNascimento, LocalDate.now()).getYears();
    }
}
