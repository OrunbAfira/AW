package br.umc.hemovida.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class CpfTest {

    @Test
    void aceitaCpfValidoComOuSemMascara() {
        assertEquals("52998224725", new Cpf("529.982.247-25").numero());
    }

    @Test
    void rejeitaCpfInvalido() {
        assertThrows(IllegalArgumentException.class, () -> new Cpf("52998224726"));
        assertThrows(IllegalArgumentException.class, () -> new Cpf("11111111111"));
        assertThrows(IllegalArgumentException.class, () -> new Cpf("123"));
        assertThrows(IllegalArgumentException.class, () -> new Cpf(null));
    }
}
