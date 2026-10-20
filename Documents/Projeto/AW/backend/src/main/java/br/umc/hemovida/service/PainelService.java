package br.umc.hemovida.service;

import java.time.Clock;
import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.umc.hemovida.dto.PainelResponse;
import br.umc.hemovida.repository.DoacaoRepository;
import br.umc.hemovida.repository.DoadorRepository;

@Service
public class PainelService {

    private final DoadorRepository doadorRepository;
    private final DoacaoRepository doacaoRepository;
    private final Clock relogio;

    public PainelService(DoadorRepository doadorRepository, DoacaoRepository doacaoRepository, Clock relogio) {
        this.doadorRepository = doadorRepository;
        this.doacaoRepository = doacaoRepository;
        this.relogio = relogio;
    }

    @Transactional(readOnly = true)
    public PainelResponse montar() {
        LocalDate hoje = LocalDate.now(relogio);
        return new PainelResponse(
                doadorRepository.count(),
                doadorRepository.contarAptosEm(hoje),
                doacaoRepository.countByDataDoacaoBetween(hoje.withDayOfMonth(1), hoje.withDayOfMonth(hoje.lengthOfMonth())));
    }
}
