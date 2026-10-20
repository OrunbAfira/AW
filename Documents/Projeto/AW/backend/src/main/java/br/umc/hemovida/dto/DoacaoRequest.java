package br.umc.hemovida.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

public record DoacaoRequest(
        @NotNull(message = "Peso é obrigatório") @Positive(message = "Peso deve ser maior que zero") Double pesoEmQuilos,
        @NotNull(message = "Volume é obrigatório") @Positive(message = "Volume deve ser maior que zero") Integer volumeEmMililitros,
        String observacoes,
        @Pattern(regexp = "\\d{2,3}/\\d{2,3}", message = "Pressão deve ter o formato 120/80") String pressaoArterial,
        @Positive(message = "Hemoglobina deve ser maior que zero") Double hemoglobina) {

    public DoacaoRequest(Double pesoEmQuilos, Integer volumeEmMililitros, String observacoes) {
        this(pesoEmQuilos, volumeEmMililitros, observacoes, null, null);
    }
}
