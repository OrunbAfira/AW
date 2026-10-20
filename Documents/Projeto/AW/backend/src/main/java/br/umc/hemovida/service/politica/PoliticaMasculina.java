package br.umc.hemovida.service.politica;

public class PoliticaMasculina implements PoliticaDoacao {

    @Override
    public int intervaloMinimoEmDias() { return 60; }

    @Override
    public int limiteDoacoesEm12Meses() { return 4; }
}
