package br.umc.hemovida.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.umc.hemovida.dto.LoginRequest;
import br.umc.hemovida.dto.LoginResponse;
import br.umc.hemovida.service.AutenticacaoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
public class AutenticacaoController {

    private final AutenticacaoService autenticacaoService;

    public AutenticacaoController(AutenticacaoService autenticacaoService) {
        this.autenticacaoService = autenticacaoService;
    }

    @PostMapping("/login")
    public LoginResponse entrar(@Valid @RequestBody LoginRequest pedido) {
        return autenticacaoService.entrar(pedido);
    }
}
