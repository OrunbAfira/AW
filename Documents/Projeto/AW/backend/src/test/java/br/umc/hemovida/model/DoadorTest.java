package br.umc.hemovida.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class DoadorTest {

    private static final LocalDate HOJE = LocalDate.of(2026, 10, 19);

    private Doador doadorCom(FichaClinica ficha) {
        return new Doador("Maria", new Cpf("52998224725"), LocalDate.of(1990, 1, 1), Sexo.FEMININO,
                TipoSanguineo.O, FatorRh.POSITIVO, null, ficha);
    }

    @Test
    void impedimentoDefinitivoVenceQualquerData() {
        assertEquals(StatusDoador.INAPTO_DEFINITIVO,
                doadorCom(new FichaClinica(60.0, true, HOJE.minusDays(1))).statusEm(HOJE));
    }

    @Test
    void proximaDoacaoNoFuturoEhInaptoTemporario() {
        assertEquals(StatusDoador.INAPTO_TEMPORARIO,
                doadorCom(new FichaClinica(60.0, false, HOJE.plusDays(1))).statusEm(HOJE));
    }

    @Test
    void proximaDoacaoHojeOuPassadaOuAusenteEhApto() {
        assertEquals(StatusDoador.APTO, doadorCom(new FichaClinica(60.0, false, HOJE)).statusEm(HOJE));
        assertEquals(StatusDoador.APTO, doadorCom(new FichaClinica(60.0, false, null)).statusEm(HOJE));
    }
}
