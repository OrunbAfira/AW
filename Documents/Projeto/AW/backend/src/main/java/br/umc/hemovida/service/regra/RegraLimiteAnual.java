package br.umc.hemovida.service.regra;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import br.umc.hemovida.dto.DoacaoRequest;
import br.umc.hemovida.model.Doacao;
import br.umc.hemovida.model.Doador;
import br.umc.hemovida.service.politica.PoliticaDoacaoFactory;

@Component
public class RegraLimiteAnual implements RegraAptidao {

    @Override
    public Optional<Impedimento> verificar(Doador doador, DoacaoRequest pedido, LocalDate data) {
        int limite = PoliticaDoacaoFactory.para(doador.getSexo()).limiteDoacoesEm12Meses();
        List<Doacao> janela = doador.historico().doacoesNosUltimos12Meses(data);
        if (janela.size() < limite) {
            return Optional.empty();
        }
        // A vaga abre quando a doação de posição "limite" (da mais recente para a mais antiga) sai da janela.
        LocalDate liberadoEm = janela.get(limite - 1).getDataDoacao().plusMonths(12);
        return Optional.of(Impedimento.temporario(
                "Limite de " + limite + " doações em 12 meses atingido", liberadoEm));
    }
}
