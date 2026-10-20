package br.umc.hemovida.service.politica;

import br.umc.hemovida.model.Sexo;

public final class PoliticaDoacaoFactory {

    private PoliticaDoacaoFactory() {
    }

    public static PoliticaDoacao para(Sexo sexo) {
        return switch (sexo) {
            case MASCULINO -> new PoliticaMasculina();
            case FEMININO -> new PoliticaFeminina();
        };
    }
}
