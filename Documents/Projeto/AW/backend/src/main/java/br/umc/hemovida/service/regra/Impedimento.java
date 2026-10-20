package br.umc.hemovida.service.regra;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/** {@code liberadoEm} é nulo quando não há data prevista (impedimento definitivo ou sem prazo conhecido). */
public record Impedimento(String motivo, LocalDate liberadoEm, boolean definitivo) {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static Impedimento temporario(String motivo, LocalDate liberadoEm) {
        return new Impedimento(motivo, liberadoEm, false);
    }

    public static Impedimento definitivo(String motivo) {
        return new Impedimento(motivo, null, true);
    }

    public static String formatar(LocalDate data) {
        return FORMATO.format(data);
    }
}
