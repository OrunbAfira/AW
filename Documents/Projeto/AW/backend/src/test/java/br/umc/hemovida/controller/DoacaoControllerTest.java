package br.umc.hemovida.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class DoacaoControllerTest {

    private static final String PEDIDO = "{\"pesoEmQuilos\":62,\"volumeEmMililitros\":450}";

    @Autowired MockMvc mockMvc;

    @Test
    void doadoraBloqueadaRecebe422ComProximaData() throws Exception {
        mockMvc.perform(post("/api/v1/doadores/1/doacoes").contentType(MediaType.APPLICATION_JSON).content(PEDIDO)
                .header(HttpHeaders.AUTHORIZATION, TokenDeTeste.atendente(mockMvc)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.apto").value(false))
                .andExpect(jsonPath("$.motivos[0]").exists())
                .andExpect(jsonPath("$.proximaDataApta").exists());
    }

    @Test
    void doadorAptoRegistraDoacaoEAparecEmListaEAptidao() throws Exception {
        String autorizacao = TokenDeTeste.atendente(mockMvc);

        mockMvc.perform(post("/api/v1/doadores/3/doacoes").contentType(MediaType.APPLICATION_JSON).content(PEDIDO)
                .header(HttpHeaders.AUTHORIZATION, autorizacao))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.numeroBolsa").value(org.hamcrest.Matchers.matchesPattern("HV-\\d{4}-\\d{6}")));
        mockMvc.perform(get("/api/v1/doadores/3/doacoes").header(HttpHeaders.AUTHORIZATION, autorizacao))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
        mockMvc.perform(get("/api/v1/doadores/3/aptidao").header(HttpHeaders.AUTHORIZATION, autorizacao))
                .andExpect(jsonPath("$.apto").value(false));
    }

    @Test
    void pedidoInvalidoRecebe400() throws Exception {
        mockMvc.perform(post("/api/v1/doadores/3/doacoes").contentType(MediaType.APPLICATION_JSON)
                .content("{\"volumeEmMililitros\":450}")
                .header(HttpHeaders.AUTHORIZATION, TokenDeTeste.atendente(mockMvc)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("Peso é obrigatório"));
    }
}
