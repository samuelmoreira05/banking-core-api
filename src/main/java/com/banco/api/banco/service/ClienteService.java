package com.banco.api.banco.service;

import com.banco.api.banco.controller.cliente.request.ClienteAtualizarDadosRequest;
import com.banco.api.banco.controller.cliente.request.ClienteCadastroDadosRequest;
import com.banco.api.banco.controller.cliente.response.ClienteMostrarDadosResponse;
import com.banco.api.banco.controller.cliente.response.ClienteListagemDadosResponse;
import com.banco.api.banco.enums.StatusCliente;
import com.banco.api.banco.enums.UserRole;
import com.banco.api.banco.infra.exception.RegraDeNegocioException;
import com.banco.api.banco.mapper.ClienteMapper;
import com.banco.api.banco.model.entity.Cliente;
import com.banco.api.banco.repository.ClienteRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
public class ClienteService {

    private final ClienteRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final ClienteMapper clienteMapper;

    public ClienteService(ClienteRepository repository,
                          PasswordEncoder passwordEncoder,
                          ClienteMapper clienteMapper) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.clienteMapper = clienteMapper;
    }

    @Transactional
    public ClienteMostrarDadosResponse cadastraCliente(ClienteCadastroDadosRequest dados) {
        validarDuplicidade(dados);

        Cliente cliente = clienteMapper.toEntity(dados, null);

        if (cliente.getIdade() < 18){
            throw new RegraDeNegocioException("Não é possivel abrir uma conta sendo menor de 18 anos");
        }

        var senhaHash = passwordEncoder.encode(dados.senha());

        cliente.getUsuario().setSenha(senhaHash);

        cliente.getUsuario().setRole(UserRole.USER);
        cliente.setStatus(StatusCliente.ATIVO);

        repository.save(cliente);
        return clienteMapper.toClienteResponse(cliente);
    }

    public Page<ClienteListagemDadosResponse> listaCliente(Pageable pageable){
        Page<Cliente> paginaDeClientes = repository.findAll(pageable);

        return paginaDeClientes.map(clienteMapper::toClienteListagemResponse);
    }

    @Transactional
    public ClienteMostrarDadosResponse atualizarCliente (Long id, ClienteAtualizarDadosRequest dados){
        Cliente cliente = buscarClientePorId(id);

        cliente.atualizaCliente(dados);
        return clienteMapper.toClienteResponse(cliente);
    }

    @Transactional
    public void bloquear (Long id) {
        alterarStatus(id, StatusCliente.BLOQUEADO, "O cliente ja se encontra bloqueado", Cliente::bloquear);
    }

    @Transactional
    public void inadimplencia(Long id) {
        alterarStatus(id, StatusCliente.INADIMPLENTE, "O cliente ja se encontra inadimplente", Cliente::inadimplencia);
    }

    @Transactional
    public void ativaCliente(Long id){
        alterarStatus(id, StatusCliente.ATIVO, "O cliente ja se encontra ativo", Cliente::ativar);
    }

    private void alterarStatus(Long id, StatusCliente statusImpeditivo, String mensagemErro, Consumer<Cliente> acao) {
        Cliente cliente = buscarClientePorId(id);

        if (cliente.getStatus() == statusImpeditivo){
            throw new RegraDeNegocioException(mensagemErro);
        }
        acao.accept(cliente);
    }

    public Cliente buscarClientePorId(Long id){
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente de ID" + id + "não encontrado"));
    }

    public void validarDuplicidade(ClienteCadastroDadosRequest dados) {
        if (repository.existsByCpf(dados.cpf()) || repository.existsByEmail(dados.email())){
            throw new RegraDeNegocioException("Esse CPF ou Email já existe na base de dados!");
        }
    }
}
