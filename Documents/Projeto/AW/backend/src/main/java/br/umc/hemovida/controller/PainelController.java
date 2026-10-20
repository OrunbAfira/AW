package br.umc.hemovida.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.umc.hemovida.dto.PainelResponse;
import br.umc.hemovida.service.PainelService;

@RestController
@RequestMapping("/api/v1")
public class PainelController {

    private final PainelService painelService;

    public PainelController(PainelService painelService) {
        this.painelService = painelService;
    }

    @GetMapping("/painel")
    public PainelResponse painel() {
        return painelService.montar();
    }
}
