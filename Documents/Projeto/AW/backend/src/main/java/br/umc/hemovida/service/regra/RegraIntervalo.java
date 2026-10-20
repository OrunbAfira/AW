package br.umc.hemovida.service.regra;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.stereotype.Component;

import br.umc.hemovida.dto.DoacaoRequest;
import br.umc.hemovida.model.Doacao;
import br.umc.hemovida.model.Doador;
import br.umc.hemovida.service.politica.PoliticaDoacaoFactory;

@Component
public class RegraIntervalo implements RegraAptidao {

    @Override
    public Optional<Impedimento> verificar(Doador doador, DoacaoRequest pedido, LocalDate data) {
        Optional<Doacao> ultima = doador.historico().ultimaDoacao();
        if (ultima.isEmpty()) {
            return Optional.empty();
        }
        int intervalo = PoliticaDoacaoFactory.para(doador.getSexo()).intervaloMinimoEmDias();
        LocalDate liberadoEm = ultima.get().getDataDoacao().plusDays(intervalo);
        if (!liberadoEm.isAfter(data)) {
            return Optional.empty();
        }
        return Optional.of(Impedimento.temporario("Intervalo mínimo de " + intervalo
                + " dias desde a última doação (" + Impedimento.formatar(ultima.get().getDataDoacao()) + ")",
                liberadoEm));
    }
}
