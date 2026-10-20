package br.umc.hemovida.dto;

import java.time.LocalDate;

import br.umc.hemovida.model.Doador;
import br.umc.hemovida.model.FatorRh;
import br.umc.hemovida.model.StatusDoador;
import br.umc.hemovida.model.TipoSanguineo;

/** Linha da listagem: o CPF sai mascarado e o status é o de {@code statusEm(hoje)}. */
public record DoadorResumo(Long id, String nome, String cpfMascarado, TipoSanguineo tipoSanguineo, FatorRh fatorRh,
        StatusDoador status, LocalDate aptoAPartirDe) {

    public static DoadorResumo de(Doador doador, LocalDate hoje) {
        StatusDoador status = doador.statusEm(hoje);
        String cpf = doador.getCpf().numero();
        return new DoadorResumo(doador.getId(), doador.getNome(), cpf.substring(0, 3) + ".***.***-" + cpf.substring(9),
                doador.getTipoSanguineo(), doador.getFatorRh(), status, aptoAPartirDe(doador, status));
    }

    static LocalDate aptoAPartirDe(Doador doador, StatusDoador status) {
        return status == StatusDoador.INAPTO_TEMPORARIO ? doador.getFichaClinica().getDataProximaDoacao() : null;
    }
}
