package br.umc.hemovida.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import br.umc.hemovida.model.Cpf;
import br.umc.hemovida.model.Doador;
import br.umc.hemovida.model.FatorRh;
import br.umc.hemovida.model.Sexo;
import br.umc.hemovida.model.TipoSanguineo;

@DataJpaTest
class DoadorRepositoryTest {

    @Autowired
    DoadorRepository repositorio;

    @Test
    void filtraPorNomeOuCpfComTipoOpcional() {
        // Nome e CPF que não colidem com os doadores do data.sql.
        repositorio.save(new Doador("Zeferina Silva", new Cpf("52998224725"), LocalDate.of(1990, 1, 1),
                Sexo.FEMININO, TipoSanguineo.O, FatorRh.POSITIVO, null, null));

        assertEquals(1, repositorio.buscarPorNomeOuCpf("zeferina", "", null).size());
        assertEquals(1, repositorio.buscarPorNomeOuCpf("529.98", "52998", TipoSanguineo.O).size());
        assertEquals(0, repositorio.buscarPorNomeOuCpf("zeferina", "", TipoSanguineo.A).size());
    }
}
