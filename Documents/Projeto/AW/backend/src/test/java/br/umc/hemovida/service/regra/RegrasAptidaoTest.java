package br.umc.hemovida.service.regra;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import br.umc.hemovida.dto.DoacaoRequest;
import br.umc.hemovida.model.Cpf;
import br.umc.hemovida.model.Doacao;
import br.umc.hemovida.model.Doador;
import br.umc.hemovida.model.FatorRh;
import br.umc.hemovida.model.FichaClinica;
import br.umc.hemovida.model.Sexo;
import br.umc.hemovida.model.TipoSanguineo;

public class RegrasAptidaoTest {

    public static final LocalDate HOJE = LocalDate.of(2026, 9, 20);
    public static final DoacaoRequest PEDIDO = new DoacaoRequest(70.0, 450, null);

    public static Doador doador(Sexo sexo, LocalDate nascimento, FichaClinica ficha) {
        return new Doador("Teste", new Cpf("52998224725"), nascimento, sexo, TipoSanguineo.O,
                FatorRh.POSITIVO, null, ficha);
    }

    public static Doador adulto(Sexo sexo) {
        return doador(sexo, LocalDate.of(1990, 1, 1), new FichaClinica(70.0, false, null));
    }

    public static Doador comDoacoes(Doador doador, LocalDate... datas) {
        for (LocalDate data : datas) {
            doador.getDoacoes().add(Doacao.builder().doador(doador).dataDoacao(data)
                    .numeroBolsa("HV-2026-" + data).volumeEmMililitros(450).build());
        }
        return doador;
    }

    @Nested
    class Idade {
        RegraIdade regra = new RegraIdade();

        @Test
        void menorDe16EhTemporarioAteOAniversario() {
            LocalDate nascimento = HOJE.minusYears(15);
            Impedimento impedimento = regra.verificar(doador(Sexo.MASCULINO, nascimento, null), PEDIDO, HOJE).orElseThrow();
            assertEquals(nascimento.plusYears(16), impedimento.liberadoEm());
            assertFalse(impedimento.definitivo());
        }

        @Test
        void acimaDe69EhDefinitivo() {
            assertTrue(regra.verificar(doador(Sexo.MASCULINO, HOJE.minusYears(70), null), PEDIDO, HOJE).orElseThrow().definitivo());
        }

        @Test
        void entre16E69EhLiberado() {
            assertTrue(regra.verificar(doador(Sexo.MASCULINO, HOJE.minusYears(16), null), PEDIDO, HOJE).isEmpty());
            assertTrue(regra.verificar(doador(Sexo.MASCULINO, HOJE.minusYears(69), null), PEDIDO, HOJE).isEmpty());
        }
    }

    @Nested
    class PrimeiraDoacao {
        RegraPrimeiraDoacao regra = new RegraPrimeiraDoacao();

        @Test
        void acimaDe60SemHistoricoEhDefinitivo() {
            Doador doador = doador(Sexo.MASCULINO, HOJE.minusYears(61), null);
            assertTrue(regra.verificar(doador, PEDIDO, HOJE).orElseThrow().definitivo());
        }

        @Test
        void acimaDe60ComHistoricoOuAte60EhLiberado() {
            Doador comHistorico = comDoacoes(doador(Sexo.MASCULINO, HOJE.minusYears(65), null), HOJE.minusYears(2));
            assertTrue(regra.verificar(comHistorico, PEDIDO, HOJE).isEmpty());
            assertTrue(regra.verificar(doador(Sexo.MASCULINO, HOJE.minusYears(60), null), PEDIDO, HOJE).isEmpty());
        }
    }

    @Nested
    class Peso {
        RegraPeso regra = new RegraPeso();

        @Test
        void abaixoDe50Impede() {
            assertTrue(regra.verificar(adulto(Sexo.FEMININO), new DoacaoRequest(49.9, 450, null), HOJE).isPresent());
            assertTrue(regra.verificar(adulto(Sexo.FEMININO), new DoacaoRequest(null, 450, null), HOJE).isPresent());
        }

        @Test
        void a50OuMaisLibera() {
            assertTrue(regra.verificar(adulto(Sexo.FEMININO), new DoacaoRequest(50.0, 450, null), HOJE).isEmpty());
        }
    }

    @Nested
    class ImpedimentoDefinitivo {
        RegraImpedimento regra = new RegraImpedimento();

