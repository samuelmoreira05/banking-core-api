package com.banco.api.banco.controller.usuario;

import com.banco.api.banco.controller.usuario.request.UsuarioAutenticacaoDadosRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/autenticar")
public class UsuarioAutenticacaoController {

    private final AuthenticationManager manager;

    public UsuarioAutenticacaoController(AuthenticationManager manager) {
        this.manager = manager;
    }

    @PostMapping("/login")
    public ResponseEntity loginUsuario(
            @RequestBody  UsuarioAutenticacaoDadosRequest dados){
        var token = new UsernamePasswordAuthenticationToken(dados.login(), dados.senha());
        var authentication = manager.authenticate(token);
        return ResponseEntity.ok().build();
    }
}
