package com.banco.api.banco.service;

import com.banco.api.banco.model.entity.Usuario;
import com.banco.api.banco.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioAutenticacaoServiceTest {

    @Mock private UsuarioRepository usuarioRepository;
    @InjectMocks private UsuarioAutenticacaoService usuarioAutenticacaoService;

    @Test
    void carregandoNomeDeUsuarioQuandoUsuarioExiste() {
        var login = "login_teste";

        Usuario usuario = new Usuario();
        usuario.setLogin(login);

        when(usuarioRepository.findByLogin(login)).thenReturn(Optional.of(usuario));

        UserDetails userDetails = usuarioAutenticacaoService.loadUserByUsername(login);

        assertNotNull(userDetails);
        assertEquals(login, userDetails.getUsername());
    }

    @Test
    void naoEncontraNomeDoUsuarioLancaExcecao() {
        var login = "login_teste";

        Usuario usuario = new Usuario();
        usuario.setLogin(login);

        when(usuarioRepository.findByLogin(login)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            usuarioAutenticacaoService.loadUserByUsername(login);
        });
    }
}