        @Test
        void fichaComImpedimentoEhDefinitivo() {
            Doador doador = doador(Sexo.MASCULINO, LocalDate.of(1990, 1, 1), new FichaClinica(70.0, true, null));
            assertTrue(regra.verificar(doador, PEDIDO, HOJE).orElseThrow().definitivo());
        }

        @Test
        void semImpedimentoOuSemFichaEhLiberado() {
            assertTrue(regra.verificar(adulto(Sexo.MASCULINO), PEDIDO, HOJE).isEmpty());
            assertTrue(regra.verificar(doador(Sexo.MASCULINO, LocalDate.of(1990, 1, 1), null), PEDIDO, HOJE).isEmpty());
        }
    }

    @Nested
    class Tatuagem {
        RegraTatuagem regra = new RegraTatuagem();

        @Test
        void tatuagemComMenosDe12MesesImpedeAte12MesesDepois() {
            Doador doador = doador(Sexo.MASCULINO, LocalDate.of(1990, 1, 1),
                    new FichaClinica(70.0, false, null, LocalDate.of(2026, 1, 10)));
            assertEquals(LocalDate.of(2027, 1, 10), regra.verificar(doador, PEDIDO, HOJE).orElseThrow().liberadoEm());
        }

        @Test
        void tatuagemHaExatos12MesesLibera() {
            Doador doador = doador(Sexo.MASCULINO, LocalDate.of(1990, 1, 1),
                    new FichaClinica(70.0, false, null, HOJE.minusMonths(12)));
            assertTrue(regra.verificar(doador, PEDIDO, HOJE).isEmpty());
        }
    }

    @Nested
    class Intervalo {
        RegraIntervalo regra = new RegraIntervalo();

        @Test
        void mulherPrecisaEsperar90Dias() {
            Doador doador = comDoacoes(adulto(Sexo.FEMININO), LocalDate.of(2026, 7, 10));
            assertEquals(LocalDate.of(2026, 10, 8), regra.verificar(doador, PEDIDO, HOJE).orElseThrow().liberadoEm());
            assertTrue(regra.verificar(doador, PEDIDO, LocalDate.of(2026, 10, 8)).isEmpty());
        }

        @Test
        void homemPrecisaEsperar60Dias() {
            Doador doador = comDoacoes(adulto(Sexo.MASCULINO), LocalDate.of(2026, 7, 10));
            assertTrue(regra.verificar(doador, PEDIDO, HOJE).isEmpty());
            assertEquals(LocalDate.of(2026, 9, 8),
                    regra.verificar(doador, PEDIDO, LocalDate.of(2026, 9, 7)).orElseThrow().liberadoEm());
        }

        @Test
        void semHistoricoEhLiberado() {
            assertTrue(regra.verificar(adulto(Sexo.FEMININO), PEDIDO, HOJE).isEmpty());
        }
    }

    @Nested
    class LimiteAnual {
        RegraLimiteAnual regra = new RegraLimiteAnual();

        @Test
        void mulherNoLimiteDe3EsperaASaidaDaDoacaoMaisAntigaDaJanela() {
            Doador doador = comDoacoes(adulto(Sexo.FEMININO),
                    LocalDate.of(2025, 11, 1), LocalDate.of(2026, 2, 1), LocalDate.of(2026, 6, 1));
            Optional<Impedimento> impedimento = regra.verificar(doador, PEDIDO, HOJE);
            assertEquals(LocalDate.of(2026, 11, 1), impedimento.orElseThrow().liberadoEm());
        }

        @Test
        void doacaoForaDaJanelaNaoContaEAbaixoDoLimiteLibera() {
            Doador doador = comDoacoes(adulto(Sexo.FEMININO),
                    LocalDate.of(2025, 9, 20), LocalDate.of(2026, 2, 1), LocalDate.of(2026, 6, 1));
            assertTrue(regra.verificar(doador, PEDIDO, HOJE).isEmpty());
        }

        @Test
        void homemComTresDoacoesAindaPodeDoar() {
            Doador doador = comDoacoes(adulto(Sexo.MASCULINO),
                    LocalDate.of(2025, 11, 1), LocalDate.of(2026, 2, 1), LocalDate.of(2026, 6, 1));
            assertTrue(regra.verificar(doador, PEDIDO, HOJE).isEmpty());
        }
    }
}
