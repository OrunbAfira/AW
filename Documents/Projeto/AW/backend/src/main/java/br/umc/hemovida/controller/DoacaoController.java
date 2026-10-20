package br.umc.hemovida.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.umc.hemovida.dto.Aptidao;
import br.umc.hemovida.dto.DoacaoRequest;
import br.umc.hemovida.dto.DoacaoResponse;
import br.umc.hemovida.service.DoacaoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class DoacaoController {

    private final DoacaoService doacaoService;

    public DoacaoController(DoacaoService doacaoService) {
        this.doacaoService = doacaoService;
    }

    @PostMapping("/doadores/{id}/doacoes")
    @ResponseStatus(HttpStatus.CREATED)
    public DoacaoResponse registrar(@PathVariable Long id, @Valid @RequestBody DoacaoRequest pedido) {
        return doacaoService.registrar(id, pedido);
    }

    @GetMapping("/doadores/{id}/doacoes")
    public List<DoacaoResponse> listar(@PathVariable Long id) {
        return doacaoService.listarDoDoador(id);
    }

    @GetMapping("/doacoes/{id}")
    public DoacaoResponse buscar(@PathVariable Long id) {
        return doacaoService.buscar(id);
    }

    @GetMapping("/doadores/{id}/aptidao")
    public Aptidao aptidao(@PathVariable Long id) {
        return doacaoService.consultarAptidao(id);
    }
}
