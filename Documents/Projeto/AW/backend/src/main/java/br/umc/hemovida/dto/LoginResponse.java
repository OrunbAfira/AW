package br.umc.hemovida.dto;

import java.time.Instant;

public record LoginResponse(String token, String perfil, Instant expiraEm) {
}
