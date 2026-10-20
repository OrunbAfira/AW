package br.umc.hemovida.service;

import static br.umc.hemovida.service.regra.RegrasAptidaoTest.PEDIDO;
import static br.umc.hemovida.service.regra.RegrasAptidaoTest.adulto;
import static br.umc.hemovida.service.regra.RegrasAptidaoTest.comDoacoes;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import org.junit.jupiter.api.Test;

import br.umc.hemovida.dto.Aptidao;
import br.umc.hemovida.model.Doador;
import br.umc.hemovida.model.FichaClinica;
import br.umc.hemovida.model.Sexo;
import br.umc.hemovida.service.regra.RegraImpedimento;
import br.umc.hemovida.service.regra.RegraIntervalo;
import br.umc.hemovida.service.regra.RegraLimiteAnual;
import br.umc.hemovida.service.regra.RegraTatuagem;

import static br.umc.hemovida.service.regra.RegrasAptidaoTest.doador;

class AptidaoServiceTest {

    private static final ZoneId FUSO = ZoneId.of("America/Sao_Paulo");

    private AptidaoService servicoEm(LocalDate hoje) {
        Clock relogio = Clock.fixed(hoje.atStartOfDay(FUSO).toInstant(), FUSO);
        return new AptidaoService(List.of(new RegraImpedimento(), new RegraTatuagem(), new RegraIntervalo(),
                new RegraLimiteAnual()), relogio);
    }

    @Test
    void exemploDaProposta_doacaoEm10Julho_tentativaEm20Setembro_bloqueadaAte8Outubro() {
        Doador doadora = comDoacoes(adulto(Sexo.FEMININO), LocalDate.of(2026, 7, 10));

        Aptidao aptidao = servicoEm(LocalDate.of(2026, 9, 20)).verificar(doadora, PEDIDO);

        assertFalse(aptidao.apto());
        assertEquals(1, aptidao.motivos().size());
        assertEquals(LocalDate.of(2026, 10, 8), aptidao.proximaDataApta());
    }

    @Test
    void semImpedimentosEhApto() {
        Aptidao aptidao = servicoEm(LocalDate.of(2026, 9, 20)).verificar(adulto(Sexo.MASCULINO), PEDIDO);

        assertTrue(aptidao.apto());
        assertTrue(aptidao.motivos().isEmpty());
        assertNull(aptidao.proximaDataApta());
    }

    @Test
    void acumulaMotivosEUsaAMaiorDataLiberada() {
        Doador doador = comDoacoes(
                doador(Sexo.FEMININO, LocalDate.of(1990, 1, 1),
                        new FichaClinica(70.0, false, null, LocalDate.of(2026, 8, 1))),
                LocalDate.of(2026, 7, 10));

        Aptidao aptidao = servicoEm(LocalDate.of(2026, 9, 20)).verificar(doador, PEDIDO);

        assertEquals(2, aptidao.motivos().size());
        assertEquals(LocalDate.of(2027, 8, 1), aptidao.proximaDataApta());
    }

    @Test
    void impedimentoDefinitivoNaoTemProximaData() {
        Doador doador = comDoacoes(
                doador(Sexo.FEMININO, LocalDate.of(1990, 1, 1), new FichaClinica(70.0, true, null)),
                LocalDate.of(2026, 7, 10));

        Aptidao aptidao = servicoEm(LocalDate.of(2026, 9, 20)).verificar(doador, PEDIDO);

        assertFalse(aptidao.apto());
        assertNull(aptidao.proximaDataApta());
    }
}
