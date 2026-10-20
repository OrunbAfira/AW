package br.umc.hemovida.service.regra;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.stereotype.Component;

import br.umc.hemovida.dto.DoacaoRequest;
import br.umc.hemovida.model.Doador;

@Component
public class RegraTatuagem implements RegraAptidao {

    static final int MESES_DE_ESPERA = 12;

    @Override
    public Optional<Impedimento> verificar(Doador doador, DoacaoRequest pedido, LocalDate data) {
        if (doador.getFichaClinica() == null || doador.getFichaClinica().getDataUltimaTatuagem() == null) {
            return Optional.empty();
        }
        LocalDate liberadoEm = doador.getFichaClinica().getDataUltimaTatuagem().plusMonths(MESES_DE_ESPERA);
        if (!liberadoEm.isAfter(data)) {
            return Optional.empty();
        }
        return Optional.of(Impedimento.temporario(
                "Tatuagem há menos de " + MESES_DE_ESPERA + " meses (liberado em " + Impedimento.formatar(liberadoEm) + ")",
                liberadoEm));
    }
}
