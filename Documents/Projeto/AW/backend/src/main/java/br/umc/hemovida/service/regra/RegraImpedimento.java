package br.umc.hemovida.service.regra;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.stereotype.Component;

import br.umc.hemovida.dto.DoacaoRequest;
import br.umc.hemovida.model.Doador;

@Component
public class RegraImpedimento implements RegraAptidao {

    @Override
    public Optional<Impedimento> verificar(Doador doador, DoacaoRequest pedido, LocalDate data) {
        if (doador.getFichaClinica() == null || !doador.getFichaClinica().isImpedimentoDefinitivo()) {
            return Optional.empty();
        }
        return Optional.of(Impedimento.definitivo("Doador com impedimento definitivo registrado na ficha clínica"));
    }
}
