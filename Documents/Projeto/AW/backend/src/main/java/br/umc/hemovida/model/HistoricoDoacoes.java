package br.umc.hemovida.model;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class HistoricoDoacoes {

    private final List<Doacao> doacoes;

    public HistoricoDoacoes(List<Doacao> doacoes) {
        this.doacoes = List.copyOf(doacoes);
    }

    public boolean estaVazio() {
        return doacoes.isEmpty();
    }

    public Optional<Doacao> ultimaDoacao() {
        return doacoes.stream().max(Comparator.comparing(Doacao::getDataDoacao));
    }

    public int quantidadeNosUltimos12Meses(LocalDate data) {
        return doacoesNosUltimos12Meses(data).size();
    }

    /** Doações da janela de 12 meses que termina em {@code data}, da mais recente para a mais antiga. */
    public List<Doacao> doacoesNosUltimos12Meses(LocalDate data) {
        LocalDate inicio = data.minusMonths(12);
        return doacoes.stream()
                .filter(doacao -> doacao.getDataDoacao().isAfter(inicio) && !doacao.getDataDoacao().isAfter(data))
                .sorted(Comparator.comparing(Doacao::getDataDoacao).reversed())
                .toList();
    }
}
