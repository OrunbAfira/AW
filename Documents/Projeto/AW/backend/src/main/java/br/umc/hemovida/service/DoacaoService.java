package br.umc.hemovida.service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.umc.hemovida.dto.Aptidao;
import br.umc.hemovida.dto.DoacaoRequest;
import br.umc.hemovida.dto.DoacaoResponse;
import br.umc.hemovida.excecao.DoacaoNaoPermitidaException;
import br.umc.hemovida.excecao.RecursoNaoEncontradoException;
import br.umc.hemovida.model.Doacao;
import br.umc.hemovida.model.Doador;
import br.umc.hemovida.repository.DoacaoRepository;
import br.umc.hemovida.repository.DoadorRepository;
import br.umc.hemovida.service.politica.PoliticaDoacaoFactory;

@Service
public class DoacaoService {

    private final DoadorRepository doadorRepository;
    private final DoacaoRepository doacaoRepository;
    private final AptidaoService aptidaoService;
    private final Clock relogio;

    public DoacaoService(DoadorRepository doadorRepository, DoacaoRepository doacaoRepository,
            AptidaoService aptidaoService, Clock relogio) {
        this.doadorRepository = doadorRepository;
        this.doacaoRepository = doacaoRepository;
        this.aptidaoService = aptidaoService;
        this.relogio = relogio;
    }

    @Transactional
    public DoacaoResponse registrar(Long idDoador, DoacaoRequest pedido) {
        Doador doador = buscarDoador(idDoador);
        Aptidao aptidao = aptidaoService.verificar(doador, pedido);
        if (!aptidao.apto()) {
            throw new DoacaoNaoPermitidaException(aptidao);
        }
        LocalDate hoje = LocalDate.now(relogio);
        Doacao doacao = Doacao.builder()
                .doador(doador)
                .dataDoacao(hoje)
                .numeroBolsa(proximoNumeroBolsa(hoje.getYear()))
                .volumeEmMililitros(pedido.volumeEmMililitros())
                .observacoes(pedido.observacoes())
                .pressaoArterial(pedido.pressaoArterial())
                .hemoglobina(pedido.hemoglobina())
                .build();
        doador.getDoacoes().add(doacao);
        int intervalo = PoliticaDoacaoFactory.para(doador.getSexo()).intervaloMinimoEmDias();
        doador.atualizarAposDoacao(pedido.pesoEmQuilos(), hoje.plusDays(intervalo));
        return DoacaoResponse.de(doacaoRepository.save(doacao));
    }

    @Transactional(readOnly = true)
    public List<DoacaoResponse> listarDoDoador(Long idDoador) {
        buscarDoador(idDoador);
        return doacaoRepository.findByDoadorIdOrderByDataDoacaoDesc(idDoador).stream()
                .map(DoacaoResponse::de)
                .toList();
    }

    @Transactional(readOnly = true)
    public DoacaoResponse buscar(Long idDoacao) {
        return doacaoRepository.findById(idDoacao)
                .map(DoacaoResponse::de)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Doação não encontrada"));
    }

    @Transactional(readOnly = true)
    public Aptidao consultarAptidao(Long idDoador) {
        Doador doador = buscarDoador(idDoador);
        Double peso = doador.getFichaClinica() == null ? null : doador.getFichaClinica().getPesoEmQuilos();
        return aptidaoService.verificar(doador, new DoacaoRequest(peso, null, null));
    }

    private Doador buscarDoador(Long idDoador) {
        return doadorRepository.findById(idDoador)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Doador não encontrado"));
    }

    // shortcut: duas requisições simultâneas podem gerar o mesmo número e a restrição de unicidade rejeita uma; rodando local, aceitável.
    private String proximoNumeroBolsa(int ano) {
        String prefixo = "HV-" + ano + "-";
        int ultimo = doacaoRepository.findTopByNumeroBolsaStartingWithOrderByNumeroBolsaDesc(prefixo)
                .map(doacao -> Integer.parseInt(doacao.getNumeroBolsa().substring(prefixo.length())))
                .orElse(0);
        return String.format("%s%06d", prefixo, ultimo + 1);
    }
}
