package com.banco.api.banco.infra.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    private final TokenSecurity security;

    public SecurityFilter(TokenSecurity security) {
        this.security = security;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var jwtToken = verificarToken(request);
        var subject = security.getSubject(jwtToken);


        filterChain.doFilter(request, response);
    }

    private String verificarToken(HttpServletRequest request) {
        var authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null) {
            throw new RuntimeException("O token não foi enviado");
        }
        return authorizationHeader.replace("Bearer", "");
    }


}
