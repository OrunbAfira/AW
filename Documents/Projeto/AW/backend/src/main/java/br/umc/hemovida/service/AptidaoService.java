package br.umc.hemovida.service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;

import br.umc.hemovida.dto.Aptidao;
import br.umc.hemovida.dto.DoacaoRequest;
import br.umc.hemovida.model.Doador;
import br.umc.hemovida.service.regra.Impedimento;
import br.umc.hemovida.service.regra.RegraAptidao;

@Service
public class AptidaoService {

    private final List<RegraAptidao> regras;
    private final Clock relogio;

    public AptidaoService(List<RegraAptidao> regras, Clock relogio) {
        this.regras = regras;
        this.relogio = relogio;
    }

    public Aptidao verificar(Doador doador, DoacaoRequest pedido) {
        LocalDate hoje = LocalDate.now(relogio);
        List<Impedimento> impedimentos = regras.stream()
                .map(regra -> regra.verificar(doador, pedido, hoje))
                .flatMap(Optional::stream)
                .toList();
        List<String> motivos = impedimentos.stream().map(Impedimento::motivo).toList();
        boolean definitivo = impedimentos.stream().anyMatch(Impedimento::definitivo);
        Optional<LocalDate> proximaDataApta = definitivo ? Optional.empty() : impedimentos.stream()
                .map(Impedimento::liberadoEm)
                .filter(Objects::nonNull)
                .max(Comparator.naturalOrder());
        return new Aptidao(impedimentos.isEmpty(), motivos, proximaDataApta.orElse(null));
    }
}
