package br.umc.hemovida.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "Usuário ou senha inválidos") String usuario,
        @NotBlank(message = "Usuário ou senha inválidos") String senha) {
}
