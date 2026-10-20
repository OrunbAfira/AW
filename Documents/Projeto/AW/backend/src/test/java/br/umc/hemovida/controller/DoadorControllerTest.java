package br.umc.hemovida.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

/** Doadores do data.sql: 1 = Ana Souza, 4 = Diego (tatuagem recente), 5/12/13 = AB. */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class DoadorControllerTest {

    private static final String CPF_VALIDO_NOVO = "52998224725";

    @Autowired MockMvc mockMvc;

    private static String corpo(String cpf, String telefone) {
        return """
                {"nome":"Zeferina Silva","cpf":"%s","dataNascimento":"1990-01-01","sexo":"FEMININO",
                 "tipoSanguineo":"O","fatorRh":"POSITIVO","telefone":%s,
                 "fichaClinica":{"pesoEmQuilos":60,"impedimentoDefinitivo":false,"dataUltimaTatuagem":null}}
                """.formatted(cpf, telefone);
    }

    @Test
    void listaTodosComCpfMascaradoEStatus() throws Exception {
        mockMvc.perform(get("/api/v1/doadores").header(HttpHeaders.AUTHORIZATION, TokenDeTeste.atendente(mockMvc)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(15))
                .andExpect(jsonPath("$[0].cpfMascarado").value(org.hamcrest.Matchers.matchesPattern("\\d{3}\\.\\*\\*\\*\\.\\*\\*\\*-\\d{2}")));
    }

    @Test
    void filtraPorNomeETipoSanguineo() throws Exception {
        String autorizacao = TokenDeTeste.atendente(mockMvc);
        mockMvc.perform(get("/api/v1/doadores").param("busca", "ana souza").header(HttpHeaders.AUTHORIZATION, autorizacao))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value("INAPTO_TEMPORARIO"))
                .andExpect(jsonPath("$[0].aptoAPartirDe").isNotEmpty());
        mockMvc.perform(get("/api/v1/doadores").param("tipoSanguineo", "AB").header(HttpHeaders.AUTHORIZATION, autorizacao))
                .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    void cadastraEBuscaPorCpfComMascara() throws Exception {
        String autorizacao = TokenDeTeste.atendente(mockMvc);
        mockMvc.perform(post("/api/v1/doadores").contentType(MediaType.APPLICATION_JSON)
                .content(corpo(CPF_VALIDO_NOVO, "\"11987654321\"")).header(HttpHeaders.AUTHORIZATION, autorizacao))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("APTO"));
        mockMvc.perform(get("/api/v1/doadores").param("busca", "529.982.247").header(HttpHeaders.AUTHORIZATION, autorizacao))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nome").value("Zeferina Silva"));
    }

    @Test
    void errosDeCampoVoltamPorCampo() throws Exception {
        String autorizacao = TokenDeTeste.atendente(mockMvc);
        mockMvc.perform(post("/api/v1/doadores").contentType(MediaType.APPLICATION_JSON)
                .content(corpo("11111111111", "\"123\"")).header(HttpHeaders.AUTHORIZATION, autorizacao))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.telefone").value("Telefone deve ter 10 ou 11 dígitos"));
        mockMvc.perform(post("/api/v1/doadores").contentType(MediaType.APPLICATION_JSON)
                .content(corpo("11111111111", "null")).header(HttpHeaders.AUTHORIZATION, autorizacao))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.cpf").value("CPF inválido"));
    }

    @Test
    void cpfJaCadastradoEhRejeitadoMasPodeSerMantidoNaEdicao() throws Exception {
        String autorizacao = TokenDeTeste.atendente(mockMvc);
        String cpfDeAna = com.jayway.jsonpath.JsonPath.read(
                mockMvc.perform(get("/api/v1/doadores/1").header(HttpHeaders.AUTHORIZATION, autorizacao))
                        .andReturn().getResponse().getContentAsString(), "$.cpf");

        mockMvc.perform(post("/api/v1/doadores").contentType(MediaType.APPLICATION_JSON)
                .content(corpo(cpfDeAna, "null")).header(HttpHeaders.AUTHORIZATION, autorizacao))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.cpf").value("CPF já cadastrado"));
        mockMvc.perform(put("/api/v1/doadores/1").contentType(MediaType.APPLICATION_JSON)
                .content(corpo(cpfDeAna, "null")).header(HttpHeaders.AUTHORIZATION, autorizacao))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Zeferina Silva"));
    }

    @Test
    void doadorInexistenteRetorna404() throws Exception {
        mockMvc.perform(get("/api/v1/doadores/999").header(HttpHeaders.AUTHORIZATION, TokenDeTeste.atendente(mockMvc)))
                .andExpect(status().isNotFound());
    }
}
