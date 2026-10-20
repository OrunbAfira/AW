package br.umc.hemovida.dto;

import java.time.LocalDate;

import br.umc.hemovida.model.FatorRh;
import br.umc.hemovida.model.Sexo;
import br.umc.hemovida.model.TipoSanguineo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

public record DoadorRequest(
        @NotBlank(message = "Nome é obrigatório") String nome,
        @NotBlank(message = "CPF é obrigatório") String cpf,
        @NotNull(message = "Data de nascimento é obrigatória")
        @Past(message = "Data de nascimento deve estar no passado") LocalDate dataNascimento,
        @NotNull(message = "Sexo é obrigatório") Sexo sexo,
        @NotNull(message = "Tipo sanguíneo é obrigatório") TipoSanguineo tipoSanguineo,
        @NotNull(message = "Fator Rh é obrigatório") FatorRh fatorRh,
        @Pattern(regexp = "\\d{10,11}", message = "Telefone deve ter 10 ou 11 dígitos") String telefone,
        @NotNull(message = "Ficha clínica é obrigatória") @Valid FichaClinicaRequest fichaClinica) {

    public record FichaClinicaRequest(
            @NotNull(message = "Peso é obrigatório") @Positive(message = "Peso deve ser maior que zero") Double pesoEmQuilos,
            boolean impedimentoDefinitivo,
            @PastOrPresent(message = "Data da tatuagem não pode ser futura") LocalDate dataUltimaTatuagem) {
    }
}
