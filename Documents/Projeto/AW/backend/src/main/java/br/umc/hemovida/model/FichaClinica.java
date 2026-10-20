package br.umc.hemovida.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class FichaClinica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double pesoEmQuilos;

    private boolean impedimentoDefinitivo;

    private LocalDate dataProximaDoacao;

    private LocalDate dataUltimaTatuagem;

    protected FichaClinica() {
    }

    public FichaClinica(Double pesoEmQuilos, boolean impedimentoDefinitivo, LocalDate dataProximaDoacao) {
        this(pesoEmQuilos, impedimentoDefinitivo, dataProximaDoacao, null);
    }

    public FichaClinica(Double pesoEmQuilos, boolean impedimentoDefinitivo, LocalDate dataProximaDoacao,
            LocalDate dataUltimaTatuagem) {
        this.pesoEmQuilos = pesoEmQuilos;
        this.impedimentoDefinitivo = impedimentoDefinitivo;
        this.dataProximaDoacao = dataProximaDoacao;
        this.dataUltimaTatuagem = dataUltimaTatuagem;
    }

    public void atualizarAposDoacao(Double pesoEmQuilos, LocalDate dataProximaDoacao) {
        this.pesoEmQuilos = pesoEmQuilos;
        this.dataProximaDoacao = dataProximaDoacao;
    }

    public void atualizarDadosClinicos(Double pesoEmQuilos, boolean impedimentoDefinitivo,
            LocalDate dataUltimaTatuagem) {
        this.pesoEmQuilos = pesoEmQuilos;
        this.impedimentoDefinitivo = impedimentoDefinitivo;
        this.dataUltimaTatuagem = dataUltimaTatuagem;
    }

    public Long getId() { return id; }

    public Double getPesoEmQuilos() { return pesoEmQuilos; }

    public boolean isImpedimentoDefinitivo() { return impedimentoDefinitivo; }

    public LocalDate getDataProximaDoacao() { return dataProximaDoacao; }

    public LocalDate getDataUltimaTatuagem() { return dataUltimaTatuagem; }
}
