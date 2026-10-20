package br.umc.hemovida.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.umc.hemovida.dto.DoadorRequest;
import br.umc.hemovida.dto.DoadorResponse;
import br.umc.hemovida.dto.DoadorResumo;
import br.umc.hemovida.model.TipoSanguineo;
import br.umc.hemovida.service.DoadorService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/doadores")
public class DoadorController {

    private final DoadorService doadorService;

    public DoadorController(DoadorService doadorService) {
        this.doadorService = doadorService;
    }

    @GetMapping
    public List<DoadorResumo> listar(@RequestParam(required = false) String busca,
            @RequestParam(required = false) TipoSanguineo tipoSanguineo) {
        return doadorService.listar(busca, tipoSanguineo);
    }

    @GetMapping("/{id}")
    public DoadorResponse buscar(@PathVariable Long id) {
        return doadorService.buscar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DoadorResponse criar(@Valid @RequestBody DoadorRequest pedido) {
        return doadorService.criar(pedido);
    }

    @PutMapping("/{id}")
    public DoadorResponse atualizar(@PathVariable Long id, @Valid @RequestBody DoadorRequest pedido) {
        return doadorService.atualizar(id, pedido);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        doadorService.excluir(id);
    }
}
