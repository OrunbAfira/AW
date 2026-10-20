package br.umc.hemovida.repository;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.dao.DataIntegrityViolationException;

import br.umc.hemovida.model.Cpf;
import br.umc.hemovida.model.Doador;
import br.umc.hemovida.model.FatorRh;
import br.umc.hemovida.model.Sexo;
import br.umc.hemovida.model.TipoSanguineo;

/** O H2 inclui o valor violado na mensagem de unicidade; ele não pode chegar ao log. */
@SpringBootTest
@ExtendWith(OutputCaptureExtension.class)
class LogSemCpfTest {

    @Autowired DoadorRepository repositorio;

    @Test
    void cpfDuplicadoNaoApareceNoLog(CapturedOutput saida) {
        Cpf cpfDeAna = repositorio.findById(1L).orElseThrow().getCpf();
        Doador repetido = new Doador("Repetido", cpfDeAna, LocalDate.of(1990, 1, 1), Sexo.FEMININO,
                TipoSanguineo.O, FatorRh.POSITIVO, null, null);

        assertThrows(DataIntegrityViolationException.class, () -> repositorio.saveAndFlush(repetido));

        assertFalse(saida.getAll().contains(cpfDeAna.numero()), "CPF apareceu no log");
    }
}
