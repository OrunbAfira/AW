package br.umc.hemovida.service.regra;

import java.time.LocalDate;
import java.util.Optional;

import br.umc.hemovida.dto.DoacaoRequest;
import br.umc.hemovida.model.Doador;

public interface RegraAptidao {

    Optional<Impedimento> verificar(Doador doador, DoacaoRequest pedido, LocalDate data);
}
