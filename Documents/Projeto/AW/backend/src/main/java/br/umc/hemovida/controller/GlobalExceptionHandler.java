package br.umc.hemovida.controller;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import br.umc.hemovida.dto.Aptidao;
import br.umc.hemovida.excecao.CampoInvalidoException;
import br.umc.hemovida.excecao.ConflitoException;
import br.umc.hemovida.excecao.DoacaoNaoPermitidaException;
import br.umc.hemovida.excecao.RecursoNaoEncontradoException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DoacaoNaoPermitidaException.class)
    public ResponseEntity<Aptidao> tratarDoacaoNaoPermitida(DoacaoNaoPermitidaException excecao) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(excecao.getAptidao());
    }

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<Map<String, String>> tratarNaoEncontrado(RecursoNaoEncontradoException excecao) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensagem", excecao.getMessage()));
    }

    @ExceptionHandler(ConflitoException.class)
    public ResponseEntity<Map<String, String>> tratarConflito(ConflitoException excecao) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("mensagem", excecao.getMessage()));
    }

    // Corrida entre duas requisições (CPF ou bolsa repetidos). A mensagem original traz o valor, então não é repassada.
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> tratarIntegridade(DataIntegrityViolationException excecao) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("mensagem", "Registro em conflito com outro já existente. Tente novamente."));
    }

    // Mensagem única para usuário inexistente e senha errada.
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, String>> tratarLoginInvalido(BadCredentialsException excecao) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("mensagem", "Usuário ou senha inválidos"));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, String>> tratarNaoAutenticado(AuthenticationException excecao) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("mensagem", "Autenticação necessária ou token inválido"));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> tratarAcessoNegado(AccessDeniedException excecao) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("mensagem", "Você não tem permissão para esta operação"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> tratarValidacao(MethodArgumentNotValidException excecao) {
        Map<String, String> campos = new LinkedHashMap<>();
        excecao.getBindingResult().getFieldErrors()
                .forEach(erro -> campos.putIfAbsent(erro.getField(), erro.getDefaultMessage()));
        String mensagem = campos.values().stream().distinct().collect(Collectors.joining("; "));
        return ResponseEntity.badRequest().body(Map.of("mensagem", mensagem, "campos", campos));
    }

    @ExceptionHandler(CampoInvalidoException.class)
    public ResponseEntity<Map<String, Object>> tratarCampoInvalido(CampoInvalidoException excecao) {
        return ResponseEntity.badRequest().body(Map.of("mensagem", excecao.getMessage(),
                "campos", Map.of(excecao.getCampo(), excecao.getMessage())));
    }
}
