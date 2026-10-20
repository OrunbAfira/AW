package br.umc.hemovida.service.regra;

import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;

import org.springframework.stereotype.Component;

import br.umc.hemovida.dto.DoacaoRequest;
import br.umc.hemovida.model.Doador;

@Component
public class RegraIdade implements RegraAptidao {

    static final int IDADE_MINIMA = 16;
    static final int IDADE_MAXIMA = 69;

    @Override
    public Optional<Impedimento> verificar(Doador doador, DoacaoRequest pedido, LocalDate data) {
        int idade = Period.between(doador.getDataNascimento(), data).getYears();
        if (idade < IDADE_MINIMA) {
            return Optional.of(Impedimento.temporario("Idade mínima para doar é " + IDADE_MINIMA + " anos",
                    doador.getDataNascimento().plusYears(IDADE_MINIMA)));
        }
        if (idade > IDADE_MAXIMA) {
            return Optional.of(Impedimento.definitivo("Idade máxima para doar é " + IDADE_MAXIMA + " anos"));
        }
        return Optional.empty();
    }
}
