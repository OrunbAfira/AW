package br.umc.hemovida.controller;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.jayway.jsonpath.JsonPath;

/** Faz login de verdade e devolve o cabeçalho Authorization; as senhas vêm de src/test/resources. */
final class TokenDeTeste {

    private TokenDeTeste() {
    }

    static String admin(MockMvc mockMvc) throws Exception {
        return cabecalho(mockMvc, "admin", "senha-admin-teste");
    }

    static String atendente(MockMvc mockMvc) throws Exception {
        return cabecalho(mockMvc, "atendente", "senha-atendente-teste");
    }

    private static String cabecalho(MockMvc mockMvc, String usuario, String senha) throws Exception {
        String corpo = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"usuario\":\"" + usuario + "\",\"senha\":\"" + senha + "\"}"))
                .andReturn().getResponse().getContentAsString();
        return "Bearer " + JsonPath.read(corpo, "$.token");
    }
}
