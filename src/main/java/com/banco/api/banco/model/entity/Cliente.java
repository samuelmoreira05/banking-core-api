package com.banco.api.banco.model.entity;

import com.banco.api.banco.enums.StatusCliente;
import com.banco.api.banco.model.dto.request.DadosAtualizarCliente;
import com.banco.api.banco.model.dto.request.DadosCadastroRequest;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import java.time.LocalDate;

@Entity
@Table(name = "Clientes")
@Data
@NoArgsConstructor
@AllArgsConstructor
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
    private LocalDate dataCadastro;

    @Email(message = "O email deve ter um formato valido!")
    private String email;

    @Enumerated(EnumType.STRING)
    private StatusCliente status = StatusCliente.ATIVO;

    private String telefone;
    private String endereco;

    private LocalDate dataDesativacao;
    private LocalDate dataAtivacao;

    public Cliente(DadosCadastroRequest dados) {
        this.nome = dados.nome();
        this.email = dados.email();
        this.cpf = dados.cpf();
        this.dataNascimento = dados.dataNascimento();
        this.endereco = dados.endereco();
        this.telefone = dados.telefone();
        this.status = StatusCliente.ATIVO;
    }

    public void desativar() {
        this.status = StatusCliente.INATIVO;
        this.dataDesativacao = LocalDate.now();
    }

    public void isAtivo() {
        this.status = StatusCliente.ATIVO;
        this.dataAtivacao = LocalDate.now();
    }

    public int getIdade() {
       return LocalDate.now().getYear() - dataNascimento.getYear();
    }

    public void atualizarCliente(DadosAtualizarCliente dados) {
        if (dados.nome() != null) {
            this.nome = dados.nome();
        }
        if (dados.endereco() != null) {
            this.endereco = dados.endereco();
        }
        if (dados.email() != null) {
            this.email = dados.email();
        }
        if (dados.telefone() != null) {
            this.telefone = dados.telefone();
        }
    }
}
