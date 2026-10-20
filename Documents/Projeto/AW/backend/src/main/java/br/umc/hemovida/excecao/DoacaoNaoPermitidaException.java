package br.umc.hemovida.excecao;

import br.umc.hemovida.dto.Aptidao;

public class DoacaoNaoPermitidaException extends RuntimeException {

    private final transient Aptidao aptidao;

    public DoacaoNaoPermitidaException(Aptidao aptidao) {
        super("Doação não permitida: " + String.join("; ", aptidao.motivos()));
        this.aptidao = aptidao;
    }

    public Aptidao getAptidao() {
        return aptidao;
    }
}
