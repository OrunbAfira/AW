package br.umc.hemovida.dto;

import java.time.LocalDate;
import java.util.List;

/** Resultado da verificação; também é o corpo do 422 (proximaDataApta nula quando o impedimento é definitivo). */
public record Aptidao(boolean apto, List<String> motivos, LocalDate proximaDataApta) {
}
