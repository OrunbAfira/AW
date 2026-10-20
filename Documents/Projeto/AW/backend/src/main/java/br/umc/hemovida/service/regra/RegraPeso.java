package br.umc.hemovida.service.regra;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.stereotype.Component;

import br.umc.hemovida.dto.DoacaoRequest;
import br.umc.hemovida.model.Doador;

@Component
public class RegraPeso implements RegraAptidao {

    static final double PESO_MINIMO_EM_QUILOS = 50;

    @Override
    public Optional<Impedimento> verificar(Doador doador, DoacaoRequest pedido, LocalDate data) {
        if (pedido.pesoEmQuilos() == null) {
            return Optional.of(Impedimento.temporario("Peso não informado", null));
        }
        if (pedido.pesoEmQuilos() >= PESO_MINIMO_EM_QUILOS) {
            return Optional.empty();
        }
        return Optional.of(Impedimento.temporario("Peso mínimo para doar é 50 kg", null));
    }
}
