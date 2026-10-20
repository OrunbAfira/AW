package br.umc.hemovida.service.regra;

import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;

import org.springframework.stereotype.Component;

import br.umc.hemovida.dto.DoacaoRequest;
import br.umc.hemovida.model.Doador;

@Component
public class RegraPrimeiraDoacao implements RegraAptidao {

    static final int IDADE_MAXIMA_PRIMEIRA_DOACAO = 60;

    @Override
    public Optional<Impedimento> verificar(Doador doador, DoacaoRequest pedido, LocalDate data) {
        if (!doador.historico().estaVazio()) {
            return Optional.empty();
        }
        int idade = Period.between(doador.getDataNascimento(), data).getYears();
        if (idade <= IDADE_MAXIMA_PRIMEIRA_DOACAO) {
            return Optional.empty();
        }
        return Optional.of(Impedimento.definitivo(
                "Primeira doação só é permitida até " + IDADE_MAXIMA_PRIMEIRA_DOACAO + " anos"));
    }
}
