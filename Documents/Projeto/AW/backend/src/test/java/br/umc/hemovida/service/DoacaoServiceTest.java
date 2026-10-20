package br.umc.hemovida.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Clock;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import br.umc.hemovida.dto.DoacaoRequest;
import br.umc.hemovida.dto.PainelResponse;
import br.umc.hemovida.excecao.DoacaoNaoPermitidaException;
import br.umc.hemovida.model.StatusDoador;
import br.umc.hemovida.repository.DoadorRepository;

/** Usa os 15 doadores do data.sql: 1 = Ana Souza (bloqueada pelo intervalo), 3 = Carla Mendes (apta), 12 = Lucas Ferreira (apto). */
@SpringBootTest
@Transactional
class DoacaoServiceTest {

    @Autowired DoacaoService doacaoService;
    @Autowired PainelService painelService;
    @Autowired DoadorRepository doadorRepository;
    @Autowired Clock relogio;

    @Test
    void doadoraBloqueadaPeloIntervaloRecebeProximaDataApta() {
        LocalDate hoje = LocalDate.now(relogio);

        DoacaoNaoPermitidaException excecao = assertThrows(DoacaoNaoPermitidaException.class,
                () -> doacaoService.registrar(1L, new DoacaoRequest(62.0, 450, null)));

        assertEquals(hoje.plusDays(60), excecao.getAptidao().proximaDataApta());
    }

    @Test
    void doacaoAptaGeraBolsaSequencialEBloqueiaODoador() {
        LocalDate hoje = LocalDate.now(relogio);

        String primeira = doacaoService.registrar(3L, new DoacaoRequest(59.0, 450, null)).numeroBolsa();
        String segunda = doacaoService.registrar(12L, new DoacaoRequest(80.0, 450, null)).numeroBolsa();

        assertEquals("HV-" + hoje.getYear() + "-000017", primeira);
        assertEquals("HV-" + hoje.getYear() + "-000018", segunda);
        assertEquals(StatusDoador.INAPTO_TEMPORARIO, doadorRepository.findById(3L).orElseThrow().statusEm(hoje));
        assertEquals(hoje.plusDays(90), doadorRepository.findById(3L).orElseThrow().getFichaClinica().getDataProximaDoacao());
    }

    @Test
    void painelConsideraOsDadosDeExemplo() {
        PainelResponse painel = painelService.montar();

        assertEquals(15, painel.totalDoadores());
        // Aptos pela ficha: todos, menos Ana, Diego, Elisa (definitivo), Gabriela e Isabela (próxima doação futura).
        assertEquals(10, painel.aptosHoje());
    }
}
