package com.banco.api.banco.service;

import com.banco.api.banco.controller.cliente.request.ClienteAtualizarDadosRequest;
import com.banco.api.banco.controller.cliente.request.ClienteCadastroDadosRequest;
import com.banco.api.banco.controller.cliente.response.ClienteMostrarDadosResponse;
import com.banco.api.banco.controller.cliente.response.ClienteListagemDadosResponse;
import com.banco.api.banco.enums.StatusCliente;
import com.banco.api.banco.enums.UserRole;
import com.banco.api.banco.infra.exception.RegraDeNegocioException;
import com.banco.api.banco.model.entity.Cliente;
import com.banco.api.banco.model.entity.Usuario;
import com.banco.api.banco.repository.ClienteRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ClienteService {

    private final ClienteRepository repository;
    private final PasswordEncoder passwordEncoder;

    public ClienteService(ClienteRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public ClienteMostrarDadosResponse cadastraCliente(ClienteCadastroDadosRequest dados) {
        if (repository.existsByCpf(dados.cpf())){
            throw new RegraDeNegocioException("Esse CPF já existe na base de dados!");
        }

        var senhaHash = passwordEncoder.encode(dados.senha());

        Usuario usuario = Usuario.builder()
                .login(dados.login())
                .senha(senhaHash)
                .role(UserRole.USER)
                .build();

        Cliente cliente = Cliente.builder()
                .nome(dados.nome())
                .cpf(dados.cpf())
                .email(dados.email())
                .dataNascimento(dados.dataNascimento())
                .endereco(dados.endereco())
                .telefone(dados.telefone())
                .status(StatusCliente.ATIVO)
                .usuario(usuario)
                .build();
        repository.save(cliente);
        return new ClienteMostrarDadosResponse(cliente);
    }

    public Page<ClienteListagemDadosResponse> listaCliente(Pageable pageable){
        return repository.findAll(pageable).map(ClienteListagemDadosResponse::new);
    }

    @Transactional
    public ClienteMostrarDadosResponse atualizarCliente (Long id, ClienteAtualizarDadosRequest dados){
        Cliente cliente = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado na base de dados!"));

        cliente.atualizaCliente(dados);
        return new ClienteMostrarDadosResponse(cliente);
    }

    @Transactional
    public void bloquear (Long id) {
        Cliente cliente = buscarClientePorId(id);
        cliente.bloquear();
    }

    @Transactional
    public void inadimplencia(Long id) {
        Cliente cliente = buscarClientePorId(id);
        cliente.inadimplencia();
    }

    protected Cliente buscarClientePorId(Long id){
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente de ID" + id + "não encontrado"));
    }
}
