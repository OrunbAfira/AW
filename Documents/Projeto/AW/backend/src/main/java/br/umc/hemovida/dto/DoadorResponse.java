package br.umc.hemovida.dto;

import java.time.LocalDate;

import br.umc.hemovida.model.Doador;
import br.umc.hemovida.model.FatorRh;
import br.umc.hemovida.model.FichaClinica;
import br.umc.hemovida.model.Sexo;
import br.umc.hemovida.model.StatusDoador;
import br.umc.hemovida.model.TipoSanguineo;

/** Detalhe completo, inclusive o CPF sem máscara (necessário para editar). Telefone só com dígitos. */
public record DoadorResponse(Long id, String nome, String cpf, LocalDate dataNascimento, Sexo sexo,
        TipoSanguineo tipoSanguineo, FatorRh fatorRh, String telefone, FichaClinicaResponse fichaClinica,
        StatusDoador status, LocalDate aptoAPartirDe) {

    public record FichaClinicaResponse(Double pesoEmQuilos, boolean impedimentoDefinitivo,
            LocalDate dataProximaDoacao, LocalDate dataUltimaTatuagem) {

        static FichaClinicaResponse de(FichaClinica ficha) {
            return ficha == null ? null : new FichaClinicaResponse(ficha.getPesoEmQuilos(),
                    ficha.isImpedimentoDefinitivo(), ficha.getDataProximaDoacao(), ficha.getDataUltimaTatuagem());
        }
    }

    public static DoadorResponse de(Doador doador, LocalDate hoje) {
        StatusDoador status = doador.statusEm(hoje);
        String telefone = doador.getTelefone() == null ? null : doador.getTelefone().replaceAll("\\D", "");
        return new DoadorResponse(doador.getId(), doador.getNome(), doador.getCpf().numero(),
                doador.getDataNascimento(), doador.getSexo(), doador.getTipoSanguineo(), doador.getFatorRh(),
                telefone, FichaClinicaResponse.de(doador.getFichaClinica()), status,
                DoadorResumo.aptoAPartirDe(doador, status));
    }
}
