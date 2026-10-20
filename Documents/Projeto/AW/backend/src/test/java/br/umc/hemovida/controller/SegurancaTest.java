package br.umc.hemovida.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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

/** Doadores do data.sql: 1 = Ana (com doações), 3 = Carla e 12 = Lucas (sem doações). */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SegurancaTest {

    @Autowired MockMvc mockMvc;

    @Test
    void semTokenRetorna401NoFormatoPadrao() throws Exception {
        mockMvc.perform(get("/api/v1/painel"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.mensagem").value("Autenticação necessária ou token inválido"));
    }

    @Test
    void tokenInvalidoRetorna401() throws Exception {
        mockMvc.perform(get("/api/v1/painel").header(HttpHeaders.AUTHORIZATION, "Bearer abc.def.ghi"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.mensagem").exists());
    }

    @Test
    void usuarioInexistenteESenhaErradaTemAMesmaMensagem() throws Exception {
        for (String corpo : new String[] {
                "{\"usuario\":\"ninguem\",\"senha\":\"qualquer\"}",
                "{\"usuario\":\"admin\",\"senha\":\"errada\"}",
                "{\"usuario\":\"\",\"senha\":\"\"}" }) {
            mockMvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON).content(corpo))
                    .andExpect(jsonPath("$.mensagem").value("Usuário ou senha inválidos"));
        }
        mockMvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"usuario\":\"admin\",\"senha\":\"errada\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginDevolveTokenPerfilEExpiracao() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"usuario\":\"atendente\",\"senha\":\"senha-atendente-teste\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.perfil").value("ATENDENTE"))
                .andExpect(jsonPath("$.expiraEm").isNotEmpty());
    }

    @Test
    void atendenteNaoExclui() throws Exception {
        mockMvc.perform(delete("/api/v1/doadores/3").header(HttpHeaders.AUTHORIZATION, TokenDeTeste.atendente(mockMvc)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.mensagem").value("Você não tem permissão para esta operação"));
    }

    @Test
    void adminExclui() throws Exception {
        mockMvc.perform(delete("/api/v1/doadores/3").header(HttpHeaders.AUTHORIZATION, TokenDeTeste.admin(mockMvc)))
                .andExpect(status().isNoContent());
    }

    @Test
    void adminNaoExcluiDoadorComDoacoes() throws Exception {
        mockMvc.perform(delete("/api/v1/doadores/1").header(HttpHeaders.AUTHORIZATION, TokenDeTeste.admin(mockMvc)))
                .andExpect(status().isConflict());
    }

    @Test
    void adminAcessaTudoDeAtendente() throws Exception {
        mockMvc.perform(get("/api/v1/painel").header(HttpHeaders.AUTHORIZATION, TokenDeTeste.admin(mockMvc)))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/painel").header(HttpHeaders.AUTHORIZATION, TokenDeTeste.atendente(mockMvc)))
                .andExpect(status().isOk());
    }

    @Test
    void swaggerEstaLiberadoSemToken() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.components.securitySchemes.bearer.scheme").value("bearer"));
    }
}
