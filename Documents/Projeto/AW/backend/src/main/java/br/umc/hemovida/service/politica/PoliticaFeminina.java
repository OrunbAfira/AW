package br.umc.hemovida.service.politica;

public class PoliticaFeminina implements PoliticaDoacao {

    @Override
    public int intervaloMinimoEmDias() { return 90; }

    @Override
    public int limiteDoacoesEm12Meses() { return 3; }
}
