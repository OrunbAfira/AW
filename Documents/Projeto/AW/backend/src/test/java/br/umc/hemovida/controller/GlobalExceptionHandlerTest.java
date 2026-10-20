package br.umc.hemovida.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;

class GlobalExceptionHandlerTest {

    @Test
    void violacaoDeUnicidadeVira409SemRepassarOValor() {
        var resposta = new GlobalExceptionHandler()
                .tratarIntegridade(new DataIntegrityViolationException("VALUES ( /* 1 */ '52998224725' )"));

        assertEquals(HttpStatus.CONFLICT, resposta.getStatusCode());
        assertFalse(resposta.getBody().get("mensagem").contains("52998224725"));
    }
}
