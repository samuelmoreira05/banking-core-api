package com.banco.api.banco.controller.usuario;

import com.banco.api.banco.controller.documentation.UsuarioDocumentation;
import com.banco.api.banco.controller.usuario.request.UsuarioAutenticacaoDadosRequest;
import com.banco.api.banco.controller.usuario.response.GeraTokenResponse;
import com.banco.api.banco.infra.security.TokenSecurity;
import com.banco.api.banco.model.entity.Usuario;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/autenticar")
public class UsuarioAutenticacaoController implements UsuarioDocumentation {

    private final AuthenticationManager manager;
    private final TokenSecurity security;

    public UsuarioAutenticacaoController(AuthenticationManager manager, TokenSecurity security) {
        this.manager = manager;
        this.security = security;
    }

    @Override // Boa prática
    @PostMapping("/login")
    public ResponseEntity<GeraTokenResponse> loginUsuario(
            @Valid
            @RequestBody UsuarioAutenticacaoDadosRequest dados){

        var token = new UsernamePasswordAuthenticationToken(dados.login(), dados.senha());
        var authentication = manager.authenticate(token);
        var jwtToken = security.geraToken((Usuario) authentication.getPrincipal());

        return ResponseEntity.ok(new GeraTokenResponse(jwtToken));
    }
}
