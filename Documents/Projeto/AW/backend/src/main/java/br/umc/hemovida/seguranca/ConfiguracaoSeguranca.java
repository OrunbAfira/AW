package br.umc.hemovida.seguranca;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.nimbusds.jose.jwk.source.ImmutableSecret;

@Configuration
@EnableMethodSecurity
public class ConfiguracaoSeguranca {

    public static final String CAMINHO_LOGIN = "/api/v1/auth/login";

    @Bean
    SecurityFilterChain filtros(HttpSecurity http,
            @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolvedor) throws Exception {
        // Os erros 401 e 403 passam pelo GlobalExceptionHandler para terem o mesmo formato dos demais.
        AuthenticationEntryPoint naoAutenticado = (requisicao, resposta, excecao) -> resolvedor
                .resolveException(requisicao, resposta, null, excecao);
        AccessDeniedHandler semPermissao = (requisicao, resposta, excecao) -> resolvedor
                .resolveException(requisicao, resposta, null, excecao);

        JwtGrantedAuthoritiesConverter autoridades = new JwtGrantedAuthoritiesConverter();
        autoridades.setAuthoritiesClaimName("perfil");
        autoridades.setAuthorityPrefix("ROLE_");
        JwtAuthenticationConverter conversor = new JwtAuthenticationConverter();
        conversor.setJwtGrantedAuthoritiesConverter(autoridades);

        return http
                // Sem cookie nem sessão: o token vai no cabeçalho Authorization, então não há alvo para CSRF.
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sessao -> sessao.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(regras -> regras
                        .requestMatchers(CAMINHO_LOGIN, "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html")
                        .permitAll()
                        .anyRequest().authenticated())
                .oauth2ResourceServer(recurso -> recurso
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(conversor))
                        .authenticationEntryPoint(naoAutenticado)
                        .accessDeniedHandler(semPermissao))
                .exceptionHandling(erros -> erros
                        .authenticationEntryPoint(naoAutenticado)
                        .accessDeniedHandler(semPermissao))
                .build();
    }

    @Bean
    RoleHierarchy hierarquiaDePerfis() {
        return RoleHierarchyImpl.fromHierarchy("ROLE_ADMIN > ROLE_ATENDENTE");
    }

    @Bean
    PasswordEncoder codificadorDeSenha() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    UserDetailsService usuarios(PasswordEncoder codificador,
            @Value("${ADMIN_PASSWORD:}") String senhaAdmin,
            @Value("${ATENDENTE_PASSWORD:}") String senhaAtendente) {
        return new InMemoryUserDetailsManager(
                User.withUsername("admin").password(codificador.encode(obrigatorio("ADMIN_PASSWORD", senhaAdmin)))
                        .roles("ADMIN").build(),
                User.withUsername("atendente")
                        .password(codificador.encode(obrigatorio("ATENDENTE_PASSWORD", senhaAtendente)))
                        .roles("ATENDENTE").build());
    }

    @Bean
    AuthenticationManager gerenciadorDeAutenticacao(UserDetailsService usuarios, PasswordEncoder codificador) {
        DaoAuthenticationProvider provedor = new DaoAuthenticationProvider(usuarios);
        provedor.setPasswordEncoder(codificador);
        return new ProviderManager(provedor);
    }

    @Bean
    SecretKey chaveJwt(@Value("${JWT_SECRET:}") String segredo) {
        byte[] bytes = obrigatorio("JWT_SECRET", segredo).getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            throw new IllegalStateException("JWT_SECRET deve ter pelo menos 32 caracteres (HMAC-SHA256)");
        }
        return new SecretKeySpec(bytes, "HmacSHA256");
    }

    @Bean
    JwtEncoder codificadorJwt(SecretKey chaveJwt) {
        return new NimbusJwtEncoder(new ImmutableSecret<>(chaveJwt));
    }

    @Bean
    JwtDecoder decodificadorJwt(SecretKey chaveJwt) {
        return NimbusJwtDecoder.withSecretKey(chaveJwt).macAlgorithm(MacAlgorithm.HS256).build();
    }

    private static String obrigatorio(String nome, String valor) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalStateException(nome + " não configurada: defina em backend/.env (veja .env.example)");
        }
        return valor;
    }
}
