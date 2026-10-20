package br.umc.hemovida.service;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import br.umc.hemovida.dto.LoginRequest;
import br.umc.hemovida.dto.LoginResponse;

@Service
public class AutenticacaoService {

    static final Duration VALIDADE = Duration.ofHours(1);

    private final AuthenticationManager gerenciador;
    private final JwtEncoder codificador;
    private final Clock relogio;

    public AutenticacaoService(AuthenticationManager gerenciador, JwtEncoder codificador, Clock relogio) {
        this.gerenciador = gerenciador;
        this.codificador = codificador;
        this.relogio = relogio;
    }

    /** Falha com BadCredentialsException, sem distinguir usuário inexistente de senha errada. */
    public LoginResponse entrar(LoginRequest pedido) {
        Authentication autenticacao = gerenciador
                .authenticate(UsernamePasswordAuthenticationToken.unauthenticated(pedido.usuario(), pedido.senha()));
        String perfil = autenticacao.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");
        Instant emitidoEm = relogio.instant().truncatedTo(ChronoUnit.SECONDS);
        Instant expiraEm = emitidoEm.plus(VALIDADE);
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(autenticacao.getName())
                .issuedAt(emitidoEm)
                .expiresAt(expiraEm)
                .claim("perfil", perfil)
                .build();
        String token = codificador
                .encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims))
                .getTokenValue();
        return new LoginResponse(token, perfil, expiraEm);
    }
}
