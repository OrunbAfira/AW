package br.umc.hemovida.dto;

import java.time.LocalDate;

import br.umc.hemovida.model.Doacao;

public record DoacaoResponse(Long id, Long idDoador, LocalDate dataDoacao, String numeroBolsa,
        Integer volumeEmMililitros, String observacoes, String pressaoArterial, Double hemoglobina) {

    public static DoacaoResponse de(Doacao doacao) {
        return new DoacaoResponse(doacao.getId(), doacao.getDoador().getId(), doacao.getDataDoacao(),
                doacao.getNumeroBolsa(), doacao.getVolumeEmMililitros(), doacao.getObservacoes(),
                doacao.getPressaoArterial(), doacao.getHemoglobina());
    }
}
